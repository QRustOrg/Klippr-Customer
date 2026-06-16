package com.example.klippr.redemption.presentation.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.util.generateQrBitmap
import com.example.klippr.shared.presentation.component.KlipprBottomBar
import com.example.klippr.shared.presentation.component.KlipprTab
import com.example.klippr.ui.theme.KlipprPurple

// @author Samuel Bonifacio

private val CardPink = Color(0xFFFBEFFA)
private val CheckGreenBg = Color(0xFFB8F0C8)
private val CheckGreen = Color(0xFF1E9E54)

/**
 * Pantalla de éxito tras generar un código (US-04, mockup "¡FELICIDADES!").
 * Muestra el QR en una tarjeta rosa con el código copiable. Solo se usa tras generar;
 * abrir un código existente desde una tarjeta usa [QrCodeScreen].
 */
@Composable
fun RedemptionSuccessScreen(
    code: RedemptionCode?,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onContinue: () -> Unit,
    onComunidad: () -> Unit = {},
    onInicio: () -> Unit = {},
    onFavoritos: () -> Unit = {},
    onPromos: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        bottomBar = {
            KlipprBottomBar(
                current = KlipprTab.PROMOS,
                onComunidad = onComunidad,
                onInicio = onInicio,
                onFavoritos = onFavoritos,
                onPromos = onPromos,
            )
        },
        containerColor = Color.White,
        modifier = modifier,
    ) { innerPadding ->
        when {
            isLoading -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = KlipprPurple) }

            code == null -> Box(
                Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
                contentAlignment = Alignment.Center,
            ) { Text(errorMessage ?: "No se encontró el código.", color = Color(0xFF888888), textAlign = TextAlign.Center) }

            else -> SuccessContent(
                code = code,
                onContinue = onContinue,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun SuccessContent(
    code: RedemptionCode,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val codeText = code.code.ifBlank { code.token }.ifBlank { code.id }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = "¡FELICIDADES!",
            color = KlipprPurple,
            fontWeight = FontWeight.Bold,
            fontSize = 38.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier.size(120.dp).clip(CircleShape).background(CheckGreenBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = CheckGreen, modifier = Modifier.size(64.dp))
        }

        Spacer(Modifier.height(20.dp))
        Text("Promocion creada", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF1A1A1A))

        Spacer(Modifier.height(28.dp))
        // Tarjeta rosa con el QR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CardPink)
                .padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = (code.businessName ?: code.promotionTitle ?: "Promoción").uppercase(),
                color = KlipprPurple,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier.size(200.dp).clip(RoundedCornerShape(20.dp)).background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                val qr = remember(code.qrContent) { generateQrBitmap(code.qrContent, 480) }
                if (qr != null) {
                    Image(bitmap = qr, contentDescription = "Código QR", modifier = Modifier.size(168.dp))
                } else {
                    Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color(0xFF1A1A1A), modifier = Modifier.size(140.dp))
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text(codeText, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A1A1A))
                }
                Spacer(Modifier.size(10.dp))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            clipboard.setText(AnnotatedString(codeText))
                            Toast.makeText(context, "Código copiado", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copiar código", tint = Color(0xFF1A1A1A), modifier = Modifier.size(22.dp))
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onContinue,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = KlipprPurple),
            modifier = Modifier.fillMaxWidth().height(54.dp),
        ) {
            Text("Continuar explorando", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        }
    }
}
