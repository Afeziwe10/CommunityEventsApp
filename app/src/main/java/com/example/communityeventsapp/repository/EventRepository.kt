package com.example.communityeventsapp.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.example.communityeventsapp.model.Event
import java.util.UUID

class EventRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val eventsCollection = firestore.collection("events")

    fun getEvents(): Flow<Result<List<Event>>> = callbackFlow {
        val subscription = eventsCollection
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val events = snapshot.documents.mapNotNull { doc ->
                        val event = doc.toObject(Event::class.java)
                        event?.copy(id = doc.id)
                    }
                    trySend(Result.success(events))
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addEvent(title: String, date: String, location: String, description: String, imageUri: Uri?): Result<Unit> {
        return try {
            var imageUrl = ""
            if (imageUri != null) {
                val fileName = "event-images/${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference.child(fileName)
                storageRef.putFile(imageUri).await()
                imageUrl = storageRef.downloadUrl.await().toString()
            }

            val eventMap = hashMapOf(
                "title" to title,
                "date" to date,
                "location" to location,
                "description" to description,
                "imageUrl" to imageUrl
            )

            eventsCollection.add(eventMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEvent(
        eventId: String,
        title: String,
        date: String,
        location: String,
        description: String,
        imageUri: Uri?,
        currentImageUrl: String
    ): Result<Unit> {
        return try {
            var imageUrl = currentImageUrl
            if (imageUri != null) {
                val fileName = "event-images/${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference.child(fileName)
                storageRef.putFile(imageUri).await()
                imageUrl = storageRef.downloadUrl.await().toString()
            }

            val eventMap = hashMapOf(
                "title" to title,
                "date" to date,
                "location" to location,
                "description" to description,
                "imageUrl" to imageUrl
            )

            eventsCollection.document(eventId).set(eventMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            eventsCollection.document(eventId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
