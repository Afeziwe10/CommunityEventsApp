package com.example.communityeventsapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.example.communityeventsapp.model.Event
import com.example.communityeventsapp.network.JsonPlaceholderApi
import com.example.communityeventsapp.network.PostResponse
import com.example.communityeventsapp.repository.EventRepository

class EventViewModel : ViewModel() {
    private val repository = EventRepository()

    private val _eventsState = MutableStateFlow<EventsUiState>(EventsUiState.Loading)
    val eventsState: StateFlow<EventsUiState> = _eventsState.asStateFlow()

    private val _addEventState = MutableStateFlow<AddEventUiState>(AddEventUiState.Idle)
    val addEventState: StateFlow<AddEventUiState> = _addEventState.asStateFlow()

    private val _editEventState = MutableStateFlow<EditEventUiState>(EditEventUiState.Idle)
    val editEventState: StateFlow<EditEventUiState> = _editEventState.asStateFlow()

    private val _apiState = MutableStateFlow<ApiUiState>(ApiUiState.Idle)
    val apiState: StateFlow<ApiUiState> = _apiState.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _eventsState.value = EventsUiState.Loading
            repository.getEvents()
                .catch { e ->
                    _eventsState.value = EventsUiState.Error(e.localizedMessage ?: "Failed to load events")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { list ->
                            if (list.isEmpty()) {
                                _eventsState.value = EventsUiState.Empty
                            } else {
                                _eventsState.value = EventsUiState.Success(list)
                            }
                        },
                        onFailure = { e ->
                            _eventsState.value = EventsUiState.Error(e.localizedMessage ?: "Failed to load events")
                        }
                    )
                }
        }
    }

    fun addEvent(title: String, date: String, location: String, description: String, imageUri: Uri?) {
        viewModelScope.launch {
            _addEventState.value = AddEventUiState.Loading
            val result = repository.addEvent(title, date, location, description, imageUri)
            result.fold(
                onSuccess = {
                    _addEventState.value = AddEventUiState.Success
                },
                onFailure = { e ->
                    _addEventState.value = AddEventUiState.Error(e.localizedMessage ?: "Failed to save event")
                }
            )
        }
    }

    fun resetAddEventState() {
        _addEventState.value = AddEventUiState.Idle
    }

    fun updateEvent(
        eventId: String,
        title: String,
        date: String,
        location: String,
        description: String,
        imageUri: Uri?,
        currentImageUrl: String
    ) {
        viewModelScope.launch {
            _editEventState.value = EditEventUiState.Loading
            val result = repository.updateEvent(eventId, title, date, location, description, imageUri, currentImageUrl)
            result.fold(
                onSuccess = {
                    _editEventState.value = EditEventUiState.Success
                },
                onFailure = { e ->
                    _editEventState.value = EditEventUiState.Error(e.localizedMessage ?: "Failed to update event")
                }
            )
        }
    }

    fun resetEditEventState() {
        _editEventState.value = EditEventUiState.Idle
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            repository.deleteEvent(eventId)
        }
    }

    fun fetchApiPost() {
        viewModelScope.launch {
            _apiState.value = ApiUiState.Loading
            val result = JsonPlaceholderApi.fetchPost()
            result.fold(
                onSuccess = { post ->
                    _apiState.value = ApiUiState.Success(post)
                },
                onFailure = { e ->
                    _apiState.value = ApiUiState.Error(e.localizedMessage ?: "Network request failed")
                }
            )
        }
    }
}

sealed class EventsUiState {
    object Loading : EventsUiState()
    object Empty : EventsUiState()
    data class Success(val events: List<Event>) : EventsUiState()
    data class Error(val message: String) : EventsUiState()
}

sealed class AddEventUiState {
    object Idle : AddEventUiState()
    object Loading : AddEventUiState()
    object Success : AddEventUiState()
    data class Error(val message: String) : AddEventUiState()
}

sealed class EditEventUiState {
    object Idle : EditEventUiState()
    object Loading : EditEventUiState()
    object Success : EditEventUiState()
    data class Error(val message: String) : EditEventUiState()
}

sealed class ApiUiState {
    object Idle : ApiUiState()
    object Loading : ApiUiState()
    data class Success(val post: PostResponse) : ApiUiState()
    data class Error(val message: String) : ApiUiState()
}
