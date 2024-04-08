package spl.cards.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GradientDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    alpha: Float = 0.12f
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(thickness)
            .background(brush = Brush.horizontalGradient(listOf(Color(0xFFC438F0), Color(0xFF7195C9), Color(0xFF6EEAAE))), alpha = alpha)
    )
}
