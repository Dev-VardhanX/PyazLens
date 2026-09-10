package com.example.pyazlens.data.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // private const val BASE_URL = "http://192.168.29.119:8000/"
    private const val BASE_URL = "http://152.67.10.2:8000/"

    private val authInterceptor = Interceptor { chain ->

        val originalRequest = chain.request()

        val firebaseUser =
            FirebaseAuth.getInstance().currentUser

        if (firebaseUser == null) {
            return@Interceptor chain.proceed(originalRequest)
        }

        try {

            val tokenResult = runBlocking {
                firebaseUser.getIdToken(false).await()
            }

            val idToken = tokenResult.token

            if (idToken.isNullOrBlank()) {
                return@Interceptor chain.proceed(originalRequest)
            }

            val authenticatedRequest =
                originalRequest
                    .newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer $idToken"
                    )
                    .build()

            chain.proceed(authenticatedRequest)

        } catch (e: Exception) {

            chain.proceed(originalRequest)
        }
    }

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    val api: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)
    }
}