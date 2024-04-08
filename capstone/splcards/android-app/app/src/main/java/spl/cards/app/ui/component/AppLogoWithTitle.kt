package spl.cards.app.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import spl.cards.app.R
import spl.cards.app.util.ThemedPreview

@Composable
fun AppLogoWithTitle(title: String) {
    Column(
            modifier = Modifier.fillMaxWidth()
    ) {
        Image(
                modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(dimensionResource(id = R.dimen.app_logo_size))
                        .width(dimensionResource(id = R.dimen.app_logo_size)),
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = null)
        Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_default)))
        Text(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                text = title,
                style = MaterialTheme.typography.h2)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAppLogoWithTitleDark() {
    ThemedPreview(darkTheme = true) {
        AppLogoWithTitle(title = stringResource(id = R.string.app_name))
    }
}
