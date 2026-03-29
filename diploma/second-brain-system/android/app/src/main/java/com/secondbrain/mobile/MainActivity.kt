package com.secondbrain.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.secondbrain.mobile.auth.SessionManager
import com.secondbrain.mobile.navigation.AppNavHost
import com.secondbrain.mobile.ui.theme.MobileTheme

class MainActivity : ComponentActivity() {
    private val appContainer = AppContainer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            MobileTheme {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    appContainer = appContainer
                )
            }
        }
    }
}