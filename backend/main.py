from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from ultralytics import YOLO
from PIL import Image

from pathlib import Path

from services.model2_service import predict_defects
from services.grading_service import (
    grade_onion,
    summarize_batch
)

from database import (
    create_batch,
    create_inspection,
    create_detected_onion,
    create_onion_defect,
    create_user_profile,
    get_user_profile_by_phone,
    get_user_inspections,
    get_inspection_details,
    upload_inspection_image,
    update_inspection_image_url
)

import io
import cv2
import numpy as np
import torch


# ============================================================
# FASTAPI APP
# ============================================================

app = FastAPI(
    title="PyazLens Model 1 API",
    description="Onion detection and size estimation API",
    version="1.0.0"
)


# ============================================================
# MODEL
# ============================================================

BASE_DIR = Path(__file__).resolve().parent

MODEL_PATH = (
    BASE_DIR
    / "model"
    / "model1"
    / "best.pt"
)

model = YOLO(MODEL_PATH)


# ============================================================
# DEVICE
# ============================================================

DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

print(f"PyazLens Model 1 device: {DEVICE}")
print(f"Model path: {MODEL_PATH}")


# ============================================================
# CLASS NAMES
# ============================================================

print("Model classes:", model.names)


# Find classes automatically
CLASS_NAMES = {
    int(class_id): str(class_name).lower()
    for class_id, class_name in model.names.items()
}


ONION_CLASS_ID = next(
    (
        class_id
        for class_id, class_name in CLASS_NAMES.items()
        if class_name == "onion"
    ),
    None
)


COIN_CLASS_ID = next(
    (
        class_id
        for class_id, class_name in CLASS_NAMES.items()
        if class_name == "coin"
    ),
    None
)


if ONION_CLASS_ID is None:
    raise RuntimeError(
        f"Could not find 'onion' class in model classes: {model.names}"
    )


if COIN_CLASS_ID is None:
    raise RuntimeError(
        f"Could not find 'coin' class in model classes: {model.names}"
    )


print(f"Onion class ID: {ONION_CLASS_ID}")
print(f"Coin class ID: {COIN_CLASS_ID}")


# ============================================================
# CONSTANTS
# ============================================================

COIN_DIAMETER_MM = 27.0
ONION_MIN_CONFIDENCE = 0.50

# ============================================================
# CROP SETTINGS
# ============================================================

CROP_PADDING = 10


def crop_onion(image_np, bbox):
    """
    Crop one onion from the original image using its bbox.

    A small padding is added around the onion so Model 2
    gets a little surrounding context.
    """

    x1, y1, x2, y2 = bbox

    x1 = max(0, x1 - CROP_PADDING)
    y1 = max(0, y1 - CROP_PADDING)

    x2 = min(
        image_np.shape[1],
        x2 + CROP_PADDING
    )

    y2 = min(
        image_np.shape[0],
        y2 + CROP_PADDING
    )

    crop = image_np[
        y1:y2,
        x1:x2
    ]

    return crop

# ============================================================
# ROOT
# ============================================================

@app.get("/")
def root():
    return {
        "message": "PyazLens Model 1 API is running",
        "device": DEVICE
    }


# ============================================================
# MODEL 1 ANALYSIS
# ============================================================

