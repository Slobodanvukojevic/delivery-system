package com.delivery.client.ui.screens.create_order

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(
    navController: NavController,
    viewModel: CreateOrderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var branchMenuExpanded by remember { mutableStateOf(false) }
    var lockerMenuExpanded by remember { mutableStateOf(false) }

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

            Spacer(modifier = Modifier.height(12.dp))

            if (state.deliveryMethod == "HOME_DELIVERY") {
                OutlinedTextField(
                    value = state.dropoffAddress,
                    onValueChange = viewModel::onDropoffAddressChange,
                    label = { Text("Adresa dostave") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.deliveryMethod == "BRANCH_PICKUP") {
                Text(
                    text = "Izaberite poslovnicu",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (state.isLoadingBranches) {
                    CircularProgressIndicator(modifier = Modifier.height(24.dp))
                } else {
                    Column {
                        Button(
                            onClick = { branchMenuExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (state.selectedBranchName.isNotEmpty())
                                    state.selectedBranchName
                                else "Odaberi poslovnicu"
                            )
                        }
                        DropdownMenu(
                            expanded = branchMenuExpanded,
                            onDismissRequest = { branchMenuExpanded = false }
                        ) {
                            state.branches.forEach { branch ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(branch.name ?: "")
                                            Text(
                                                text = branch.address ?: "",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.onBranchSelected(branch)
                                        branchMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (state.selectedBranchId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Adresa poslovnice:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = state.dropoffAddress,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(onClick = { viewModel.clearBranch() }) {
                                Text("Promeni poslovnicu")
                            }
                        }
                    }
                }
            }

            if (state.deliveryMethod == "LOCKER_PICKUP") {
                Text(
                    text = "Izaberite paketomat",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (state.isLoadingBranches) {
                    CircularProgressIndicator(modifier = Modifier.height(24.dp))
                } else {
                    Column {
                        Button(
                            onClick = { lockerMenuExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (state.selectedLockerName.isNotEmpty())
                                    state.selectedLockerName
                                else "Odaberi paketomat"
                            )
                        }
                        DropdownMenu(
                            expanded = lockerMenuExpanded,
                            onDismissRequest = { lockerMenuExpanded = false }
                        ) {
                            state.lockers.forEach { locker ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(locker.name ?: "")
                                            Text(
                                                text = locker.address ?: "",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.onLockerSelected(locker)
                                        lockerMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (state.selectedLockerId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Adresa paketomata:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = state.dropoffAddress,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(onClick = { viewModel.clearLocker() }) {
                                Text("Promeni paketomat")
                            }
                        }
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