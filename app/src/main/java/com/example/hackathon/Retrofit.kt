package com.example.hackathon

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.Serializable

object RetrofitClient {
    private const val BASE_URL = "https://90b9-2600-1700-65a0-e3a0-b870-675a-1048-f9c2.ngrok-free.app"

    val apiService: ChatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApiService::class.java)
    }
}

interface ChatApiService {
    @Headers("Content-Type: application/json")
    @POST("/api/query")
    fun sendQuery(@Body request: ChatRequest): Call<ChatResponse>
}

data class ChatRequest(
    val query: String
) : Serializable

data class ChatResponse(
    val answer: String,
    val query: String
) : Serializable