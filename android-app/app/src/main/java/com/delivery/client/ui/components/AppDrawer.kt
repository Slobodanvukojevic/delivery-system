package com.delivery.client.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

data class DrawerItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

val drawerItems = listOf(
    DrawerItem("Pocetna", "home", Icons.Default.Home),
    DrawerItem("Nova posiljka", "create_order", Icons.Default.ShoppingCart),
    DrawerItem("Moje porudzbine", "my_orders", Icons.Default.List),
    DrawerItem("Pracenje posiljke", "track_order", Icons.Default.Star),
    DrawerItem("Mapa paketomata", "map", Icons.Default.LocationOn),
    DrawerItem("Cenovnik", "pricing", Icons.Default.List),
    DrawerItem("Profil", "profile", Icons.Default.Person)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    navController: NavController,
    currentRoute: String,
    content: @Composable (openDrawer: () -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Delivery Client",
                        style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Paket servis",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                    )
                }
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        content {
            scope.launch { drawerState.open() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerIconButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Default.Menu, contentDescription = "Meni")
    }
}