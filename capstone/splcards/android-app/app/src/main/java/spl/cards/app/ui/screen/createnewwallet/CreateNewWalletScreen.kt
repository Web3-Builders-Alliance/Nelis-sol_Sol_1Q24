package spl.cards.app.ui.screen.createnewwallet

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication
import spl.cards.app.R
import spl.cards.app.di.apiModule
import spl.cards.app.di.repositoryModule
import spl.cards.app.di.useCaseModule
import spl.cards.app.di.viewModelModule
import spl.cards.app.extension.navigateBack
import spl.cards.app.ui.component.AppLogoWithTitle
import spl.cards.app.ui.component.CustomSnackbar
import spl.cards.app.ui.component.GradientButton
import spl.cards.app.ui.component.PinCodeTextField
import spl.cards.app.ui.component.modal.BottomModal
import spl.cards.app.ui.component.modal.BottomSheetLayout
import spl.cards.app.ui.component.modal.layout.NFCModalState
import spl.cards.app.ui.screen.enableReaderMode
import spl.cards.app.ui.screen.initNfcAdapter
import spl.cards.app.ui.theme.BaeColor
import spl.cards.app.util.BackHandler
import spl.cards.app.util.Constants
import spl.cards.app.util.ThemedPreview

@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun CreateNewWalletScreen(navController: NavController, secretKey: String, isPreview: Boolean = false, viewModel: CreateNewWalletViewModel = koinViewModel()) {
    val context: Context = LocalContext.current
    var nfcAdapter: NfcAdapter? = null

    // States
    val nfcState: NFCModalState by viewModel.onNfcState.observeAsState(initial = NFCModalState.INITIAL)
    val onFailure: Constants.ResultStatus by viewModel.onFailure.observeAsState(initial = Constants.ResultStatus.NOT_SET)

    // Remembers
    val pinCodeTextFieldValue = remember { mutableStateOf(TextFieldValue()) }
    val scope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    if (!isPreview) {
        val activity = context as Activity

        nfcAdapter = initNfcAdapter(context)
        @Suppress("DEPRECATION") val vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        enableReaderMode(activity = activity, nfcAdapter = nfcAdapter, onReadTag = { tag: Tag ->
            if (viewModel.isNfcEnabled()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                viewModel.writeCard(navController = navController, tag = tag, secretKey = secretKey, pinCode = pinCodeTextFieldValue.value.text)
            }
        })
    }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val onShowSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    // Error Handling
    if (onFailure != Constants.ResultStatus.NOT_SET && onFailure != Constants.ResultStatus.SUCCESS) {
        when (onFailure) {
            Constants.ResultStatus.NFC_TAG_NOT_REGISTERED -> {
                onShowSnackbar(stringResource(id = R.string.create_new_wallet_nfc_tag_not_registered_error))
            }
            else -> {
                onShowSnackbar(stringResource(id = R.string.create_new_wallet_error))
            }
        }
        viewModel.clearError()
    }

    // BottomSheet
    val closeBottomSheet: () -> Unit = {
        scope.launch {
            modalBottomSheetState.hide()
        }
        viewModel.disableNfc()
    }

    val showBottomSheet: () -> Unit = {
        if (nfcAdapter?.isEnabled == true) {
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
        navController.navigateBack()
    })

    Box(modifier = Modifier.fillMaxSize()) {
        BottomModal(
            modalBottomSheetState = modalBottomSheetState,
            bottomSheetLayout = BottomSheetLayout.Nfc(state = nfcState),
            title = stringResource(id = R.string.modal_nfc_active_title),
            onClose = closeBottomSheet
        ) {
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
                    PinCodeTextField(
                        value = pinCodeTextFieldValue.value,
                        onValueChange = { textFieldValue: TextFieldValue ->
                            pinCodeTextFieldValue.value = textFieldValue
                        },
                        label = stringResource(id = R.string.create_new_wallet_enter_optionally_code_step)
                    )
                    Spacer(modifier = Modifier.padding(bottom = 20.dp))
                    Text(
                        modifier = Modifier.padding(
                            start = dimensionResource(id = R.dimen.padding_default_half),
                            bottom = dimensionResource(id = R.dimen.padding_default_half)
                        ),
                        text = stringResource(id = R.string.create_new_wallet_write_key_step),
                        style = MaterialTheme.typography.subtitle1,
                        color = BaeColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(bottom = 20.dp))
                    GradientButton(
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(painter = painterResource(id = R.drawable.ic_scan_24dp), contentDescription = null)
                        },
                        text = stringResource(id = R.string.button_scan),
                        onClick = {
                            //SentryHelper.sendUserInteractionBreadcrumb(component = "Scan Button")
                            scope.launch {
                                showBottomSheet()
                            }
                        },
                        enabled = pinCodeTextFieldValue.value.text.isEmpty() || pinCodeTextFieldValue.value.text.length == 6
                    )
                }
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

@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun PreviewCreateNewWalletScreenDark() {
    ScreenPreview {
        ThemedPreview(darkTheme = true) {
            CreateNewWalletScreen(navController = rememberNavController(), secretKey = "secretKey", isPreview = true)
        }
    }
}

@Composable
fun ScreenPreview(
    screen: @Composable () -> Unit
) {
    val context = LocalContext.current
    KoinApplication(application = {
        androidContext(context)
        modules(viewModelModule, useCaseModule, apiModule, repositoryModule)
    }) { screen() }
}
