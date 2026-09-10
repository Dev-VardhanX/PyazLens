package com.example.pyazlens.data.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    // --------------------------------------------------
    // FIREBASE → BACKEND AUTHENTICATION
    // --------------------------------------------------

    @FormUrlEncoded
    @POST("auth/firebase")
    suspend fun authenticateFirebase(
        @Field("id_token") idToken: String,
        @Field("name") name: String,
        @Field("address") address: String?
    ): UserProfileResponse


    // --------------------------------------------------
    // ANALYZE
    // --------------------------------------------------

    @Multipart
    @POST("analyze")
    suspend fun analyzeImage(
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody?,
        @Part("address") address: RequestBody?
    ): AnalyzeResponse


    // --------------------------------------------------
    // HISTORY
    // --------------------------------------------------

    @GET("users/{userProfileId}/inspections")
    suspend fun getUserInspections(
        @Path("userProfileId") userProfileId: Long
    ): HistoryResponse


    // --------------------------------------------------
    // INSPECTION DETAILS
    // --------------------------------------------------

    @GET("inspections/{inspectionId}")
    suspend fun getInspectionDetails(
        @Path("inspectionId") inspectionId: Int
    ): InspectionDetailsResponse


    // --------------------------------------------------
    // DELETE INSPECTION
    // --------------------------------------------------

    @DELETE("inspections/{inspectionId}")
    suspend fun deleteInspection(
        @Path("inspectionId") inspectionId: Int
    ): DeleteInspectionResponse


    // --------------------------------------------------
    // UPDATE PROFILE
    // --------------------------------------------------

    @Multipart
    @PUT("users/{userProfileId}")
    suspend fun updateUserProfile(
        @Path("userProfileId") userProfileId: Long,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody?,
        @Part("address") address: RequestBody?
    ): UserProfileUpdateResponse
}