def analyze_onion_image(image):

    # --------------------------------------------------------
    # Run YOLO
    # --------------------------------------------------------

    results = model.predict(
        source=image,
        imgsz=640,
        conf=0.25,
        device=DEVICE,
        verbose=False
    )

    result = results[0]


    # --------------------------------------------------------
    # Check segmentation
    # --------------------------------------------------------

    if result.masks is None or result.boxes is None:
        raise ValueError(
            "No segmentation objects detected."
        )


    # --------------------------------------------------------
    # Get classes and confidence
    # --------------------------------------------------------

    classes = (
        result.boxes.cls
        .cpu()
        .numpy()
        .astype(int)
    )

    confidences = (
        result.boxes.conf
        .cpu()
        .numpy()
    )


    # IMPORTANT:
    # result.masks.xy gives mask coordinates already
    # scaled to the original image dimensions.

    masks_xy = result.masks.xy


    # --------------------------------------------------------
    # Separate coin and onions
    # --------------------------------------------------------

    coin_masks = []
    onion_masks = []


    for mask_points, class_id, confidence in zip(
        masks_xy,
        classes,
        confidences
    ):

        if class_id == COIN_CLASS_ID:

            coin_masks.append(
                (mask_points, confidence)
            )

        elif class_id == ONION_CLASS_ID:

            if confidence >= ONION_MIN_CONFIDENCE:

                onion_masks.append(
                    (mask_points, confidence)
                )


    # --------------------------------------------------------
    # Check coin
    # --------------------------------------------------------

    if len(coin_masks) == 0:

        raise ValueError(
            "Coin was not detected. "
            "Please make sure the 27 mm reference coin is visible."
        )


    # --------------------------------------------------------
    # Select largest coin
    # --------------------------------------------------------

    coin_points, coin_confidence = max(
        coin_masks,
        key=lambda item: cv2.contourArea(
            item[0].astype(np.float32)
        )
    )


    # --------------------------------------------------------
    # Measure coin
    # --------------------------------------------------------

    coin_points = (
        coin_points
        .astype(np.float32)
    )


    coin_x_min = float(
        coin_points[:, 0].min()
    )

    coin_x_max = float(
        coin_points[:, 0].max()
    )

    coin_y_min = float(
        coin_points[:, 1].min()
    )

    coin_y_max = float(
        coin_points[:, 1].max()
    )


    coin_width_px = (
        coin_x_max - coin_x_min
    )

    coin_height_px = (
        coin_y_max - coin_y_min
    )


    coin_diameter_px = (
        coin_width_px +
        coin_height_px
    ) / 2


    if coin_diameter_px <= 0:

        raise ValueError(
            "Invalid coin measurement."
        )


    # --------------------------------------------------------
    # Pixel → millimetre conversion
    # --------------------------------------------------------

    mm_per_pixel = (
        COIN_DIAMETER_MM /
        coin_diameter_px
    )


    # --------------------------------------------------------
    # Measure onions
    # --------------------------------------------------------

    measurements = []


    for onion_id, (
        mask_points,
        confidence
    ) in enumerate(
        onion_masks,
        start=1
    ):

        points = (
            mask_points
            .astype(np.float32)
        )


        if len(points) < 3:
            continue


        # ----------------------------------------------------
        # Convex hull
        # ----------------------------------------------------

        hull = cv2.convexHull(
            points
        )


        hull_points = (
            hull.reshape(-1, 2)
            .astype(np.float32)
        )


        # ----------------------------------------------------
        # Maximum diameter
        # ----------------------------------------------------
        #
        # Instead of calculating distances between every
        # contour point, use the convex hull.
        #
        # This significantly reduces the number of points
        # for large/high-resolution images.
        #

        max_diameter_px = 0.0


        for i in range(
            len(hull_points)
        ):

            distances = np.sqrt(
                np.sum(
                    (
                        hull_points[i + 1:]
                        - hull_points[i]
                    ) ** 2,
                    axis=1
                )
            )


            if len(distances) > 0:

                max_diameter_px = max(
                    max_diameter_px,
                    float(distances.max())
                )


        # ----------------------------------------------------
        # Diameter in mm
        # ----------------------------------------------------

        diameter_mm = (
            max_diameter_px *
            mm_per_pixel
        )


        # ----------------------------------------------------
        # Bounding box
        # ----------------------------------------------------

        x, y, w, h = cv2.boundingRect(
            points.astype(np.int32)
        )


        width_mm = (
            w *
            mm_per_pixel
        )

        height_mm = (
            h *
            mm_per_pixel
        )


        # ----------------------------------------------------
        # Save measurement
        # ----------------------------------------------------

        measurements.append({

            "id": onion_id,

            "confidence": round(
                float(confidence),
                3
            ),

            "diameter_mm": round(
                float(diameter_mm),
                2
            ),

            "width_mm": round(
                float(width_mm),
                2
            ),

            "height_mm": round(
                float(height_mm),
                2
            ),

            "bbox": [
                int(x),
                int(y),
                int(x + w),
                int(y + h)
            ]
        })


    # --------------------------------------------------------
    # Final result
    # --------------------------------------------------------

    return {

        "coin_detected": True,

        "coin_confidence": round(
            float(coin_confidence),
            3
        ),

        "coin_diameter_px": round(
            float(coin_diameter_px),
            2
        ),

        "mm_per_pixel": round(
            float(mm_per_pixel),
            4
        ),

        "onion_count": len(
            measurements
        ),

        "onions": measurements
    }


