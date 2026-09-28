package com.delivery.client.ui.screens.select_locker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.delivery.client.ui.screens.map.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLockerScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadLocations()
    }

    val beograd = LatLng(44.8178, 20.4569)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(beograd, 12f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Izaberi paketomat") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Nazad")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                state.lockers.forEach { locker ->
                    val pos = LatLng(locker.latitude, locker.longitude)
                    val markerState = rememberMarkerState(
                        key = "locker_${locker.id}",
                        position = pos
                    )
                    Marker(
                        state = markerState,
                        title = locker.name ?: "Paketomat",
                        snippet = locker.address ?: "",
                        onClick = {
                            viewModel.onMarkerClick(locker)
                            false
                        }
                    )
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }

            state.selectedLocation?.let { locker ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = locker.name ?: "Paketomat",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = locker.address ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row {
                            TextButton(onClick = { viewModel.clearSelection() }) {
                                Text("Zatvori")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("selectedLockerId", locker.id)
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("selectedLockerName", locker.name)
                                    navController.popBackStack()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Izaberi ovaj paketomat")
                            }
                        }
                    }
                }
            }
        }
    }
}