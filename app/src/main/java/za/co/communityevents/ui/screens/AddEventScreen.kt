package za.co.communityevents.ui.screens

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
import za.co.communityevents.viewmodel.AddEventUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    addEventState: AddEventUiState,
    onSaveEvent: (String, String, String, String, Uri?) -> Unit,
    onBackClick: () -> Unit,
    onResetState: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(addEventState) {
        if (addEventState is AddEventUiState.Success) {
            onBackClick()
            onResetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Community Event") },
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
                        Text(if (selectedImageUri == null) "Select Event Image (Optional)" else "Change Image")
                    }
                    selectedImageUri?.let { uri ->
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = uri,
                            contentDescription = "Selected Image Preview",
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

                if (addEventState is AddEventUiState.Error) {
                    Text(
                        text = "Save Error: ${addEventState.message}",
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

                        // Validate YYYY-MM-DD date format
                        val dateRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
                        if (!dateRegex.matches(trimmedDate)) {
                            errorMessage = "Date must be in exact YYYY-MM-DD format (e.g. 2026-11-25)."
                            return@Button
                        }

                        errorMessage = null
                        onSaveEvent(trimmedTitle, trimmedDate, trimmedLocation, trimmedDescription, selectedImageUri)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = addEventState !is AddEventUiState.Loading
                ) {
                    if (addEventState is AddEventUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Save Event")
                    }
                }
            }
        }
    }
}