# ============================================================
# API ENDPOINT
# ============================================================

@app.post("/detect")
async def detect_onions(
    file: UploadFile = File(...)
):

    # --------------------------------------------------------
    # Validate image
    # --------------------------------------------------------

    if (
        not file.content_type
        or not file.content_type.startswith("image/")
    ):

        raise HTTPException(
            status_code=400,
            detail="Please upload an image file."
        )


    try:

        # ----------------------------------------------------
        # Read uploaded image
        # ----------------------------------------------------

        image_bytes = await file.read()


        image = Image.open(
            io.BytesIO(image_bytes)
        ).convert("RGB")


        # ----------------------------------------------------
        # Run Model 1
        # ----------------------------------------------------

        result = analyze_onion_image(
            image
        )


        # ----------------------------------------------------
        # Return response
        # ----------------------------------------------------

        return {

            "success": True,

            "total_onions": result[
                "onion_count"
            ],

            "coin": {

                "detected": result[
                    "coin_detected"
                ],

                "confidence": result[
                    "coin_confidence"
                ],

                "diameter_px": result[
                    "coin_diameter_px"
                ]
            },

            "scale": {

                "mm_per_pixel": result[
                    "mm_per_pixel"
                ]
            },

            "onions": result[
                "onions"
            ]
        }


    except ValueError as e:

        raise HTTPException(
            status_code=400,
            detail=str(e)
        )


    except Exception as e:

        raise HTTPException(
            status_code=500,
            detail=f"Model 1 error: {str(e)}"
        )

# ============================================================
# CROP TEST ENDPOINT
# ============================================================

@app.post("/crops")
async def get_onion_crops(
    file: UploadFile = File(...)
):

    # --------------------------------------------------------
    # Validate image
    # --------------------------------------------------------

    if (
        not file.content_type
        or not file.content_type.startswith("image/")
    ):

        raise HTTPException(
            status_code=400,
            detail="Please upload an image file."
        )


    try:

        # ----------------------------------------------------
        # Read original image
        # ----------------------------------------------------

        image_bytes = await file.read()

        image = Image.open(
            io.BytesIO(image_bytes)
        ).convert("RGB")


        # ----------------------------------------------------
        # Convert to NumPy
        # ----------------------------------------------------

        image_np = np.array(image)


        # ----------------------------------------------------
        # Run Model 1
        # ----------------------------------------------------

        result = analyze_onion_image(
            image
        )


        # ----------------------------------------------------
        # Create crops
        # ----------------------------------------------------

        crops = []


        for onion in result["onions"]:

            crop = crop_onion(
                image_np,
                onion["bbox"]
            )


            if crop.size == 0:
                continue


            # ------------------------------------------------
            # Save temporary crop
            # ------------------------------------------------

            crop_filename = (
                f"onion_{onion['id']}.jpg"
            )

            crop_path = (
                BASE_DIR
                / crop_filename
            )


            crop_image = Image.fromarray(
                crop
            )

            crop_image.save(
                crop_path,
                quality=95
            )


            crops.append({

                "id": onion["id"],

                "confidence": onion[
                    "confidence"
                ],

                "bbox": onion[
                    "bbox"
                ],

                "crop_width": int(
                    crop.shape[1]
                ),

                "crop_height": int(
                    crop.shape[0]
                ),

                "crop_file": crop_filename
            })


        # ----------------------------------------------------
        # Return result
        # ----------------------------------------------------

        return {

            "success": True,

            "total_onions": len(crops),

            "crops": crops
        }


    except ValueError as e:

        raise HTTPException(
            status_code=400,
            detail=str(e)
        )


    except Exception as e:

        raise HTTPException(
            status_code=500,
            detail=f"Crop generation error: {str(e)}"
        )

# ============================================================
# COMBINED ANALYSIS ENDPOINT
# ============================================================

