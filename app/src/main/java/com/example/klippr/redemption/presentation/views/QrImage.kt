package com.example.klippr.redemption.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.klippr.redemption.util.buildQrImageUrl
import com.example.klippr.shared.presentation.theme.KlipprPurple
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

// @author Samuel Bonifacio
/**
 * Renderiza el QR de canje pidiéndolo a la QR Server API (goqr.me) a partir de [content]
 * (token/código ya generado por el backend Render). Muestra loading/error con reintento;
 * se usa tanto en [RedemptionSuccessScreen] como en [QrCodeScreen].
 */
@Composable
fun QrImage(content: String, modifier: Modifier = Modifier, sizePx: Int = 200) {
    var retryNonce by remember(content) { mutableStateOf(0) }
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .okHttpClient {
                OkHttpClient.Builder()
                    .callTimeout(8, TimeUnit.SECONDS)
                    .build()
            }
            .build()
    }

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(buildQrImageUrl(content, sizePx))
            .setParameter("retryNonce", retryNonce)
            .build(),
        imageLoader = imageLoader,
        contentDescription = "Código QR",
        modifier = modifier,
        loading = { CircularProgressIndicator(color = KlipprPurple) },
        error = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(56.dp))
                Spacer(Modifier.height(8.dp))
                Text(
                    "No se pudo generar el QR",
                    fontSize = 13.sp,
                    color = Color(0xFF888888),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { retryNonce++ },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KlipprPurple),
                ) {
                    Text("Reintentar", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
            }
        },
    )
}
