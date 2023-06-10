package com.bangkit.ecoeasemitra.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector){
    object Onboard : Screen(route = "onboard", icon = Icons.Default.Start)
    object Home : Screen(route = "home", icon = Icons.Default.Home)
    object Scan : Screen(route = "scan", icon = Icons.Default.Home)
    object History : Screen(route = "history", icon = Icons.Default.History)
    object Profile : Screen(route = "profile", icon = Icons.Default.AccountCircle)
    object Map : Screen(route = "map", icon = Icons.Default.Map)
    object Auth : Screen(route = "auth", icon = Icons.Default.Login)
    object Register : Screen(route = "register", icon = Icons.Default.AppRegistration)
    object Order : Screen(route = "order", icon = Icons.Default.Reorder)
    object DetailOrder : Screen(route = "detail_order/{orderId}", icon = Icons.Default.Reorder){
        fun createRoute(orderId: String) = "detail_order/$orderId"
    }
    object ChangeAddress : Screen(route = "change_address", icon = Icons.Default.Reorder)
    object OrderSuccess : Screen(route = "order_success", icon = Icons.Default.Reorder)
    object UsersChats : Screen(route = "users_chats", icon = Icons.Default.Chat)
    object ChatRoom : Screen(route = "chat_room/{roomId}", icon = Icons.Default.Chat){
        private var title: String? = null
        fun createRoute(roomId: String) = "chat_room/$roomId"
        fun setTitle(newTitle: String?){
            title = newTitle
        }
        fun getTitle(): String? = title
    }
}