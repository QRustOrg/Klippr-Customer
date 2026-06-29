package com.example.klippr.redemption.presentation.views

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klippr.redemption.domain.model.RedemptionCode
import com.example.klippr.redemption.util.formatVence
import com.example.klippr.redemption.util.generateQrBitmap
import com.example.klippr.shared.presentation.components.KlipprTopBar
import com.example.klippr.shared.presentation.theme.KlipprPurple

// @author Samuel Bonifacio
/**
 * Pantalla de código QR generado (US-04). Muestra el QR escaneable, el código y el vencimiento.
 * Si [code] es null (navegación directa sin estado), muestra un aviso.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScreen(
    code: RedemptionCode?,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onBack: () -> Unit,
    onGoToMisPromos: () -> Unit,
    onRetry: () -> Unit = {},
    onLeaveReview: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            KlipprTopBar(title = "Tu código", onBack = onBack)
        },
        containerColor = Color.White,
        modifier = modifier,
    ) { innerPadding ->
        if (isLoading) {
            Box(
                Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = KlipprPurple) }
            return@Scaffold
        }

        if (errorMessage != null) {
            Column(
                Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(errorMessage, color = Color(0xFFD3503F), textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KlipprPurple),
                ) {
                    Text("Reintentar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            return@Scaffold
        }

        if (code == null) {
            Box(
                Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
                contentAlignment = Alignment.Center,
            ) { Text("No se encontró el código.", color = Color(0xFF888888)) }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = code.businessName ?: code.promotionTitle ?: "Promoción",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center,
            )
            code.promotionTitle?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, fontSize = 15.sp, color = Color(0xFF888888), textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(28.dp))

            // QR grande escaneable
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                val qr = remember(code.qrContent) { generateQrBitmap(code.qrContent, 640) }
                if (qr != null) {
                    Image(bitmap = qr, contentDescription = "Código QR", modifier = Modifier.size(248.dp))
                } else {
                    Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color(0xFF1A1A1A), modifier = Modifier.size(200.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Código", fontSize = 12.sp, color = Color(0xFFAAAAAA))
            Spacer(Modifier.height(2.dp))
            Text(
                text = code.code.ifBlank { code.token }.ifBlank { code.id },
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = KlipprPurple,
            )

            Spacer(Modifier.height(12.dp))
            Text("Vence: ${formatVence(code.expiresAt)}", fontSize = 14.sp, color = Color(0xFF888888))

            Spacer(Modifier.height(36.dp))

            Button(
                onClick = onGoToMisPromos,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KlipprPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text("Ver mis códigos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }

            if (onLeaveReview != null) {
                TextButton(onClick = onLeaveReview) {
                    Text("¿Ya lo usaste? Deja tu reseña", color = KlipprPurple, fontSize = 14.sp)
                }
            }
        }
    }
}
