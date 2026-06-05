package com.example.klippr.iam.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// @author Samuel Bonifacio

/*
 * Colors defined in AuthColors.kt (ScreenBg, ButtonPurple, TextDark).
 */

/**
 * Pantalla de restablecimiento de contraseña (mockup 1:1, UI-only).
 * No existe endpoint backend documentado; validación local por ahora.
 */
@Composable
fun ForgotPasswordScreen(
    onPasswordChanged: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
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
            onValueChange = { newPassword = it; error = null },
            label = "New password",
            isPassword = true,
        )

        Spacer(Modifier.height(20.dp))

        KlipprField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; error = null },
            label = "Confirm new password",
            isPassword = true,
        )

        if (error != null) {
            Spacer(Modifier.height(10.dp))
            Text(error!!, color = Color(0xFFD32F2F), fontSize = 13.sp)
        }

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = {
                when {
                    newPassword.isBlank() || confirmPassword.isBlank() -> error = "Completa todos los campos"
                    newPassword != confirmPassword -> error = "Las contraseñas no coinciden"
                    newPassword.length < 6 -> error = "Mínimo 6 caracteres"
                    else -> onPasswordChanged()
                }
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            Text("Change password", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.White)
        }

        Spacer(Modifier.height(48.dp))
    }
}
