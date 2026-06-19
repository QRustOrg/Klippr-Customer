package com.example.klippr.shared.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.klippr.promotions.domain.model.DiscountType
import com.example.klippr.ui.theme.KlipprPurple

// @author Samuel Bonifacio
// Formato + badge de descuento compartidos (antes duplicados en Home/Explore/PromotionList/MisPromos).

/** Etiqueta unificada. [type] nulo => porcentaje (códigos de canje legados sin tipo). */
fun discountLabel(type: DiscountType?, value: Double): String = when (type) {
    DiscountType.FIXED_AMOUNT -> "S/ ${value.toInt()} OFF"
    else -> "${value.toInt()}% OFF"
}

@Composable
fun DiscountBadge(type: DiscountType?, value: Double, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(KlipprPurple)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(discountLabel(type, value), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
