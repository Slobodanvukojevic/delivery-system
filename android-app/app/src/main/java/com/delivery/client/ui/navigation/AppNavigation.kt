package com.delivery.client.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.delivery.client.ui.screens.create_order.CreateOrderScreen
import com.delivery.client.ui.screens.home.HomeScreen
import com.delivery.client.ui.screens.login.LoginScreen
import com.delivery.client.ui.screens.my_orders.MyOrdersScreen
import com.delivery.client.ui.screens.order_detail.OrderDetailScreen
import com.delivery.client.ui.screens.register.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("my_orders") { MyOrdersScreen(navController) }
        composable("create_order") { CreateOrderScreen(navController) }
        composable(
            route = "order_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            OrderDetailScreen(navController)
        }
    }
}