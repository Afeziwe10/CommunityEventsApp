package za.co.communityevents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import za.co.communityevents.model.Event
import za.co.communityevents.ui.screens.AddEventScreen
import za.co.communityevents.ui.screens.ApiDemoScreen
import za.co.communityevents.ui.screens.EventDetailScreen
import za.co.communityevents.ui.screens.EventListScreen
import za.co.communityevents.ui.screens.SplashScreen
import za.co.communityevents.ui.theme.CommunityEventsTheme
import za.co.communityevents.viewmodel.EventViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CommunityEventsTheme {
                val navController = rememberNavController()
                val viewModel: EventViewModel = viewModel()
                val eventsState by viewModel.eventsState.collectAsState()
                val addEventState by viewModel.addEventState.collectAsState()
                val apiState by viewModel.apiState.collectAsState()

                var selectedEvent by remember { mutableStateOf<Event?>(null) }

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(
                            onSplashFinished = {
                                navController.navigate("events_list") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("events_list") {
                        EventListScreen(
                            uiState = eventsState,
                            onEventClick = { event ->
                                selectedEvent = event
                                navController.navigate("event_detail")
                            },
                            onAddEventClick = {
                                navController.navigate("add_event")
                            },
                            onApiDemoClick = {
                                navController.navigate("api_demo")
                            },
                            onRetry = {
                                viewModel.loadEvents()
                            }
                        )
                    }
                    composable("event_detail") {
                        selectedEvent?.let { event ->
                            EventDetailScreen(
                                event = event,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                    composable("add_event") {
                        AddEventScreen(
                            addEventState = addEventState,
                            onSaveEvent = { title, date, location, description, uri ->
                                viewModel.addEvent(title, date, location, description, uri)
                            },
                            onBackClick = { navController.popBackStack() },
                            onResetState = { viewModel.resetAddEventState() }
                        )
                    }
                    composable("api_demo") {
                        ApiDemoScreen(
                            apiState = apiState,
                            onFetchClick = { viewModel.fetchApiPost() },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
