package com.example.klippr.shared.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.klippr.R
import kotlinx.coroutines.delay

// @author Samuel Bonifacio

/** SplashScreen. */
@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        delay(1200)
        onTimeout()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Surface(color = Color.White, shape = RoundedCornerShape(28.dp)) {
            AsyncImage(
                model = R.drawable.klippr_lockup,
                contentDescription = "Klippr",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .heightIn(max = 260.dp)
                    .padding(20.dp),
            )
        }
    }
}
