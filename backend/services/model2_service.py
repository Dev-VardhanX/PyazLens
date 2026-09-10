import torch
import torch.nn as nn

from pathlib import Path
from PIL import Image
from torchvision import models, transforms


# ============================================================
# MODEL 2 CONFIGURATION
# ============================================================

BASE_DIR = Path(__file__).resolve().parent.parent

MODEL2_PATH = (
    BASE_DIR
    / "model"
    / "model2"
    / "best.pt"
)

DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

DEFECT_THRESHOLD = 0.50


# ============================================================
# DEFECT CLASSES
# ============================================================

DEFECT_CLASSES = [
    "Rotten",
    "Sprouted",
    "Cut/Crack",
    "Skin Damage",
    "Sunburned",
    "Misshapen"
]


# ============================================================
# IMAGE PREPROCESSING
# ============================================================

transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize(
        mean=[0.485, 0.456, 0.406],
        std=[0.229, 0.224, 0.225]
    )
])


# ============================================================
# LOAD MODEL
# ============================================================

print(f"Loading Model 2 from: {MODEL2_PATH}")
print(f"Model 2 device: {DEVICE}")


model2 = models.efficientnet_b0(
    weights=None
)


# Replace final classifier
model2.classifier[1] = nn.Linear(
    model2.classifier[1].in_features,
    6
)


# Load trained weights
state_dict = torch.load(
    MODEL2_PATH,
    map_location=DEVICE
)

model2.load_state_dict(
    state_dict
)


model2 = model2.to(DEVICE)

model2.eval()


print("Model 2 loaded successfully.")


# ============================================================
# PREDICT DEFECTS
# ============================================================

def predict_defects(image):
    image = image.convert("RGB")

    input_tensor = transform(image).unsqueeze(0).to(DEVICE)

    with torch.no_grad():
        outputs = model2(input_tensor)

    probabilities = torch.sigmoid(outputs)[0]

    # Store all six probabilities
    all_probabilities = {}

    defects = []

    for index, probability in enumerate(probabilities):

        confidence = float(probability.item())

        class_name = DEFECT_CLASSES[index]

        all_probabilities[class_name] = round(
            confidence,
            3
        )

        # Apply defect threshold
        if confidence >= DEFECT_THRESHOLD:

            defects.append({
                "name": class_name,
                "confidence": round(
                    confidence,
                    3
                )
            })

    # If no defect crosses threshold
    if len(defects) == 0:

        classification = "No Defect"

    else:

        classification = "Defective"

    return {
        "classification": classification,
        "defects": defects,
        "probabilities": all_probabilities
    }