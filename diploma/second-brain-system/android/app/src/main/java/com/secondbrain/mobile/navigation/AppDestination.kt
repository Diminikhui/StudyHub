package com.secondbrain.mobile.navigation

sealed class AppDestination(val route: String) {
    data object Login : AppDestination("login")
    object Inbox : AppDestination("inbox")
    object Capture : AppDestination("capture")
    data object Search : AppDestination("search")
    object Details : AppDestination("details/{rawItemId}") {
        fun createRoute(rawItemId: String) = "details/$rawItemId"
    }
}