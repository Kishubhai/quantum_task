package com.example.quantum_task

import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {
    @GET("photos/random")
    suspend fun getRandomImages(
        @Query("count") count: Int,
        @Query("client_id") clientId: String
    ): List<UnsplashImage>
}