@app.post("/analyze")
async def analyze_batch(
    file: UploadFile = File(...),
    name: str = Form(...),
    phone: str | None = Form(None),
    address: str | None = Form(None)
):

    # --------------------------------------------------------
    # Validate image
    # --------------------------------------------------------

    if (
        not file.content_type
        or not file.content_type.startswith("image/")
    ):

        raise HTTPException(
            status_code=400,
            detail="Please upload an image file."
        )

    try:
        # ----------------------------------------------------
        # Get or create user profile
        # ----------------------------------------------------

        user_profile = None

        if phone:
            user_profile = get_user_profile_by_phone(phone)

        if user_profile is None:
            user_profile = create_user_profile(
                name=name,
                phone=phone,
                address=address
            )
        # ----------------------------------------------------
        # Read uploaded image
        # ----------------------------------------------------

        image_bytes = await file.read()

        image = Image.open(
            io.BytesIO(image_bytes)
        ).convert("RGB")


        # ----------------------------------------------------
        # Run Model 1
        # ----------------------------------------------------

        model1_result = analyze_onion_image(
            image
        )


        # ----------------------------------------------------
        # Convert original image to NumPy
        # ----------------------------------------------------

        image_np = np.array(image)


        # ----------------------------------------------------
        # Analyze every onion
        # ----------------------------------------------------

        onions = []


        for onion in model1_result["onions"]:

            # ------------------------------------------------
            # Crop onion
            # ------------------------------------------------

            crop = crop_onion(
                image_np,
                onion["bbox"]
            )


            if crop.size == 0:

                continue


            # ------------------------------------------------
            # Convert crop back to PIL
            # ------------------------------------------------

            crop_image = Image.fromarray(
                crop
            )


            # ------------------------------------------------
            # Run Model 2
            # ------------------------------------------------

            defect_result = predict_defects(
                crop_image
            )


            # ------------------------------------------------
            # Combine Model 1 + Model 2
            # ------------------------------------------------

            # --------------------------------------------------------
            # Create combined onion result
            # --------------------------------------------------------

            onion_result = {

                "id": onion["id"],

                "size": {
                    "diameter_mm": onion["diameter_mm"],
                    "width_mm": onion["width_mm"],
                    "height_mm": onion["height_mm"]
                },

                "classification": defect_result["classification"],

                "defects": defect_result["defects"],

                "probabilities": defect_result["probabilities"]
            }


            # --------------------------------------------------------
            # Apply grading
            # --------------------------------------------------------

            grading_result = grade_onion(
                onion_result
            )


            onion_result["grade"] = grading_result[
                "grade"
            ]

            onion_result["grade_reason"] = grading_result[
                "reason"
            ]


            # --------------------------------------------------------
            # Add onion to final results
            # --------------------------------------------------------

            onions.append(
                onion_result
            )


        # --------------------------------------------------------
        # Generate batch summary
        # --------------------------------------------------------

        batch_summary = summarize_batch(
            onions
        )

        # --------------------------------------------------------
        # Save results to Supabase
        # --------------------------------------------------------

        batch_code = f"PYAZ-{int(torch.randint(100000, 999999, (1,)).item())}"

        batch = create_batch(batch_code)

        image_path = upload_inspection_image(
            image_bytes,
            file.content_type
        )

        inspection = create_inspection({
            "user_profile_id": user_profile["id"],
            "batch_id": batch["id"],
            "input_type": "IMAGE",
            "image_url": image_path,
            "total_onions": len(onions),
            "healthy_count": batch_summary["grade_counts"]["Grade A"],
            "damaged_count": batch_summary["grade_counts"]["Grade URS"],
            "rotten_count": batch_summary["defect_summary"]["Rotten"],
            "sprouted_count": batch_summary["defect_summary"]["Sprouted"],
            "grade_a_count": batch_summary["grade_counts"]["Grade A"],
            "urs_count": batch_summary["grade_counts"]["Grade URS"],
            "rejected_count": batch_summary["grade_counts"]["REJECT"],
            "grade_a_percentage": batch_summary["grade_percentages"]["Grade A"],
            "urs_percentage": batch_summary["grade_percentages"]["Grade URS"],
            "rejected_percentage": batch_summary["grade_percentages"]["REJECT"],
            "cut_crack_count": batch_summary["defect_summary"]["Cut/Crack"],
            "skin_damage_count": batch_summary["defect_summary"]["Skin Damage"],
            "sunburned_count": batch_summary["defect_summary"]["Sunburned"],
            "misshapen_count": batch_summary["defect_summary"]["Misshapen"],
            "final_grade": (
                "Grade A"
                if batch_summary["grade_counts"]["Grade A"] > 0
                else "Grade URS"
                if batch_summary["grade_counts"]["Grade URS"] > 0
                else "REJECT"
            )
        })

        for onion in onions:

            detected_onion = create_detected_onion({
                "inspection_id": inspection["id"],
                "tracking_id": onion["id"],
                "diameter_mm": onion["size"]["diameter_mm"],
                "width_mm": onion["size"]["width_mm"],
                "height_mm": onion["size"]["height_mm"],
                "detection_confidence": None,
                "grade": onion["grade"],
                "grade_reason": onion["grade_reason"]
            })

            for defect in onion["defects"]:
                create_onion_defect({
                    "detected_onion_id": detected_onion["id"],
                    "defect_class": defect["name"],
                    "confidence": defect["confidence"]
                })


        # --------------------------------------------------------
        # Final response
        # --------------------------------------------------------

        return {

            "success": True,

            "total_onions": len(onions),

            "measurement": {
                "reference_coin_diameter_mm": 27.0,
                "mm_per_pixel": model1_result[
                    "mm_per_pixel"
                ]
            },

            "onions": onions,

            "summary": {

                "grade_a": batch_summary[
                    "grade_counts"
                ]["Grade A"],

                "grade_urs": batch_summary[
                    "grade_counts"
                ]["Grade URS"],

                "rejected": batch_summary[
                    "grade_counts"
                ]["REJECT"],

                "grade_a_percentage": batch_summary[
                    "grade_percentages"
                ]["Grade A"],

                "grade_urs_percentage": batch_summary[
                    "grade_percentages"
                ]["Grade URS"],

                "rejected_percentage": batch_summary[
                    "grade_percentages"
                ]["REJECT"]
            },

            "defect_summary": batch_summary[
                "defect_summary"
            ]
        }

    except ValueError as e:

        raise HTTPException(
            status_code=400,
            detail=str(e)
        )


    except Exception as e:

        raise HTTPException(
            status_code=500,
            detail=f"Combined analysis error: {str(e)}"

        )

