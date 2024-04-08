package spl.cards.app.util

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import spl.cards.app.ui.theme.SPLCardsTheme

@Composable
internal fun ThemedPreview(
        darkTheme: Boolean = false,
        content: @Composable () -> Unit
) {
    SPLCardsTheme(darkTheme = darkTheme) {
        Surface {
            content()
        }
    }
}
