package com.example.trackingfitness.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trackingfitness.screens.LoginScreen
import com.example.trackingfitness.screens.OTPScreen
import com.example.trackingfitness.screens.PrincipalScreen
import com.example.trackingfitness.screens.RecoverPasswordScreen
import com.example.trackingfitness.screens.RegisterFourScreen
import com.example.trackingfitness.screens.RegisterThreeScreen
import com.example.trackingfitness.screens.RegisterTwoScreen
import com.example.trackingfitness.screens.RegisterOneScreen
import com.example.trackingfitness.screens.StartScreen
import com.example.trackingfitness.viewModel.LoginViewModel
import com.example.trackingfitness.viewModel.RegisterViewModel


@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    val registerViewModel: RegisterViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    NavHost(navController = navController, startDestination = AppScreens.StartScreen.route ) {
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
            RecoverPasswordScreen(navController = navController)
        }
        composable(AppScreens.OTPScreen.route) {
            OTPScreen()
        }
        composable(AppScreens.PrincipalScreen.route) {
            PrincipalScreen(navController = navController)
        }
    }
}

