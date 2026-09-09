package com.example.pyazlens.data.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @Multipart
    @POST("users")
    suspend fun createOrGetUser(
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody?,
        @Part("address") address: RequestBody?
    ): UserProfileResponse

    @Multipart
    @POST("analyze")
    suspend fun analyzeImage(
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("user_profile_id") userProfileId: RequestBody
    ): AnalyzeResponse

    @GET("users/{userProfileId}/inspections")
    suspend fun getUserInspections(
        @Path("userProfileId") userProfileId: Long
    ): HistoryResponse

    @GET("inspections/{inspectionId}")
    suspend fun getInspectionDetails(
        @Path("inspectionId") inspectionId: Int
    ): InspectionDetailsResponse

    @DELETE("inspections/{inspectionId}")
    suspend fun deleteInspection(
        @Path("inspectionId") inspectionId: Int
    ): DeleteInspectionResponse

    @FormUrlEncoded
    @POST("auth/send-otp")
    suspend fun sendOtp(
        @Field("phone") phone: String
    ): SendOtpResponse

    @FormUrlEncoded
    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Field("phone") phone: String,
        @Field("otp") otp: String
    ): VerifyOtpResponse

    @Multipart
    @POST("users/verified")
    suspend fun createOrGetVerifiedUser(
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("address") address: RequestBody?,
        @Part("verification_id") verificationId: RequestBody
    ): UserProfileResponse

    @Multipart
    @retrofit2.http.PUT("users/{userProfileId}")
    suspend fun updateUserProfile(
        @retrofit2.http.Path("userProfileId") userProfileId: Long,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody?,
        @Part("address") address: RequestBody?
    ): UserProfileUpdateResponse
}