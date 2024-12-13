package com.example.trackingfitness.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trackingfitness.screens.ChangePassScreen
import com.example.trackingfitness.screens.LoginScreen
import com.example.trackingfitness.screens.OTPScreen
import com.example.trackingfitness.screens.PrincipalScreen
import com.example.trackingfitness.screens.RecoverPasswordScreen
import com.example.trackingfitness.screens.RegisterFourScreen
import com.example.trackingfitness.screens.RegisterThreeScreen
import com.example.trackingfitness.screens.RegisterTwoScreen
import com.example.trackingfitness.screens.RegisterOneScreen
import com.example.trackingfitness.screens.StartScreen
import com.example.trackingfitness.screens.ExerciseScreen
import com.example.trackingfitness.screens.ProfileScreen
import com.example.trackingfitness.viewModel.LoginViewModel
import com.example.trackingfitness.viewModel.RecoverPasswordViewModel
import com.example.trackingfitness.viewModel.RegisterViewModel
import com.example.trackingfitness.viewModel.UserSessionManager

// Navegación de la app
@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    val registerViewModel: RegisterViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val recoverPasswordViewModel: RecoverPasswordViewModel = viewModel()
    val userSessionManager = UserSessionManager(LocalContext.current.applicationContext)
    NavHost(
        navController = navController,
        startDestination = if (userSessionManager.isUserLoggedIn()) AppScreens.PrincipalScreen.route
    else AppScreens.StartScreen.route ) // Ruta de inicio de la app según el estado de inicio de sesión
    {
        composable(AppScreens.StartScreen.route)
        {
            StartScreen(navController)
        }
        composable(AppScreens.RegisterOneScreen.route) {
            RegisterOneScreen(
                navController = navController,
                viewModel = registerViewModel
            )
        }
        composable(AppScreens.RegisterTwoScreen.route) {
            RegisterTwoScreen(
                navController = navController,
                viewModel = registerViewModel
            )
        }
        composable(AppScreens.RegisterThreeScreen.route) {
            RegisterThreeScreen(
                navController = navController,
                viewModel = registerViewModel
            )
        }
        composable(AppScreens.RegisterFourScreen.route) {
            RegisterFourScreen(
                navController = navController,
                viewModel = registerViewModel
            )
        }
        composable(AppScreens.LoginScreen.route) {
            LoginScreen(navController = navController, loginViewModel = loginViewModel)
        }
        composable(AppScreens.RecoverPasswordScreen.route) {
            RecoverPasswordScreen(
                navController = navController,
                recoverPasswordViewModel = recoverPasswordViewModel)
        }
        composable(AppScreens.OTPScreen.route) {
            OTPScreen(
                navController = navController,
                recoverPasswordViewModel = recoverPasswordViewModel
            )
        }
        composable(AppScreens.PrincipalScreen.route) {
            PrincipalScreen(navController = navController,
                userSession = userSessionManager)
        }
        composable(AppScreens.ExerciseScreen.route) {
            ExerciseScreen(navController = navController)
        }
        composable(AppScreens.ChangePassScreen.route) {
            ChangePassScreen(
                navController = navController,
                recoverPasswordViewModel = recoverPasswordViewModel
            )
        }
        composable(AppScreens.ProfileScreen.route) {
            ProfileScreen(
                navController = navController,
                userSession = userSessionManager
            )
        }
    }
}

