package com.bangkit.ecoeasemitra.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bangkit.ecoeasemitra.data.Screen
import com.bangkit.ecoeasemitra.ui.theme.DarkGrey

@Composable
fun BottomNavBar(
    navController: NavHostController,
    items: List<Screen>,
    modifier: Modifier = Modifier
){
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    BottomAppBar(
        backgroundColor = MaterialTheme.colors.background
    ) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.background,
            modifier = modifier
            .fillMaxWidth()
        ) {
            items.forEach { item  ->
                BottomNavigationItem(
                    selected = currentRoute == item.route,
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = DarkGrey,
                    onClick = { if(currentRoute != item.route) navController.navigate(item.route) },
                    icon = { Icon(item.icon, contentDescription = "${item.route}") }
                )
            }
        }
    }
}