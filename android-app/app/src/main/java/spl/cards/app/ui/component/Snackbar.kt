package spl.cards.app.ui.component

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import spl.cards.app.ui.theme.BaeColor
import spl.cards.app.ui.theme.BlueButton
import spl.cards.app.ui.theme.BoxBackground

@Composable
fun CustomSnackbar(modifier: Modifier, snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        modifier = modifier,
        hostState = snackbarHostState,
        snackbar = { snackbarData: SnackbarData ->
            Snackbar(backgroundColor = BoxBackground, action = {
                snackbarData.actionLabel?.let { actionLabel: String ->
                    TextButton(onClick = {
                        snackbarHostState.currentSnackbarData?.performAction()
                    }) {
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.subtitle2,
                            color = BlueButton,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }) {
                Text(text = snackbarData.message, style = MaterialTheme.typography.subtitle2, color = BaeColor)
            }
        }
    )
}
