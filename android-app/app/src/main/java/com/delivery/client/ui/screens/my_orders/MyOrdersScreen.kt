package com.delivery.client.ui.screens.my_orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.delivery.client.data.remote.dto.OrderDto
import com.delivery.client.utils.ShakeDetector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    navController: NavController,
    isTablet: Boolean = false,
    viewModel: MyOrdersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Shake detector
    DisposableEffect(Unit) {
        val shakeDetector = ShakeDetector(context) {
            viewModel.loadOrders()
        }
        shakeDetector.start()

        onDispose {
            shakeDetector.stop()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moje porudzbine") },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Nazad")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.loadOrders() }) {
                        Text("Osvezi")
                    }
                }
            )
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tablet: NavigationRail sa leve strane
            if (isTablet) {
                NavigationRail {
                    NavigationRailItem(
                        selected = false,
                        onClick = { navController.navigate("home") },
                        icon = { Text("H") },
                        label = { Text("Pocetna") }
                    )
                    NavigationRailItem(
                        selected = true,
                        onClick = { },
                        icon = { Text("P") },
                        label = { Text("Porudzbine") }
                    )
                    NavigationRailItem(
                        selected = false,
                        onClick = { navController.navigate("create_order") },
                        icon = { Text("N") },
                        label = { Text("Nova") }
                    )
                    NavigationRailItem(
                        selected = false,
                        onClick = { navController.navigate("map") },
                        icon = { Text("M") },
                        label = { Text("Mapa") }
                    )
                }
            }

            // Glavni sadrzaj
            Column(modifier = Modifier.fillMaxSize()) {

                if (state.fromCache) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Offline mode - prikazujem kesirane podatke",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        state.isLoading && state.orders.isEmpty() -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        state.error != null && state.orders.isEmpty() -> {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = state.error!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Protresti uredjaj za osvezavanje",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        state.orders.isEmpty() -> {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Nema porudzbina")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Protresti za osvezavanje",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.orders) { order ->
                                    OrderCard(
                                        order = order,
                                        onClick = { navController.navigate("order_detail/${order.id}") }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderDto, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Porudzbina #${order.id}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Primalac: ${order.customerName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status: ${order.status}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onClick) {
                Text("Detalji")
            }
        }
    }
}