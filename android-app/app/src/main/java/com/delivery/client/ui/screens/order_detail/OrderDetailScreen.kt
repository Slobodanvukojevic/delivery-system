package com.delivery.client.ui.screens.order_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalji porudzbine") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Nazad")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.loadOrder() }) {
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
            if (state.isLoading && state.order == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null && state.order == null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )
            } else if (state.order != null) {
                val order = state.order!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Porudzbina #${order.id}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Status: ${order.status}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Primalac: ${order.customerName}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Telefon: ${order.customerPhone}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Adresa preuzimanja: ${order.pickupAddress}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Adresa dostave: ${order.dropoffAddress}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tezina: ${order.weight} kg")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Cena: ${order.price ?: 0.0} RSD")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Nacin dostave: ${order.deliveryMethod}")

                    if (order.pickupCode != null && order.status == "PLACED_IN_LOCKER") {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Kod za preuzimanje: ${order.pickupCode}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = state.pickupCodeInput,
                            onValueChange = viewModel::onPickupCodeChange,
                            label = { Text("Unesite kod za otvaranje") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = viewModel::openLocker,
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Otvori sanduce")
                        }

                        if (state.lockerMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.lockerMessage!!,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}