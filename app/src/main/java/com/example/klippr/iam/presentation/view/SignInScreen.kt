package com.example.klippr.iam.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.klippr.iam.presentation.viewmodel.AuthViewModel
import com.example.klippr.R

// @author Samuel Bonifacio

/** Pantalla de inicio de sesión */
@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    onSignedIn: () -> Unit,
    onNavigateToSignUp: () -> Unit = {},
    onNavigateToForgot: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) onSignedIn()
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

        Surface(color = Color.White, shape = RoundedCornerShape(28.dp)) {
            AsyncImage(
                model = R.drawable.klippr_lockup,
                contentDescription = "Klippr",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .padding(16.dp),
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Welcome to\nKlippr!",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            lineHeight = 42.sp,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(32.dp))

        // Username field
        KlipprField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            keyboardType = KeyboardType.Email,
        )

        Spacer(Modifier.height(16.dp))

        // Password field
        KlipprField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
        )

        // Remember me + Forgot password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { rememberMe = !rememberMe },
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = ButtonPurple),
                )
                Text("Remember me", color = TextDark, fontSize = 14.sp)
            }
            TextButton(onClick = onNavigateToForgot) {
                Text("Forgot your password?", color = LinkPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        if (state.error != null) {
            Spacer(Modifier.height(4.dp))
            Text(state.error!!, color = Color(0xFFD32F2F), fontSize = 13.sp)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { viewModel.signIn(username, password, rememberMe) },
            enabled = !state.isLoading,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
            } else {
                Text("Log in", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.White)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Sign up link
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = TextDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)) {
                    append("Don't have an account? ")
                }
                withStyle(SpanStyle(color = LinkPurple, fontWeight = FontWeight.Bold, fontSize = 15.sp)) {
                    append("Sign up")
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onNavigateToSignUp() },
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
internal fun KlipprField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp, color = TextGray) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        trailingIcon = {
            IconButton(onClick = { onValueChange("") }) {
                Icon(Icons.Default.Cancel, contentDescription = "Borrar", tint = ClearIcon, modifier = Modifier.size(22.dp))
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ButtonPurple,
            unfocusedBorderColor = FieldBorder,
            focusedLabelColor = ButtonPurple,
            unfocusedLabelColor = TextGray,
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark,
            cursorColor = ButtonPurple,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    )
}
