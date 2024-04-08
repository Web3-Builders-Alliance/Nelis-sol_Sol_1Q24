package spl.cards.app.ui.screen.getstarted

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import spl.cards.app.R
import spl.cards.app.extension.navigate
import spl.cards.app.ui.component.AppLogoWithTitle
import spl.cards.app.ui.component.CustomSnackbar
import spl.cards.app.ui.component.Footer
import spl.cards.app.ui.component.modal.BottomModal
import spl.cards.app.ui.component.modal.BottomSheetLayout
import spl.cards.app.ui.component.modal.layout.NFCModalState
import spl.cards.app.ui.screen.enableReaderMode
import spl.cards.app.ui.screen.initNfcAdapter
import spl.cards.app.ui.theme.BaeColor
import spl.cards.app.ui.theme.BoxBackground
import spl.cards.app.ui.theme.IconColor
import spl.cards.app.util.BackHandler
import spl.cards.app.util.Constants
import spl.cards.app.util.ThemedPreview

@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun GetStartedScreen(navController: NavController, viewModel: GetStartedViewModel = koinViewModel()) {
    val nfcState: NFCModalState by viewModel.onNfcState.observeAsState(initial = NFCModalState.INITIAL)
    val onFailure: Constants.ResultStatus by viewModel.onFailure.observeAsState(initial = Constants.ResultStatus.NOT_SET)

    val context: Context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, confirmStateChange = { false })
    val nfcAdapter: NfcAdapter = initNfcAdapter(context)
    @Suppress("DEPRECATION") val vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val onShowSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    if (onFailure != Constants.ResultStatus.NOT_SET && onFailure != Constants.ResultStatus.SUCCESS) {
        when (onFailure) {
            Constants.ResultStatus.WRONG_PINCODE -> {
                onShowSnackbar(stringResource(id = R.string.get_started_wrong_pincode_error))
            }
            else -> {
                onShowSnackbar(stringResource(id = R.string.get_started_error))
            }
        }
        viewModel.clearError()
    }

    enableReaderMode(activity = activity, nfcAdapter = nfcAdapter, onReadTag = { tag: Tag ->
        if (viewModel.isNfcEnabled()) {
            //SentryHelper.sendInfoBreadcrumb(category = "NFC", message = "NFC Tag detected.")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            viewModel.readCard(navController, tag)
        }
    })

    val closeBottomSheet: () -> Unit = {
        scope.launch {
            modalBottomSheetState.hide()
        }
        viewModel.disableNfc()
    }

    val showBottomSheet: () -> Unit = {
        if (nfcAdapter.isEnabled) {
            scope.launch {
                modalBottomSheetState.show()
            }
            viewModel.enableNfc()
        } else {
            val intent = Intent(Settings.ACTION_NFC_SETTINGS)
            context.startActivity(intent)
        }
    }

    BackHandler(onBack = {
        if (modalBottomSheetState.isVisible) {
            closeBottomSheet()
            return@BackHandler
        }
        activity.finish()
    })

    Box(modifier = Modifier.fillMaxSize()) {
        BottomModal(
            modalBottomSheetState = modalBottomSheetState,
            bottomSheetLayout = BottomSheetLayout.Nfc(state = nfcState, onPinCode = { pinCode: String ->
                viewModel.decryptSeed(navController = navController, pinCode = pinCode)
            }),
            title = if (nfcState == NFCModalState.PIN_CODE) stringResource(id = R.string.modal_nfc_pincode_title) else stringResource(id = R.string.modal_nfc_active_title),
            onClose = closeBottomSheet
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 40.dp, horizontal = dimensionResource(id = R.dimen.padding_default))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    AppLogoWithTitle(title = stringResource(id = R.string.get_started_title))
                    Spacer(modifier = Modifier.padding(bottom = 40.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ScanWalletBox(modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(25))
                            .background(color = BoxBackground)
                            .clickable {
                                //SentryHelper.sendUserInteractionBreadcrumb(component = "ScanWalletBox")
                                showBottomSheet()
                            }
                            .padding(dimensionResource(id = R.dimen.padding_default)))
                        Spacer(modifier = Modifier.padding(top = 40.dp))
                        OrDivider()
                        Spacer(modifier = Modifier.padding(bottom = 40.dp))
                        SetUpNewWalletBox(modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(25))
                            .background(color = BoxBackground)
                            .clickable {
                                //SentryHelper.sendUserInteractionBreadcrumb(component = "SetUpNewWalletBox")
                                navController.navigate(from = Constants.Screen.GetStarted, to = Constants.Screen.SetupRecoveryPhrase)
                            }
                            .padding(dimensionResource(id = R.dimen.padding_default)))
                    }
                }
                Footer(modifier = Modifier.align(alignment = Alignment.BottomCenter))
            }
        }
        CustomSnackbar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(vertical = 40.dp, horizontal = dimensionResource(id = R.dimen.padding_default)),
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(modifier = Modifier.weight(0.4f), color = IconColor)
        Text(
            modifier = Modifier.weight(0.2f),
            text = stringResource(id = R.string.label_or),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle1, color = BaeColor
        )
        Divider(modifier = Modifier.weight(0.4f), color = IconColor)
    }
}

@Preview
@Composable
private fun PreviewOrDivider() {
    ThemedPreview(darkTheme = true) {
        OrDivider()
    }
}

@Composable
private fun ScanWalletBox(modifier: Modifier) {
    Box(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.ic_scan_24dp), contentDescription = null)
            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_default_half)))
            Column {
                Text(text = stringResource(id = R.string.get_started_scan_wallet_box_title), style = MaterialTheme.typography.h3)
                Text(text = stringResource(id = R.string.get_started_scan_wallet_box_subtitle), style = MaterialTheme.typography.subtitle2, color = BaeColor)
            }
        }
        Icon(
            modifier = Modifier.align(alignment = Alignment.CenterEnd),
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null,
            tint = IconColor
        )
    }
}

@Preview
@Composable
private fun PreviewScanWalletBox() {
    ThemedPreview(darkTheme = true) {
        ScanWalletBox(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(25))
                .background(color = BoxBackground)
                .padding(dimensionResource(id = R.dimen.padding_default))
        )
    }
}

@Composable
private fun SetUpNewWalletBox(modifier: Modifier) {
    Box(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.ic_wallet_24dp), contentDescription = null)
            Spacer(modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_default_half)))
            Column {
                Text(text = stringResource(id = R.string.get_started_set_up_wallet_box_title), style = MaterialTheme.typography.h3)
                Text(text = stringResource(id = R.string.get_started_set_up_wallet_box_subtitle), style = MaterialTheme.typography.subtitle2, color = BaeColor)
            }
        }
        Icon(
            modifier = Modifier.align(alignment = Alignment.CenterEnd),
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null,
            tint = IconColor
        )
    }
}

@Preview
@Composable
private fun PreviewSetUpNewWalletBox() {
    ThemedPreview(darkTheme = true) {
        SetUpNewWalletBox(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(25))
                .background(color = BoxBackground)
                .padding(dimensionResource(id = R.dimen.padding_default))
        )
    }
}

@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun PreviewGetStartedScreenDark() {
    ThemedPreview(darkTheme = true) {
        GetStartedScreen(navController = rememberNavController())
    }
}
