package com.example.klippr.iam.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel
import com.example.la25_11.R

// @author Samuel Bonifacio

/** Pantalla de registro (mockup 1:1). */
@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onSignedUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) onSignedUp()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.height(48.dp))

        AsyncImage(
            model = R.mipmap.ic_launcher,
            contentDescription = "Klippr",
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(28.dp)),
        )

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Create an account",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(32.dp))

        KlipprField(value = name, onValueChange = { name = it }, label = "Name")
        Spacer(Modifier.height(16.dp))
        KlipprField(value = email, onValueChange = { email = it }, label = "Email", keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(16.dp))
        KlipprField(value = username, onValueChange = { username = it }, label = "Username")
        Spacer(Modifier.height(16.dp))
        KlipprField(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)

        if (state.error != null) {
            Spacer(Modifier.height(10.dp))
            Text(state.error!!, color = Color(0xFFD32F2F), fontSize = 13.sp)
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = { viewModel.signUp(name, email, password) },
            enabled = !state.isLoading,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
            } else {
                Text("Sign Up", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.White)
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}
