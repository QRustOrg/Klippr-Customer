package com.example.klippr.shared.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// @author Samuel Bonifacio
// Campo de texto reutilizado por las pantallas de la app (IAM y otras).
// Colores propios para mantener el look 1:1 con el mockup, sin depender de paletas internas de un feature.
private val FieldButtonPurple = Color(0xFF7B6AF0)
private val FieldBorder = Color(0xFFCAC4D0)
private val FieldTextGray = Color(0xFF888888)
private val FieldTextDark = Color(0xFF1A1A1A)
private val FieldClearIcon = Color(0xFF9E9E9E)

@Composable
fun KlipprField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 14.sp, color = FieldTextGray) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        trailingIcon = {
            IconButton(onClick = { onValueChange("") }) {
                Icon(Icons.Default.Cancel, contentDescription = "Borrar", tint = FieldClearIcon, modifier = Modifier.size(22.dp))
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FieldButtonPurple,
            unfocusedBorderColor = FieldBorder,
            focusedLabelColor = FieldButtonPurple,
            unfocusedLabelColor = FieldTextGray,
            focusedTextColor = FieldTextDark,
            unfocusedTextColor = FieldTextDark,
            cursorColor = FieldButtonPurple,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    )
}
