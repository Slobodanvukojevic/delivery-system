package com.delivery.client.ui.screens.create_order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(
    navController: NavController,
    viewModel: CreateOrderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Slusaj rezultat iz SelectLockerScreen
    val currentEntry by navController.currentBackStackEntryAsState()
    val savedStateHandle = currentEntry?.savedStateHandle

    val selectedLockerId by (savedStateHandle?.getStateFlow("selectedLockerId", -1L)
        ?: MutableStateFlow(-1L)).collectAsStateWithLifecycle()
    val selectedLockerName by (savedStateHandle?.getStateFlow("selectedLockerName", "")
        ?: MutableStateFlow("")).collectAsStateWithLifecycle()

    LaunchedEffect(selectedLockerId, selectedLockerName) {
        if (selectedLockerId > 0 && selectedLockerName.isNotEmpty()) {
            viewModel.onLockerSelected(selectedLockerId, selectedLockerName)
            // ocisti da ne bi ponovo postavljalo
            savedStateHandle?.remove<Long>("selectedLockerId")
            savedStateHandle?.remove<String>("selectedLockerName")
        }
    }

    LaunchedEffect(state.success) {
        if (state.success) navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova posiljka") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Nazad")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = state.senderName,
                onValueChange = viewModel::onSenderNameChange,
                label = { Text("Ime posiljaoca") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.senderPhone,
                onValueChange = viewModel::onSenderPhoneChange,
                label = { Text("Telefon posiljaoca") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.customerName,
                onValueChange = viewModel::onCustomerNameChange,
                label = { Text("Ime primaoca") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.customerPhone,
                onValueChange = viewModel::onCustomerPhoneChange,
                label = { Text("Telefon primaoca") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.weight,
                onValueChange = viewModel::onWeightChange,
                label = { Text("Tezina (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.pickupAddress,
                onValueChange = viewModel::onPickupAddressChange,
                label = { Text("Adresa preuzimanja") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.dropoffAddress,
                onValueChange = viewModel::onDropoffAddressChange,
                label = { Text("Adresa dostave") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("Nacin dostave", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.deliveryMethod == "HOME_DELIVERY",
                    onClick = { viewModel.onDeliveryMethodChange("HOME_DELIVERY") }
                )
                Text("Kucna dostava")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.deliveryMethod == "BRANCH_PICKUP",
                    onClick = { viewModel.onDeliveryMethodChange("BRANCH_PICKUP") }
                )
                Text("Preuzimanje u poslovnici")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.deliveryMethod == "LOCKER_PICKUP",
                    onClick = { viewModel.onDeliveryMethodChange("LOCKER_PICKUP") }
                )
                Text("Preuzimanje u paketomatu")
            }
            if (state.deliveryMethod == "LOCKER_PICKUP") {
                Spacer(modifier = Modifier.height(12.dp))

                if (state.selectedLockerId != null) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Izabran paketomat:",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = state.selectedLockerName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(onClick = { viewModel.clearLocker() }) {
                                Text("Promeni paketomat")
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = { navController.navigate("select_locker") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Izaberi paketomat na mapi")
                    }
                }
            }
            if (state.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = viewModel::submit,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Kreiraj porudzbinu")
                }
            }
        }
    }
}