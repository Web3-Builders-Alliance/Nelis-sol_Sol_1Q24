package spl.cards.app.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import spl.cards.app.R
import spl.cards.app.ui.theme.BlueButton
import spl.cards.app.util.ThemedPreview

@Composable
fun RoundedIconButton(enabled: Boolean = true, onClick: () -> Unit, content: @Composable () -> Unit) {
    IconButton(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier
            .width(dimensionResource(id = R.dimen.button_min_height))
            .height(dimensionResource(id = R.dimen.button_min_height))
            .background(color = BlueButton, shape = CircleShape)
    ) {
        content.invoke()
    }
}

@Preview
@Composable
private fun PreviewRoundedButtonDark() {
    ThemedPreview(darkTheme = true) {
        RoundedIconButton(onClick = {}) {
            Icon(painter = painterResource(id = R.drawable.ic_arrow_up_right_24dp), contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun RoundedBorderIconButton(enabled: Boolean = true, onClick: () -> Unit, content: @Composable () -> Unit) {
    IconButton(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier
            .width(dimensionResource(id = R.dimen.button_min_height))
            .height(dimensionResource(id = R.dimen.button_min_height))
            .background(color = Color.Transparent, shape = CircleShape)
            .border(BorderStroke(width = 2.dp, color = BlueButton), shape = CircleShape)
    ) {
        content()
    }
}

@Preview
@Composable
private fun PreviewRoundedBorderIconButtonDark() {
    ThemedPreview(darkTheme = true) {
        RoundedBorderIconButton(onClick = {}) {
            Icon(painter = painterResource(id = R.drawable.ic_arrow_up_right_24dp), contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun GradientButton(
    modifier: Modifier = Modifier,
    text: String,
    loading: Boolean = false,
    loadingText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = { },
    enabled: Boolean = true
) {
    Button(
        modifier = Modifier
            .requiredHeight(dimensionResource(id = R.dimen.button_min_height))
            .then(modifier),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        contentPadding = PaddingValues(),
        onClick = { onClick() },
        enabled = enabled,
        shape = RoundedCornerShape(15)
    ) {
        Box(
            modifier = Modifier
                .requiredHeight(dimensionResource(id = R.dimen.button_min_height))
                .background(Brush.horizontalGradient(listOf(Color(0xFFC438F0), Color(0xFF7195C9), Color(0xFF6EEAAE))))
                .then(modifier),
            contentAlignment = Alignment.Center,
        ) {
            Row {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    if (loadingText != null) {
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Text(text = loadingText)
                    }
                } else {
                    leadingIcon?.invoke()
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(text = text)
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    trailingIcon?.invoke()
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewGradientButtonDark() {
    ThemedPreview(darkTheme = true) {
        GradientButton(modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.ic_scan_24dp), contentDescription = null)
            },
            text = "Scan",
            onClick = {})
    }
}
