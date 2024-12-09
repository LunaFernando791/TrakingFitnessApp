package com.example.trackingfitness.navigation

sealed class AppScreens(val route: String) {
    object StartScreen : AppScreens("startScreen")
    object RegisterOneScreen : AppScreens("registerOneScreen")
    object RegisterTwoScreen : AppScreens("registerTwoScreen")
    object RegisterThreeScreen : AppScreens("registerThreeScreen")
    object RegisterFourScreen : AppScreens("registerFourScreen")
    object LoginScreen : AppScreens("loginScreen")
    object RecoverPasswordScreen : AppScreens("recoverPassword")
    object PrincipalScreen : AppScreens("principalScreen")
    object OTPScreen : AppScreens("otpScreen")
    object ExerciseScreen : AppScreens("exerciseScreen")

    // RUTAS DE LA APLICACIÓN.
}