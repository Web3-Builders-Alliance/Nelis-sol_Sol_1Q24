package spl.cards.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import spl.cards.app.R

@Composable
fun InfoBox(modifier: Modifier, text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(red = 192, green = 57, blue = 43, alpha = 48), shape = RoundedCornerShape(25))
            .padding(all = dimensionResource(id = R.dimen.padding_default_half))
            .then(modifier),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = Color(red = 192, green = 57, blue = 43))
            Spacer(modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_default_half)))
            Text(text = text, style = MaterialTheme.typography.subtitle2, color = Color(red = 192, green = 57, blue = 43))
        }
    }
}

@Preview
@Composable
private fun PreviewInfoBox() {
    InfoBox(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.label_recovery_phrase_warning)
    )
}
