package spl.cards.app.ui.component.modal.layout

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import coil.annotation.ExperimentalCoilApi
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import org.koin.androidx.compose.koinViewModel
import spl.cards.app.R
import spl.cards.app.model.WalletPolicy
import spl.cards.app.ui.component.*
import spl.cards.app.ui.screen.enableReaderMode
import spl.cards.app.ui.screen.initNfcAdapter
import spl.cards.app.ui.theme.BaeColor
import spl.cards.app.util.BackHandler
import spl.cards.app.util.Constants

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun WalletPolicyModal(
    close: (() -> Unit)? = null,
    onShowSnackbar: (String) -> Unit,
    onShowTransactionSnackbar: (String) -> Unit
) {
    val viewModel: WalletPolicyViewModel = koinViewModel()
    val walletPolicy: WalletPolicy by viewModel.onWalletPolicy.observeAsState(initial = WalletPolicy(address = null, allowList = listOf(), spendingWindow = listOf()))
    val nfcEnabled: Boolean by viewModel.onNFCEnabled.observeAsState(initial = false)
    val sendButtonEnabled: Boolean by viewModel.onSendButtonEnabled.observeAsState(initial = false)
    val sendTransactionLoading: Boolean by viewModel.onSendTransactionLoading.observeAsState(initial = false)
    val onFailure: Constants.ResultStatus by viewModel.onFailure.observeAsState(initial = Constants.ResultStatus.NOT_SET)
    val closeModal = viewModel.onCloseModal.collectAsState()
    val showConfetti = viewModel.onShowConfetti.collectAsState()
    val showPinCode = viewModel.onShowPinCode.collectAsState()
    val onTransactionId: String? by viewModel.onTransactionId.observeAsState(initial = null)

    // NFC Modal
    val context: Context = LocalContext.current
    val activity = context as Activity
    val nfcAdapter: NfcAdapter = initNfcAdapter(context)
    @Suppress("DEPRECATION") val vibrator =
        activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    if (closeModal.value) {
        viewModel.resetCloseModal()
        close?.invoke()
    }

    // Handle transaction snackbar.
    onTransactionId?.let { transactionId: String ->
        onShowTransactionSnackbar(transactionId)
        viewModel.clearTransactionId()
    }

    if (onFailure != Constants.ResultStatus.NOT_SET && onFailure != Constants.ResultStatus.SUCCESS) {
        when (onFailure) {
            Constants.ResultStatus.WRONG_PINCODE -> {
                onShowSnackbar(stringResource(id = R.string.modal_send_token_wrong_pincode_error))
            }

            else -> {
                onShowSnackbar(stringResource(id = R.string.label_error_has_occurred))
            }
        }
        viewModel.clearError()
    }

    enableReaderMode(activity = activity, nfcAdapter = nfcAdapter, onReadTag = { tag: Tag ->
        if (nfcEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        250,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
            viewModel.readNfcTag(tag)
        }
    })

    BackHandler(onBack = {
        if (nfcEnabled) {
            viewModel.disableNfc()
            return@BackHandler
        }
    })

    WalletPolicyModalLayout(
        walletPolicy = walletPolicy,
        closeModal = closeModal.value,
        sendButtonEnabled = sendButtonEnabled,
        sendTransactionLoading = sendTransactionLoading,
        showConfetti = showConfetti.value,
        showPinCode = showPinCode.value,
        nfcEnabled = nfcEnabled,
        onClickActivateNfc = {
            //SentryHelper.sendUserInteractionBreadcrumb(component = "Scan Button")
            if (nfcAdapter.isEnabled) {
                viewModel.enableNfc()
            } else {
                val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                context.startActivity(intent)
            }
        },
        onClickUpdateWalletPolicy = { allowList: List<String>, spendingWindow: List<Long>, pinCode: String ->
            //SentryHelper.sendUserInteractionBreadcrumb(component = "Send Transaction Button")
            viewModel.updateWalletPolicy(
                allowList = allowList,
                spendingWindow = spendingWindow,
                pinCode = pinCode
            )
        }
    )
}

