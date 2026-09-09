import os
from dotenv import load_dotenv
from supabase import create_client, Client
import uuid

load_dotenv()

SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_KEY")

if not SUPABASE_URL:
    raise RuntimeError("SUPABASE_URL is missing from .env")

if not SUPABASE_KEY:
    raise RuntimeError("SUPABASE_KEY is missing from .env")

supabase: Client = create_client(
    SUPABASE_URL,
    SUPABASE_KEY
)


def create_batch(batch_code):
    response = (
        supabase
        .table("batches")
        .insert({
            "batch_code": batch_code
        })
        .execute()
    )

    return response.data[0]


def create_inspection(inspection_data):
    response = (
        supabase
        .table("inspections")
        .insert(inspection_data)
        .execute()
    )

    return response.data[0]


def create_detected_onion(onion_data):
    response = (
        supabase
        .table("detected_onions")
        .insert(onion_data)
        .execute()
    )

    return response.data[0]


def create_onion_defect(defect_data):
    response = (
        supabase
        .table("onion_defects")
        .insert(defect_data)
        .execute()
    )

    return response.data[0]

def create_user_profile(name, phone=None, address=None):
    response = (
        supabase
        .table("user_profiles")
        .insert({
            "name": name,
            "phone": phone,
            "address": address,
            "phone_verified": False
        })
        .execute()
    )

    return response.data[0]


def get_user_profile_by_phone(phone):
    response = (
        supabase
        .table("user_profiles")
        .select("*")
        .eq("phone", phone)
        .limit(1)
        .execute()
    )

    if response.data:
        return response.data[0]

    return None

def get_user_inspections(user_profile_id):
    response = (
        supabase
        .table("inspections")
        .select("*")
        .eq("user_profile_id", user_profile_id)
        .order("created_at", desc=True)
        .execute()
    )

    return response.data


def get_inspection_details(inspection_id):
    inspection_response = (
        supabase
        .table("inspections")
        .select("*")
        .eq("id", inspection_id)
        .limit(1)
        .execute()
    )

    if not inspection_response.data:
        return None

    inspection = inspection_response.data[0]

    onions_response = (
        supabase
        .table("detected_onions")
        .select("*")
        .eq("inspection_id", inspection_id)
        .order("tracking_id")
        .execute()
    )

    onions = onions_response.data

    for onion in onions:
        defects_response = (
            supabase
            .table("onion_defects")
            .select("*")
            .eq("detected_onion_id", onion["id"])
            .execute()
        )

        onion["defects"] = defects_response.data

    inspection["onions"] = onions

    return inspection

def upload_inspection_image(image_bytes, content_type="image/jpeg"):
    file_extension = "jpg"

    if content_type == "image/png":
        file_extension = "png"
    elif content_type == "image/webp":
        file_extension = "webp"

    file_path = f"{uuid.uuid4()}.{file_extension}"

    supabase.storage.from_("inspection-images").upload(
        file_path,
        image_bytes,
        {
            "content-type": content_type,
            "upsert": "false"
        }
    )

    return file_path

def update_inspection_image_url(inspection_id, image_path):
    response = (
        supabase
        .table("inspections")
        .update({
            "image_url": image_path
        })
        .eq("id", inspection_id)
        .execute()
    )

    return response.data[0]