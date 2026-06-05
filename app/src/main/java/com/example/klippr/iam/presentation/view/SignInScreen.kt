package com.example.klippr.iam.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel
import com.example.klippr.ui.theme.KlipprLavender
import com.example.klippr.ui.theme.KlipprPurple

// @author Samuel Bonifacio
/** Pantalla de inicio de sesión. Al autenticar, dispara [onSignedIn]. */
@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    onSignedIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Navega cuando hay usuario autenticado (login manual o sesión restaurada).
    androidx.compose.runtime.LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) onSignedIn()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(KlipprLavender, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.LocalOffer,
                    contentDescription = null,
                    tint = KlipprPurple,
                    modifier = Modifier.size(40.dp),
                )
            }

            androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp))
            Text("Klippr", fontWeight = FontWeight.Bold, fontSize = 30.sp, color = KlipprPurple)
            androidx.compose.foundation.layout.Spacer(Modifier.height(4.dp))
            Text("Inicia sesión para continuar", fontSize = 14.sp, color = Color(0xFF888888))

            androidx.compose.foundation.layout.Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = klipprFieldColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            )

            androidx.compose.foundation.layout.Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = klipprFieldColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.error != null) {
                androidx.compose.foundation.layout.Spacer(Modifier.height(10.dp))
                Text(state.error!!, color = Color(0xFFD32F2F), fontSize = 13.sp)
            }

            androidx.compose.foundation.layout.Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.signIn(email, password) },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KlipprPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Text("Iniciar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun klipprFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = KlipprPurple,
    unfocusedBorderColor = KlipprLavender,
    focusedLabelColor = KlipprPurple,
    cursorColor = KlipprPurple,
)
