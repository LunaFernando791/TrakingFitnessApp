package com.example.trackingfitness.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trackingfitness.screens.ChangePassScreen
import com.example.trackingfitness.screens.EditEmailScreen
import com.example.trackingfitness.screens.EditPasswordScreen
import com.example.trackingfitness.screens.EditProfilePicture
import com.example.trackingfitness.screens.EditProfileScreen
import com.example.trackingfitness.screens.LoginScreen
import com.example.trackingfitness.screens.OTPScreen
import com.example.trackingfitness.screens.PrincipalScreen
import com.example.trackingfitness.screens.RecoverPasswordScreen
import com.example.trackingfitness.screens.RegisterFourScreen
import com.example.trackingfitness.screens.RegisterThreeScreen
import com.example.trackingfitness.screens.RegisterTwoScreen
import com.example.trackingfitness.screens.RegisterOneScreen
import com.example.trackingfitness.screens.StartScreen
import com.example.trackingfitness.screens.ExerciseListScreen
import com.example.trackingfitness.screens.FriendProfileScreen
import com.example.trackingfitness.screens.FriendsScreen
import com.example.trackingfitness.screens.ProfileScreen
import com.example.trackingfitness.viewModel.FriendsViewModel
import com.example.trackingfitness.viewModel.ImageViewModel
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
    val imageViewModel: ImageViewModel = viewModel()
    val friendsViewModel: FriendsViewModel = viewModel()
    var startDestination by remember { mutableStateOf(AppScreens.StartScreen.route) }
    val userSessionManager = UserSessionManager(LocalContext.current.applicationContext)
    LaunchedEffect(Unit) {
        val isLoggedIn = userSessionManager.isUserLoggedIn()
        startDestination = if (isLoggedIn) AppScreens.PrincipalScreen.route else AppScreens.StartScreen.route
    }
    NavHost(
        navController = navController,
        startDestination = startDestination ) // Ruta de inicio de la app según el estado de inicio de sesión
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
            PrincipalScreen(
                navController = navController,
                userSession = userSessionManager,
                friendsViewModel = friendsViewModel
            )
        }
        composable(AppScreens.ExerciseListScreen.route) {
            ExerciseListScreen(navController = navController)
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
        composable(AppScreens.ExerciseCameraScreen.route) {
        // TRACKING
        }
        composable(AppScreens.EditProfileScreen.route) {
            EditProfileScreen(
                navController = navController,
                userSession = userSessionManager
            )
        }
        composable(AppScreens.EditEmailScreen.route) {
            EditEmailScreen(
                navController = navController,
                userSession = userSessionManager
            )
        }
        composable(AppScreens.EditPasswordScreen.route) {
            EditPasswordScreen(
                navController = navController,
                userSession = userSessionManager
            )
        }
        composable(AppScreens.EditProfilePicture.route) {
            EditProfilePicture(
                imageViewModel = imageViewModel,
                navController = navController,
                userSessionManager = userSessionManager
            )
        }
        composable(AppScreens.FriendsScreen.route) {
            FriendsScreen(
                navController = navController,
                userSession = userSessionManager,
                friendsViewModel = friendsViewModel
            )
        }
        composable(
            AppScreens.FriendProfileScreen.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) {
            backStackEntry ->
            val friendUsername = backStackEntry.arguments?.getString("username")
            if (friendUsername != null) {
                FriendProfileScreen(
                    friendUsername = friendUsername,
                    navController = navController,
                    userSession = userSessionManager,
                    friendsViewModel = friendsViewModel
                )
            }
        }
    }
}

