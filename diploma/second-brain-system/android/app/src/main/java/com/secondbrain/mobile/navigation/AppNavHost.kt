package com.secondbrain.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.secondbrain.mobile.AppContainer
import com.secondbrain.mobile.auth.SessionManager
import com.secondbrain.mobile.ui.capture.CaptureScreen
import com.secondbrain.mobile.ui.details.RawItemDetailsScreen
import com.secondbrain.mobile.ui.inbox.InboxScreen
import com.secondbrain.mobile.ui.login.LoginScreen
import com.secondbrain.mobile.ui.personnote.PersonNoteScreen
import com.secondbrain.mobile.ui.personnote.PersonNoteViewModel
import com.secondbrain.mobile.ui.search.SearchScreen
import com.secondbrain.mobile.ui.topicnote.TopicNoteScreen
import com.secondbrain.mobile.ui.topicnote.TopicNoteViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    appContainer: AppContainer
) {
    val startDestination = if (SessionManager.isLoggedIn()) {
        AppDestination.Inbox.route
    } else {
        AppDestination.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppDestination.Login.route) {
            LoginScreen(
                appContainer = appContainer,
                onLoginSuccess = {
                    navController.navigate(AppDestination.Inbox.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestination.Inbox.route) {
            InboxScreen(
                appContainer = appContainer,
                onNavigateToCapture = {
                    navController.navigate(AppDestination.Capture.route)
                },
                onNavigateToDetails = { id ->
                    navController.navigate(AppDestination.Details.createRoute(id))
                },
                onNavigateToSearch = {
                    navController.navigate(AppDestination.Search.route)
                },
                onNavigateToTopicNote = { topicId ->
                    navController.navigate("topic-note/$topicId")
                },
                onLogout = {
                    SessionManager.clearSession()
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppDestination.Capture.route) {
            CaptureScreen(
                appContainer = appContainer,
                onBack = { navController.popBackStack() },
                onNavigateToDetails = { id ->
                    navController.navigate(AppDestination.Details.createRoute(id)) {
                        popUpTo(AppDestination.Inbox.route)
                    }
                }
            )
        }

        composable(AppDestination.Search.route) {
            SearchScreen(
                appContainer = appContainer,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "topic-note/{topicId}",
            arguments = listOf(navArgument("topicId") { type = NavType.LongType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getLong("topicId") ?: 0L
            val viewModel = remember(topicId) {
                TopicNoteViewModel(appContainer.repository, topicId)
            }

            TopicNoteScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "person-note/{personId}",
            arguments = listOf(navArgument("personId") { type = NavType.LongType })
        ) { backStackEntry ->
            val personId = backStackEntry.arguments?.getLong("personId") ?: 0L
            val viewModel = remember(personId) {
                PersonNoteViewModel(appContainer.repository, personId)
            }

            PersonNoteScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppDestination.Details.route,
            arguments = listOf(navArgument("rawItemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rawItemId = backStackEntry.arguments?.getString("rawItemId") ?: ""
            RawItemDetailsScreen(
                rawItemId = rawItemId,
                appContainer = appContainer,
                onBack = { navController.popBackStack() },
                onNavigateToTopicNote = { topicId ->
                    navController.navigate("topic-note/$topicId")
                },
                onNavigateToPersonNote = { personId ->
                    navController.navigate("person-note/$personId")
                }
            )
        }
    }
}
