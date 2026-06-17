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
import com.example.klippr.shared.presentation.component.KlipprField

// @author Samuel Bonifacio

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }

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

            Spacer(Modifier.height(32.dp))

            if (state.passwordRecoverySent) {
                Text(
                    text = "If the email exists, we sent a recovery link. Check your inbox and follow the link to continue.",
                    color = TextDark,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.consumeResetFlags()
                        onBack()
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) {
                    Text("Back to sign in", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.White)
                }
            } else {
                Text(
                    text = "Enter your email and we will send you a recovery link.",
                    color = TextDark,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                )

                Spacer(Modifier.height(24.dp))

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
                    onClick = { viewModel.requestPasswordRecovery(email) },
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    } else {
                        Text("Send recovery link", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(48.dp))
        }

        IconButton(
            onClick = {
                viewModel.consumeResetFlags()
                onBack()
            },
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
