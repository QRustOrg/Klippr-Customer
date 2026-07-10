package com.example.klippr.community.presentation.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klippr.community.presentation.state.CommunityUiState

private val KlipprPurple = Color(0xFF6B3FA0)
private val KlipprGreen  = Color(0xFF4CAF50)
private val StarYellow   = Color(0xFFFFC107)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewBottomSheet(
    uiState: CommunityUiState,
    onDismiss: () -> Unit,
    onRatingChanged: (Int) -> Unit,
    onCommentChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Título
            Text(
                text = "Escribir reseña",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = KlipprPurple
            )

            uiState.selectedPromotionTitle?.let { title ->
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )
            }

            // Bloqueo si el usuario no puede comentar
            if (!uiState.canCurrentUserReview) {
                LockedReviewMessage()
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar")
                }
                return@Column
            }

            // ── Selección de estrellas ──
            Text(
                text = "¿Cómo fue tu experiencia?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                (1..5).forEach { i ->
                    IconButton(onClick = { onRatingChanged(i) }) {
                        Icon(
                            imageVector = if (i <= uiState.draftRating) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "$i estrellas",
                            tint = if (i <= uiState.draftRating) StarYellow else Color.LightGray,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Etiqueta de rating seleccionado
            if (uiState.draftRating > 0) {
                Text(
                    text = ratingLabel(uiState.draftRating),
                    color = KlipprPurple,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // ── Campo de comentario ──
            OutlinedTextField(
                value = uiState.draftComment,
                onValueChange = onCommentChanged,
                label = { Text("Tu comentario") },
                placeholder = { Text("Cuéntale a la comunidad cómo fue tu experiencia...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KlipprPurple,
                    focusedLabelColor = KlipprPurple,
                    cursorColor = KlipprPurple
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Contador de caracteres
            Text(
                text = "${uiState.draftComment.length}/300",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Error de validación
            uiState.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // ── Botón publicar ──
            Button(
                onClick = onSubmit,
                enabled = uiState.draftRating > 0 && uiState.draftComment.isNotBlank() && !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KlipprPurple)
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Publicar reseña", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    }
}

// ─── Mensaje cuando el usuario no ha canjeado la promo ───────────────────────
@Composable
private fun LockedReviewMessage() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🔒 Solo usuarios verificados",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFFE65100)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Para escribir una reseña debes haber canjeado esta promoción. ¡Úsala y comparte tu experiencia!",
                fontSize = 13.sp,
                color = Color(0xFF5D4037)
            )
        }
    }
}

// ─── Labels para el rating ────────────────────────────────────────────────────
private fun ratingLabel(rating: Int) = when (rating) {
    1 -> "😞 Muy malo"
    2 -> "😕 Malo"
    3 -> "😐 Regular"
    4 -> "😊 Bueno"
    5 -> "🤩 ¡Excelente!"
    else -> ""
}