package com.example.quantum_task

data class UnsplashImage(
    val id: String,
    val urls: ImageUrls
)

data class ImageUrls(
    val regular: String
)