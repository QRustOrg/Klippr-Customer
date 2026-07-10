package com.example.klippr.shared.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klippr.shared.presentation.theme.KlipprLavender
import com.example.klippr.shared.presentation.theme.KlipprPurple
import com.example.klippr.shared.presentation.theme.KlipprTextDark
import com.example.klippr.shared.presentation.theme.KlipprTextGray
import kotlinx.coroutines.delay

/** Tipo visual del toast in-app. */
enum class KlipprToastKind {
    FAVORITE,
    REDEMPTION,
}

/** Payload de un toast tipo card. */
data class KlipprToastMessage(
    val kind: KlipprToastKind,
    val title: String,
    val subtitle: String,
)

/**
 * Estado del host de toasts. Un solo mensaje a la vez; al mostrar otro se reemplaza.
 */
@Stable
class KlipprToastHostState {
    var current by mutableStateOf<KlipprToastMessage?>(null)
        private set

    private var generation: Long = 0L

    fun show(message: KlipprToastMessage) {
        generation += 1L
        current = message
    }

    fun showFavorite(promoTitle: String? = null) {
        val subtitle = promoTitle
            ?.takeIf { it.isNotBlank() }
            ?.let { "\"$it\" se agregó a tu lista" }
            ?: "Agregaste una promo a tus favoritos"
        show(
            KlipprToastMessage(
                kind = KlipprToastKind.FAVORITE,
                title = "Guardado en favoritos",
                subtitle = subtitle,
            ),
        )
    }

    fun showRedemption(promoTitle: String? = null) {
        val subtitle = promoTitle
            ?.takeIf { it.isNotBlank() }
            ?.let { "Tu código para \"$it\" está listo" }
            ?: "Tu código de canje está listo"
        show(
            KlipprToastMessage(
                kind = KlipprToastKind.REDEMPTION,
                title = "¡Código generado!",
                subtitle = subtitle,
            ),
        )
    }

    internal fun dismissIfCurrent(token: Long) {
        if (token == generation) current = null
    }

    internal fun currentGeneration(): Long = generation
}

@Composable
fun rememberKlipprToastHostState(): KlipprToastHostState = remember { KlipprToastHostState() }

val LocalKlipprToast = staticCompositionLocalOf<KlipprToastHostState> {
    error("LocalKlipprToast no está proveído. Envuelve MainNavHost con CompositionLocalProvider.")
}

/**
 * Host visual: card chiquita flotante (toast) con auto-dismiss.
 * Colocar en un Box encima del NavHost, alineado abajo.
 */
@Composable
fun KlipprToastHost(
    hostState: KlipprToastHostState,
    modifier: Modifier = Modifier,
    durationMs: Long = 2600L,
) {
    val message = hostState.current
    val token = hostState.currentGeneration()

    LaunchedEffect(message, token) {
        if (message == null) return@LaunchedEffect
        delay(durationMs)
        hostState.dismissIfCurrent(token)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = message != null,
            enter = fadeIn(tween(180)) + slideInVertically(tween(220)) { it / 2 },
            exit = fadeOut(tween(160)) + slideOutVertically(tween(180)) { it / 3 },
        ) {
            message?.let { KlipprToastCard(it) }
        }
    }
}

@Composable
private fun KlipprToastCard(message: KlipprToastMessage) {
    val (icon, iconBg, iconTint) = when (message.kind) {
        KlipprToastKind.FAVORITE -> Triple(
            Icons.Filled.Favorite as ImageVector,
            Color(0xFFFBEFFA),
            KlipprPurple,
        )
        KlipprToastKind.REDEMPTION -> Triple(
            Icons.Filled.LocalOffer as ImageVector,
            KlipprLavender,
            KlipprPurple,
        )
    }

    Surface(
        modifier = Modifier
            .widthIn(max = 360.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 10.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message.title,
                    color = KlipprTextDark,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = message.subtitle,
                    color = KlipprTextGray,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
