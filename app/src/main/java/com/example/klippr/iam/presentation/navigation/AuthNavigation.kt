package com.example.klippr.iam.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel
import com.example.klippr.iam.presentation.views.ForgotPasswordScreen
import com.example.klippr.iam.presentation.views.ResetPasswordScreen
import com.example.klippr.iam.presentation.views.SignInScreen
import com.example.klippr.iam.presentation.views.SignUpScreen
import com.example.klippr.shared.presentation.navigation.MainRoutes

/** Grafo de navegacion del bounded context IAM (autenticacion). */
fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    composable(AuthRoutes.SIGN_IN) {
        SignInScreen(
            viewModel = authViewModel,
            onSignedIn = {
                navController.navigate(MainRoutes.HOME) {
                    popUpTo(AuthRoutes.SIGN_IN) { inclusive = true }
                }
            },
            onNavigateToSignUp = { navController.navigate(AuthRoutes.SIGN_UP) },
            onNavigateToForgot = { navController.navigate(AuthRoutes.FORGOT_PASSWORD) },
        )
    }

    composable(AuthRoutes.SIGN_UP) {
        SignUpScreen(
            viewModel = authViewModel,
            onSignedUp = {
                navController.navigate(MainRoutes.HOME) {
                    popUpTo(AuthRoutes.SIGN_IN) { inclusive = true }
                }
            },
            onBack = { navController.popBackStack() },
        )
    }

    composable(AuthRoutes.FORGOT_PASSWORD) {
        ForgotPasswordScreen(
            viewModel = authViewModel,
            onBack = { navController.popBackStack() },
        )
    }

    composable(AuthRoutes.RESET_PASSWORD) {
        ResetPasswordScreen(
            viewModel = authViewModel,
            onPasswordChanged = {
                navController.navigate(AuthRoutes.SIGN_IN) {
                    popUpTo(AuthRoutes.SIGN_IN) { inclusive = true }
                }
            },
            onBack = { navController.popBackStack() },
        )
    }
}
