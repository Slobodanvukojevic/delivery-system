package com.delivery.client.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.delivery.client.data.remote.dto.LocationDto
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadLocations()
    }

    val beograd = LatLng(44.8178, 20.4569)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(beograd, 11f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paketomati i poslovnice") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Nazad")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.loadLocations() }) {
                        Text("Osvezi")
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
                // CRVENI markeri - Paketomati
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
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                        zIndex = 1f,
                        onClick = {
                            viewModel.onMarkerClick(locker)
                            false
                        }
                    )
                }

                // PLAVI markeri - Poslovnice
                state.branches.forEach { branch ->
                    val pos = LatLng(branch.latitude, branch.longitude)
                    val markerState = rememberMarkerState(
                        key = "branch_${branch.id}",
                        position = pos
                    )
                    Marker(
                        state = markerState,
                        title = branch.name ?: "Poslovnica",
                        snippet = branch.address ?: "",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                        zIndex = 2f,
                        onClick = {
                            viewModel.onMarkerClick(branch)
                            false
                        }
                    )
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }

            state.selectedLocation?.let { location ->
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
                            text = location.name ?: "Nepoznato",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = location.address ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.clearSelection() }) {
                            Text("Zatvori")
                        }
                    }
                }
            }
        }
    }
}