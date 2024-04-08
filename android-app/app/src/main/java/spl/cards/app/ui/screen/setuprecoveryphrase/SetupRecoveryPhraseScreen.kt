package spl.cards.app.ui.screen.setuprecoveryphrase

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import spl.cards.app.R
import spl.cards.app.extension.navigateBack
import spl.cards.app.ui.component.*
import spl.cards.app.util.BackHandler
import spl.cards.app.util.ThemedPreview

@ExperimentalAnimationApi
@Composable
fun SetupRecoveryPhraseScreen(
    navController: NavController,
    viewModel: SetupRecoveryPhraseViewModel = koinViewModel()
) {
    BackHandler(
        onBack = {
            navController.navigateBack()
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp, horizontal = dimensionResource(id = R.dimen.padding_default))
    ) {
        IconButton(modifier = Modifier.size(24.dp),
            onClick = {
                //SentryHelper.sendUserInteractionBreadcrumb(component = "Back Button")
                navController.navigateBack()
            }
        ) {
            Image(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)
        }
        Column(modifier = Modifier.fillMaxSize()) {
            AppLogoWithTitle(title = stringResource(id = R.string.new_wallet_title))
            Spacer(modifier = Modifier.padding(bottom = 40.dp))
            RecoveryPhraseTextField(textFieldValue = TextFieldValue(text = viewModel.mnemonicCode.joinToString(" ")))
            Spacer(modifier = Modifier.padding(bottom = 20.dp))
            InfoBox(modifier = Modifier.fillMaxWidth(), text = stringResource(id = R.string.label_recovery_phrase_warning))
            Spacer(modifier = Modifier.padding(bottom = 40.dp))
            GradientButton(
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null)
                },
                text = stringResource(id = R.string.button_next),
                onClick = {
                    //SentryHelper.sendUserInteractionBreadcrumb(component = "Next Button")
                    viewModel.navigateToCreateNewWallet(navController = navController)
                }
            )
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
private fun PreviewCreateNewWalletScreenDark() {
    ThemedPreview(darkTheme = true) {
        SetupRecoveryPhraseScreen(navController = rememberNavController())
    }
}
