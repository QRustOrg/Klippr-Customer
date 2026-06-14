package com.example.klippr.iam.presentation.view

import com.example.klippr.shared.presentation.component.KlipprField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel

// @author Samuel Bonifacio

/*
 * Colors defined in AuthColors.kt (ScreenBg, ButtonPurple, TextDark).
 */

/**
 * Paso 1 del flujo "olvidé mi contraseña": el usuario ingresa su email.
 * viewModel.verifyEmail() valida contra el backend; al verificarse (state.emailVerified)
 * navega a la pantalla de reset (ResetPasswordScreen).
 */
@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onEmailVerified: () -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }

    LaunchedEffect(state.emailVerified) {
        if (state.emailVerified) {
            onEmailVerified()
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
                text = "Forgot password?",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
            )

            Spacer(Modifier.height(48.dp))

            KlipprField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                keyboardType = KeyboardType.Email,
            )

            if (state.error != null) {
                Spacer(Modifier.height(10.dp))
                Text(state.error!!, color = Color(0xFFD32F2F), fontSize = 13.sp)
            }

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = { viewModel.verifyEmail(email) },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
                modifier = Modifier.fillMaxWidth().height(56.dp),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text("Recover password", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.White)
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
