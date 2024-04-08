package spl.cards.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
)

val BottomSheetShape = RoundedCornerShape(
        topStartPercent = 5,
        topEndPercent = 5,
        bottomStartPercent = 0,
        bottomEndPercent = 0
)
