package com.example.pyazlens.data.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    //private const val BASE_URL = "http://192.168.29.119:8000/"
    private const val BASE_URL = "http://152.67.10.2:8000/"
    //private const val BASE_URL = "http://10.98.129.1:8000/"
    fun imageUrl(path: String): String {
        return BASE_URL.trimEnd('/') + "/" + path.trimStart('/')
    }

    private val authInterceptor = Interceptor { chain ->

        val originalRequest = chain.request()

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser == null) {
            throw IllegalStateException("Firebase user is not authenticated")
        }

        val tokenResult = runBlocking {
            firebaseUser.getIdToken(false).await()
        }

        val idToken = tokenResult.token
            ?: throw IllegalStateException("Firebase ID token is null")

        val authenticatedRequest = originalRequest
            .newBuilder()
            .header(
                "Authorization",
                "Bearer $idToken"
            )
            .build()

        chain.proceed(authenticatedRequest)
    }

    private val okHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .callTimeout(120, TimeUnit.SECONDS)
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