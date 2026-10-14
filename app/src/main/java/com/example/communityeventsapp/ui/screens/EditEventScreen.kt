package com.example.communityeventsapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.communityeventsapp.model.Event
import com.example.communityeventsapp.viewmodel.EditEventUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    event: Event,
    editEventState: EditEventUiState,
    onUpdateEvent: (String, String, String, String, String, Uri?, String) -> Unit,
    onBackClick: () -> Unit,
    onResetState: () -> Unit
) {
    var title by remember(event) { mutableStateOf(event.title) }
    var date by remember(event) { mutableStateOf(event.date) }
    var location by remember(event) { mutableStateOf(event.location) }
    var description by remember(event) { mutableStateOf(event.description) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(editEventState) {
        if (editEventState is EditEventUiState.Success) {
            onBackClick()
            onResetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Community Event") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("2026-12-31") }
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text(if (selectedImageUri == null && event.imageUrl.isBlank()) "Select Event Image (Optional)" else "Change Image")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "New Selected Image Preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else if (event.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = event.imageUrl,
                            contentDescription = "Current Image Preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (editEventState is EditEventUiState.Error) {
                    Text(
                        text = "Update Error: ${editEventState.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val trimmedTitle = title.trim()
                        val trimmedDate = date.trim()
                        val trimmedLocation = location.trim()
                        val trimmedDescription = description.trim()

                        if (trimmedTitle.isEmpty() || trimmedDate.isEmpty() || trimmedLocation.isEmpty() || trimmedDescription.isEmpty()) {
                            errorMessage = "All fields (Title, Date, Location, Description) are required and cannot be blank."
                            return@Button
                        }

                        val dateRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
                        if (!dateRegex.matches(trimmedDate)) {
                            errorMessage = "Date must be in exact YYYY-MM-DD format (e.g. 2026-11-25)."
                            return@Button
                        }

                        errorMessage = null
                        onUpdateEvent(
                            event.id,
                            trimmedTitle,
                            trimmedDate,
                            trimmedLocation,
                            trimmedDescription,
                            selectedImageUri,
                            event.imageUrl
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = editEventState !is EditEventUiState.Loading
                ) {
                    if (editEventState is EditEventUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Update Event")
                    }
                }
            }
        }
    }
}
