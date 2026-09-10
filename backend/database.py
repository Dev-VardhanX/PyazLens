import os
import hashlib
import secrets
from datetime import datetime, timedelta, timezone
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

def delete_inspection_record(inspection_id):
    # First check that the inspection exists
    inspection_response = (
        supabase
        .table("inspections")
        .select("id")
        .eq("id", inspection_id)
        .limit(1)
        .execute()
    )

    if not inspection_response.data:
        return False

    # Delete the inspection.
    # Related detected_onions and onion_defects will be
    # deleted automatically because of ON DELETE CASCADE.
    (
        supabase
        .table("inspections")
        .delete()
        .eq("id", inspection_id)
        .execute()
    )

    return True

# ============================================================
# DEVELOPMENT OTP STORAGE
# ============================================================

otp_store = {}
verification_store = {}


def generate_otp(phone):
    import random

    otp = f"{random.randint(0, 999999):06d}"

    otp_store[phone] = {
        "otp_hash": hashlib.sha256(
            otp.encode("utf-8")
        ).hexdigest(),

        "expires_at": datetime.now(timezone.utc)
        + timedelta(minutes=5),

        "attempts": 0
    }

    return otp


def verify_otp(phone, otp):

    record = otp_store.get(phone)

    if record is None:
        return (
            False,
            "No OTP found. Please request a new OTP.",
            None
        )

    now = datetime.now(timezone.utc)

    if now > record["expires_at"]:

        del otp_store[phone]

        return (
            False,
            "OTP has expired. Please request a new OTP.",
            None
        )

    if record["attempts"] >= 5:

        del otp_store[phone]

        return (
            False,
            "Too many incorrect attempts. Please request a new OTP.",
            None
        )

    record["attempts"] += 1

    entered_hash = hashlib.sha256(
        otp.encode("utf-8")
    ).hexdigest()

    if entered_hash != record["otp_hash"]:

        return (
            False,
            "Invalid OTP.",
            None
        )

    # OTP is correct
    del otp_store[phone]

    verification_id = secrets.token_urlsafe(32)

    verification_store[verification_id] = {
        "phone": phone,

        "expires_at": datetime.now(timezone.utc)
        + timedelta(minutes=10)
    }

    return (
        True,
        "Phone number verified successfully.",
        verification_id
    )


def consume_verification_id(
    verification_id,
    phone
):

    record = verification_store.get(
        verification_id
    )

    if record is None:
        return False

    if record["phone"] != phone:
        return False

    if datetime.now(timezone.utc) > record["expires_at"]:

        del verification_store[verification_id]

        return False

    # Verification ID can only be used once.
    del verification_store[verification_id]

    return True

def update_user_profile(
    user_profile_id,
    name,
    phone=None,
    address=None
):
    response = (
        supabase
        .table("user_profiles")
        .update({
            "name": name,
            "phone": phone,
            "address": address
        })
        .eq("id", user_profile_id)
        .execute()
    )

    if not response.data:
        return None

    return response.data[0]

def update_user_profile(
    user_profile_id,
    name,
    phone=None,
    address=None
):
    response = (
        supabase
        .table("user_profiles")
        .update({
            "name": name,
            "phone": phone,
            "address": address
        })
        .eq("id", user_profile_id)
        .execute()
    )

    if not response.data:
        return None

    return response.data[0]