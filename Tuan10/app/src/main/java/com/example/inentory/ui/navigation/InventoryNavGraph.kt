package com.example.inentory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.inentory.ui.home.HomeScreen
import com.example.inentory.ui.item.ItemEntryScreen

enum class InventoryScreen {
    Home,
    ItemEntry
}

@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = InventoryScreen.Home.name,
        modifier = modifier
    ) {
        composable(route = InventoryScreen.Home.name) {
            HomeScreen(
                navigateToItemEntry = { navController.navigate(InventoryScreen.ItemEntry.name) }
            )
        }
        composable(route = InventoryScreen.ItemEntry.name) {
            ItemEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
