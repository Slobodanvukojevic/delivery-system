package com.delivery.client.ui.screens.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.delivery.client.data.remote.dto.LocationDto
import com.delivery.client.utils.LocationPermissionHandler
import com.delivery.client.utils.LocationProvider
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val beograd = LatLng(44.8178, 20.4569)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(beograd, 11f)
    }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            scope.launch {
                val loc = LocationProvider(context).getCurrentLocation()
                if (loc != null) {
                    userLocation = LatLng(loc.first, loc.second)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        userLocation!!, 15f
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadLocations()
        if (LocationPermissionHandler.hasPermission(context)) {
            val loc = LocationProvider(context).getCurrentLocation()
            if (loc != null) {
                userLocation = LatLng(loc.first, loc.second)
            }
        } else {
            permissionLauncher.launch(LocationPermissionHandler.permissions)
        }
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
                state.lockers.forEach { locker ->
                    val markerState = rememberMarkerState(
                        key = "locker_${locker.id}",
                        position = LatLng(locker.latitude, locker.longitude)
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

                state.branches.forEach { branch ->
                    val markerState = rememberMarkerState(
                        key = "branch_${branch.id}",
                        position = LatLng(branch.latitude, branch.longitude)
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

                userLocation?.let { loc ->
                    val markerState = rememberMarkerState(
                        key = "user_location",
                        position = loc
                    )
                    Marker(
                        state = markerState,
                        title = "Vi ste ovde",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                        zIndex = 3f
                    )
                }
            }

            FloatingActionButton(
                onClick = {
                    scope.launch {
                        if (!LocationPermissionHandler.hasPermission(context)) {
                            permissionLauncher.launch(LocationPermissionHandler.permissions)
                        } else {
                            val loc = LocationProvider(context).getCurrentLocation()
                            val target = if (loc != null) {
                                userLocation = LatLng(loc.first, loc.second)
                                userLocation
                            } else {
                                beograd
                            }
                            if (target != null) {
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(target, 14f)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 120.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Centriraj")
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            state.selectedLocation?.let { location ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
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