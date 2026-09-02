package com.example.cst438project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = { username ->
                            navController.navigate("landing/$username") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onNavigateToRegister = { navController.navigate("register") }
                    )
                }
                composable("register") {
                    RegisterScreen(onRegisterSuccess = { navController.popBackStack() })
                }
                composable("landing/{username}") { backStackEntry ->
                    val username = backStackEntry.arguments?.getString("username") ?: "User"
                    LandingScreen(
                        username = username,
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        }
                    )
                }
            }
        }
    }
}