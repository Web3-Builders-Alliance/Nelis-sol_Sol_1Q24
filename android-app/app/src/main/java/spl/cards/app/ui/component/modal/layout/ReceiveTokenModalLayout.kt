package spl.cards.app.ui.component.modal.layout

import android.content.Context
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import coil.annotation.ExperimentalCoilApi
import spl.cards.app.R
import spl.cards.app.model.WalletItem
import spl.cards.app.ui.component.GradientButton
import spl.cards.app.ui.component.TokenDropdown
import spl.cards.app.ui.component.YourWalletAddressTextField
import spl.cards.app.ui.theme.BaeColor

@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun ReceiveTokenModalLayout(walletItems: List<WalletItem>) {
    if (walletItems.isNotEmpty()) {
        val context: Context = LocalContext.current
        val selectedWalletItem = remember { mutableStateOf(walletItems[0]) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.6f)
                .padding(top = dimensionResource(id = R.dimen.padding_default))
        ) {

            Column {
                TokenDropdown(
                    onTokenSelected = { walletItem: WalletItem ->
                        //SentryHelper.sendUserInteractionBreadcrumb(component = "Select token from Dropdown")
                        selectedWalletItem.value = walletItem
                    },
                    enabled = true,
                    walletItems = walletItems,
                    selectedWalletItem = selectedWalletItem.value
                )
                Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default)))
                YourWalletAddressTextField(textFieldValue = TextFieldValue(selectedWalletItem.value.publicKey))
                Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default)))
                Text(
                    text = stringResource(id = R.string.modal_receive_token_message),
                    style = MaterialTheme.typography.subtitle2,
                    color = BaeColor
                )
            }
            GradientButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.BottomCenter),
                text = stringResource(id = R.string.button_share),
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.ic_share_24dp), contentDescription = null)
                },
                onClick = {
                    //SentryHelper.sendUserInteractionBreadcrumb(component = "Share Button")
                    sharePublicKey(context = context, publicKey = selectedWalletItem.value.publicKey)
                }
            )
        }
    }
}

private fun sharePublicKey(context: Context, publicKey: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, publicKey)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, null))
}
