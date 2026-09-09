package com.example.pyazlens.data.network

import com.google.gson.annotations.SerializedName

// ============================================================
// USER PROFILE
// ============================================================

data class UserProfileResponse(
    val success: Boolean,
    val user_profile_id: Long,
    val name: String,
    val phone: String?,
    val address: String?,
    val existing_user: Boolean
)


// ============================================================
// ANALYZE RESPONSE
// ============================================================

data class AnalyzeResponse(
    val success: Boolean,

    // ID of the user who owns this inspection
    val user_profile_id: Long,

    val total_onions: Int,

    val measurement: Measurement,

    val onions: List<OnionResult>,

    val summary: Summary,

    val defect_summary: DefectSummary
)


// ============================================================
// MEASUREMENT
// ============================================================

data class Measurement(
    val reference_coin_diameter_mm: Double,
    val mm_per_pixel: Double
)


// ============================================================
// ONION RESULT
// ============================================================

data class OnionResult(
    val id: Int,
    val size: OnionSize,
    val classification: String,
    val defects: List<Defect>,
    val probabilities: Probabilities,
    val grade: String,
    val grade_reason: String
)


// ============================================================
// ONION SIZE
// ============================================================

data class OnionSize(
    val diameter_mm: Double,
    val width_mm: Double,
    val height_mm: Double
)


// ============================================================
// DEFECT
// ============================================================

data class Defect(
    val name: String,
    val confidence: Double
)


// ============================================================
// PROBABILITIES
// ============================================================

data class Probabilities(
    val Rotten: Double,
    val Sprouted: Double,

    @SerializedName("Cut/Crack")
    val cutCrack: Double,

    @SerializedName("Skin Damage")
    val skinDamage: Double,

    val Sunburned: Double,
    val Misshapen: Double
)


// ============================================================
// SUMMARY
// ============================================================

data class Summary(
    val grade_a: Int,
    val grade_urs: Int,
    val rejected: Int,
    val grade_a_percentage: Double,
    val grade_urs_percentage: Double,
    val rejected_percentage: Double
)


// ============================================================
// DEFECT SUMMARY
// ============================================================

data class DefectSummary(
    val Rotten: Int,

    @SerializedName("Cut/Crack")
    val cutCrack: Int,

    val Sprouted: Int,

    @SerializedName("Skin Damage")
    val skinDamage: Int,

    val Sunburned: Int,
    val Misshapen: Int
)

// ============================================================
// HISTORY RESPONSE
// ============================================================

data class HistoryResponse(
    val success: Boolean,
    val user_profile_id: Long,
    val total_inspections: Int,
    val inspections: List<HistoryInspection>
)


// ============================================================
// HISTORY INSPECTION
// ============================================================

data class HistoryInspection(
    val id: Int,
    val batch_id: Int?,
    val input_type: String?,
    val image_url: String?,
    val total_onions: Int,
    val healthy_count: Int?,
    val damaged_count: Int?,
    val rotten_count: Int?,
    val grade_a_count: Int?,
    val urs_count: Int?,
    val rejected_count: Int?,
    val grade_a_percentage: Double?,
    val urs_percentage: Double?,
    val rejected_percentage: Double?,
    val cut_crack_count: Int?,
    val skin_damage_count: Int?,
    val sunburned_count: Int?,
    val misshapen_count: Int?,
    val final_grade: String?,
    val sprouted_count: Int?,
    val created_at: String?
)

data class InspectionDetailsResponse(
    val success: Boolean,
    val inspection: InspectionDetails
)

data class InspectionDetails(
    val id: Int,
    val batch_id: Int?,
    val user_id: Int?,
    val input_type: String?,
    val image_url: String?,
    val total_onions: Int,
    val healthy_count: Int?,
    val damaged_count: Int?,
    val rotten_count: Int?,
    val sprouted_count: Int?,
    val undersized_count: Int?,
    val grade_a_percentage: Double?,
    val urs_percentage: Double?,
    val quality_score: Double?,
    val final_grade: String?,
    val processing_time_ms: Int?,
    val model_version: String?,
    val created_at: String?,
    val grade_a_count: Int?,
    val urs_count: Int?,
    val rejected_count: Int?,
    val rejected_percentage: Double?,
    val cut_crack_count: Int?,
    val skin_damage_count: Int?,
    val sunburned_count: Int?,
    val misshapen_count: Int?,
    val user_profile_id: Long?,
    val onions: List<InspectionOnion>
)

data class InspectionOnion(
    val id: Int,
    val inspection_id: Int,
    val tracking_id: String?,
    val defect_class: String?,
    val confidence: Double?,
    val size_mm: Double?,
    val area_pixels: Double?,
    val center_x: Double?,
    val center_y: Double?,
    val created_at: String?,
    val diameter_mm: Double?,
    val width_mm: Double?,
    val height_mm: Double?,
    val detection_confidence: Double?,
    val grade: String?,
    val grade_reason: String?,
    val defects: List<InspectionDefect>
)

data class InspectionDefect(
    val id: Int,
    val detected_onion_id: Int,
    val defect_class: String,
    val confidence: Double,
    val created_at: String?
)

data class DeleteInspectionResponse(
    val success: Boolean,
    val inspection_id: Int,
    val message: String
)

data class SendOtpResponse(
    val success: Boolean,
    val message: String,
    val dev_otp: String?,
    val verification_id: String?
)

data class VerifyOtpResponse(
    val success: Boolean,
    val verified: Boolean,
    val message: String,
    val phone: String?,
    val verification_id: String?
)

data class UserProfileUpdateResponse(
    val success: Boolean,
    val user_profile_id: Long,
    val name: String,
    val phone: String?,
    val address: String?,
    val phone_verified: Boolean
)