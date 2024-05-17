package com.example.quantum_task

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private val CACHE_SIZE: Long = 10 * 1024 * 1024 // 10 MB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter = ImageAdapter(emptyList())
        recyclerView.adapter = adapter

        // Initialize Retrofit and make API request to fetch images
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val picasso = initializePicasso(this)
        
        val unsplashApi = retrofit.create(UnsplashApi::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            val allImages = mutableListOf<UnsplashImage>()
            try {
                // Fetch images in batches of 30
                repeat(3) {
                    val images = withContext(Dispatchers.IO) {
                        unsplashApi.getRandomImages(30, "3Ie-sijLKu73B2Fpnz5S1E6U8kjBaO-XrqEeDOZ2yxA")
                    }
                    allImages.addAll(images)
                }
                // Fetch the remaining 10 images
                val remainingImages = withContext(Dispatchers.IO) {
                    unsplashApi.getRandomImages(10, "3Ie-sijLKu73B2Fpnz5S1E6U8kjBaO-XrqEeDOZ2yxA")
                }
                allImages.addAll(remainingImages)

                // Update the adapter with the fetched images
                adapter.updateImages(allImages)
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }
}

    private fun initializePicasso(context: Context): Picasso {
        val cacheDir = File(context.cacheDir, "picasso-cache")
        val cache = Cache(cacheDir, CACHE_SIZE)
        val client = OkHttpClient.Builder().cache(cache).build()
        return Picasso.Builder(context)
            .downloader(OkHttp3Downloader(client))
            .build()

    }
}