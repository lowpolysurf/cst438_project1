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

        // Replaces XML layout with composable UI
        setContent {
            // Keeps track of current screen and allows other screens to go to other screens
            val navController = rememberNavController()

            // All possible navigation routes in app (so far)
            // This case, user starts on the "login" page
            NavHost(navController = navController, startDestination = "login") {
                // Shows LoginScreen.kt when the current screen is "login"
                composable("login") {
                    LoginScreen(
                        // Called by LoginScreen when username/password are valid
                        onLoginSuccess = { username ->
                            // Goes to LandingScreen.kt
                            navController.navigate("landing/$username") {
                                // Removes LoginScreen from the back stack
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        // Called when user wishes to create an account
                        onNavigateToRegister = {
                            navController.navigate("register")
                        }
                    )
                }

                // Shows RegisterScreen.kt when the current screen is "register"
                composable("register") {
                    RegisterScreen(
                        // After registering, user is directed back to LoginScreen.kt
                        onRegisterSuccess = {
                        navController.popBackStack()
                        }
                    )
                }

                composable("landing/{username}") { backStackEntry ->
                    // Retrieves username
                    val username = backStackEntry.arguments?.getString("username") ?: "User"
                    LandingScreen(
                        username = username,
                        onLogout = {
                            // Goes back to login page and clears nav history
                            // Preventing to go back to landing screen
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