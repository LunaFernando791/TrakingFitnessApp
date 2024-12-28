package com.example.trackingfitness.navigation
// RUTAS DE LA APLICACIÓN.
sealed class AppScreens(val route: String) {
    data object StartScreen : AppScreens("startScreen")
    data object RegisterOneScreen : AppScreens("registerOneScreen")
    data object RegisterTwoScreen : AppScreens("registerTwoScreen")
    data object RegisterThreeScreen : AppScreens("registerThreeScreen")
    data object RegisterFourScreen : AppScreens("registerFourScreen")
    data object LoginScreen : AppScreens("loginScreen")
    data object RecoverPasswordScreen : AppScreens("recoverPassword")
    data object PrincipalScreen : AppScreens("homeScreen")
    data object OTPScreen : AppScreens("otpScreen")
    data object ExerciseListScreen : AppScreens("exerciseListScreen")
    data object ChangePassScreen : AppScreens("changePassScreen")
    data object ProfileScreen : AppScreens("profileScreen")
    data object ExerciseCameraScreen : AppScreens("exerciseCameraScreen")
    data object EditPasswordScreen : AppScreens("editPasswordScreen")
    data object EditEmailScreen : AppScreens("editEmailScreen")
    data object EditProfileScreen : AppScreens("editProfileScreen")
}