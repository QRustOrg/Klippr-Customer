package com.example.klippr.iam.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel

// @author Samuel Bonifacio

/*
 * Colors defined in AuthColors.kt (ScreenBg, ButtonPurple, TextDark).
 */

/**
 * Paso 2 del flujo "olvidé mi contraseña": fija la nueva contraseña.
 * El email validado se lee de [AuthViewModel] (state.forgotEmail). Llama al backend vía
 * viewModel.resetPassword(); al éxito navega de vuelta a SignIn.
 */
@Composable
fun ResetPasswordScreen(
    viewModel: AuthViewModel,
    onPasswordChanged: () -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(state.resetSuccess) {
        if (state.resetSuccess) {
            onPasswordChanged()
            viewModel.consumeResetFlags()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(ScreenBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.height(64.dp))

            Text(
                text = "Reset password",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
            )

            Spacer(Modifier.height(48.dp))

            KlipprField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "New password",
                isPassword = true,
            )

            Spacer(Modifier.height(20.dp))

            KlipprField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm new password",
                isPassword = true,
            )

            if (state.error != null) {
                Spacer(Modifier.height(10.dp))
                Text(state.error!!, color = Color(0xFFD32F2F), fontSize = 13.sp)
            }

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = { viewModel.resetPassword(newPassword, confirmPassword) },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
                modifier = Modifier.fillMaxWidth().height(56.dp),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text("Change password", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.White)
                }
            }

            Spacer(Modifier.height(48.dp))
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(8.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = TextDark,
            )
        }
    }
}