# ============================================================
# USER INSPECTION HISTORY
# ============================================================

@app.get("/users/{user_profile_id}/inspections")
def get_user_history(user_profile_id: int):

    try:

        inspections = get_user_inspections(
            user_profile_id
        )

        return {
            "success": True,
            "user_profile_id": user_profile_id,
            "total_inspections": len(inspections),
            "inspections": inspections
        }

    except Exception as e:

        raise HTTPException(
            status_code=500,
            detail=f"Could not retrieve inspection history: {str(e)}"
        )

# ============================================================
# INSPECTION DETAILS
# ============================================================

@app.get("/inspections/{inspection_id}")
def get_inspection(inspection_id: int):

    try:

        inspection = get_inspection_details(
            inspection_id
        )

        if inspection is None:

            raise HTTPException(
                status_code=404,
                detail="Inspection not found."
            )

        return {
            "success": True,
            "inspection": inspection
        }

    except HTTPException:
        raise

    except Exception as e:

        raise HTTPException(
            status_code=500,
            detail=f"Could not retrieve inspection: {str(e)}"
        )

@app.post("/users")
async def create_user(
    name: str = Form(...),
    phone: str | None = Form(None),
    address: str | None = Form(None)
):
    existing_user = None

    if phone:
        existing_user = get_user_profile_by_phone(phone)

    if existing_user:
        return {
            "message": "User already exists",
            "user": existing_user
        }

    user = create_user_profile(
        name=name,
        phone=phone,
        address=address
    )

    return {
        "message": "User created successfully",
        "user": user
    }

@app.get("/users/{user_profile_id}/inspections")
async def user_inspection_history(user_profile_id: int):
    inspections = get_user_inspections(user_profile_id)

    return {
        "user_profile_id": user_profile_id,
        "inspections": inspections
    }