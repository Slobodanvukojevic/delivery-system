package com.delivery.client.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.delivery.client.ui.screens.create_order.CreateOrderScreen
import com.delivery.client.ui.screens.home.HomeScreen
import com.delivery.client.ui.screens.login.LoginScreen
import com.delivery.client.ui.screens.map.MapScreen
import com.delivery.client.ui.screens.my_orders.MyOrdersScreen
import com.delivery.client.ui.screens.order_detail.OrderDetailScreen
import com.delivery.client.ui.screens.register.RegisterScreen
import com.delivery.client.ui.screens.select_locker.SelectLockerScreen

@Composable
fun AppNavigation(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()

    val isTablet = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController, isTablet) }
        composable("my_orders") { MyOrdersScreen(navController, isTablet) }
        composable("create_order") { CreateOrderScreen(navController) }
        composable("map") { MapScreen(navController) }
        composable("select_locker") { SelectLockerScreen(navController) }
        composable(
            route = "order_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            OrderDetailScreen(navController)
        }
    }
}