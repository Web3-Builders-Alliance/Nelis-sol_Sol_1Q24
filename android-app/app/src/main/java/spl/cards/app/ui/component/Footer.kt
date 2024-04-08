package spl.cards.app.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import spl.cards.app.R
import spl.cards.app.ui.theme.BaeColor
import spl.cards.app.ui.theme.BlueButton
import spl.cards.app.util.Constants
import spl.cards.app.util.ThemedPreview

@Composable
fun Footer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val browserIntent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(Constants.HOME_PAGE_URL)) }

    Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
                text = stringResource(id = R.string.label_footer_title),
                style = MaterialTheme.typography.subtitle2,
                color = BaeColor)
        TextButton(onClick = {
            context.startActivity(browserIntent)
        }) {
            Text(
                    text = stringResource(id = R.string.label_footer_subtitle),
                    style = MaterialTheme.typography.subtitle2,
                    color = BlueButton)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFooterDark() {
    ThemedPreview(darkTheme = true) {
        Footer()
    }
}
