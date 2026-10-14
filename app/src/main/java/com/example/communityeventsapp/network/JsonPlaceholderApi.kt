package com.example.communityeventsapp.network

import androidx.annotation.Keep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

@Keep
data class PostResponse(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

object JsonPlaceholderApi {
    private val client = OkHttpClient()

    suspend fun fetchPost(): Result<PostResponse> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://jsonplaceholder.typicode.com/posts/1")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP error: ${response.code} ${response.message}"))
                }
                val responseBody = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response body"))
                val json = JSONObject(responseBody)
                val post = PostResponse(
                    userId = json.getInt("userId"),
                    id = json.getInt("id"),
                    title = json.getString("title"),
                    body = json.getString("body")
                )
                Result.success(post)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