@ExperimentalAnimationApi
@ExperimentalCoilApi
@Composable
fun WalletPolicyModalLayout(
    walletPolicy: WalletPolicy,
    closeModal: Boolean,
    sendButtonEnabled: Boolean,
    sendTransactionLoading: Boolean,
    showConfetti: Boolean,
    showPinCode: Boolean,
    nfcEnabled: Boolean,
    onClickActivateNfc: () -> Unit,
    onClickUpdateWalletPolicy: (List<String>, List<Long>, String) -> Unit
) {
    val pinCodeTextFieldValue = remember { mutableStateOf(TextFieldValue()) }
    val whiteListTextFieldValue = remember { mutableStateOf(TextFieldValue(walletPolicy.allowList.joinToString(","))) }
    val spendingWindow = remember { mutableStateOf(walletPolicy.spendingWindow) }

    if (closeModal) {
        whiteListTextFieldValue.value = TextFieldValue(text = "")
        pinCodeTextFieldValue.value = TextFieldValue(text = "")
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.9f)
            .padding(top = dimensionResource(id = R.dimen.padding_default))
    ) {
        if (showConfetti) {
            val sendTokenSuccess by rememberLottieComposition(LottieCompositionSpec.Asset("animations/confetti.json"))
            val sendTokenSuccessProgress by animateLottieCompositionAsState(
                sendTokenSuccess,
                iterations = 1
            )

            LottieAnimation(
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .padding(dimensionResource(id = R.dimen.padding_default)),
                composition = sendTokenSuccess,
                progress = sendTokenSuccessProgress,
                contentScale = ContentScale.Crop
            )
        }
        Column {
            Text(
                text = stringResource(id = R.string.modal_wallet_policy_message),
                style = MaterialTheme.typography.subtitle2,
                color = BaeColor
            )
            Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default)))
            WhitelistTextField(
                backgroundColor = Color(red = 118, green = 118, blue = 118, alpha = 0x1A),
                value = whiteListTextFieldValue.value,
                onValueChange = { whiteListTextFieldValue.value = it }
            )
            Text(
                text = stringResource(id = R.string.modal_wallet_policy_whitelist_separated),
                style = MaterialTheme.typography.subtitle2,
                color = BaeColor
            )
            Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default)))
            SpendingWindowPicker(preSelectedTimestamps = walletPolicy.spendingWindow, onTimestampsSelected = { timestamps: List<Long> ->
                spendingWindow.value = timestamps
            })
            Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default)))
            if (showPinCode) {
                Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default)))
                PinCodeTextField(
                    backgroundColor = Color(red = 118, green = 118, blue = 118, alpha = 0x1A),
                    value = pinCodeTextFieldValue.value,
                    onValueChange = { pinCodeTextFieldValue.value = it }
                )
            }
        }
        if (sendButtonEnabled) {
            GradientButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.BottomCenter),
                text = stringResource(id = R.string.button_update_wallet_policy),
                loading = sendTransactionLoading,
                onClick = {
                    val allowList: List<String> = whiteListTextFieldValue.value.text.takeIf { it.isNotEmpty() }
                        ?.split(",")
                        ?.map { it.trim() }
                        ?.filter { it.isNotEmpty() }
                        ?: listOf()

                    onClickUpdateWalletPolicy.invoke(
                        allowList,
                        spendingWindow.value,
                        pinCodeTextFieldValue.value.text
                    )
                    pinCodeTextFieldValue.value = TextFieldValue("")
                },
                enabled = sendTransactionLoading.not()
            )
        } else {
            GradientButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.BottomCenter),
                text = stringResource(id = R.string.button_scan),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_scan_24dp),
                        contentDescription = null
                    )
                },
                loading = nfcEnabled,
                loadingText = stringResource(id = R.string.modal_nfc_active_title),
                onClick = onClickActivateNfc,
                enabled = nfcEnabled.not()
            )
        }
    }
}
