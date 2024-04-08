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
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
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
import spl.cards.app.model.WalletItem
import spl.cards.app.ui.component.*
import spl.cards.app.ui.screen.enableReaderMode
import spl.cards.app.ui.screen.initNfcAdapter
import spl.cards.app.util.BackHandler
import spl.cards.app.util.Constants

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun SendTokenModal(
    walletItems: List<WalletItem>,
    selectedWalletItem: WalletItem? = null,
    close: (() -> Unit)? = null,
    onShowSnackbar: (String) -> Unit,
    onShowTransactionSnackbar: (String) -> Unit
) {
    if (walletItems.isNotEmpty()) {
        val viewModel: SendTokenViewModel = koinViewModel()
        val nfcEnabled: Boolean by viewModel.onNFCEnabled.observeAsState(initial = false)
        val sendButtonEnabled: Boolean by viewModel.onSendButtonEnabled.observeAsState(initial = false)
        val sendTransactionLoading: Boolean by viewModel.onSendTransactionLoading.observeAsState(initial = false)
        val onFailure: Constants.ResultStatus by viewModel.onFailure.observeAsState(initial = Constants.ResultStatus.NOT_SET)
        val closeModal = viewModel.onCloseModal.collectAsState()
        val showConfetti = viewModel.onShowConfetti.collectAsState()
        val showPinCode = viewModel.onShowPinCode.collectAsState()
        val onTransactionId: String? by viewModel.onTransactionId.observeAsState(initial = null)
        val onSelectedWalletItem: WalletItem? by viewModel.onSelectedWalletItem.observeAsState(initial = null)

        // NFC Modal
        val context: Context = LocalContext.current
        val activity = context as Activity
        val nfcAdapter: NfcAdapter = initNfcAdapter(context)
        @Suppress("DEPRECATION") val vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        LaunchedEffect(key1 = selectedWalletItem, block = {
            viewModel.selectWalletItem(selectedWalletItem ?: walletItems[0])
        })

        if (closeModal.value) {
            viewModel.resetCloseModal()
            viewModel.unselectWalletItem()
            close?.invoke()
        }

        // Handle transaction snackbar.
        onTransactionId?.let { transactionId: String ->
            onShowTransactionSnackbar(transactionId)
            viewModel.clearTransactionId()
        }

        if (onFailure != Constants.ResultStatus.NOT_SET && onFailure != Constants.ResultStatus.SUCCESS) {
            when (onFailure) {
                Constants.ResultStatus.SAME_ADDRESSES -> {
                    onShowSnackbar(stringResource(id = R.string.modal_send_token_same_addresses_error))
                }
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
                    vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE))
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

        SendTokenModalLayout(
            closeModal = closeModal.value,
            walletItems = walletItems,
            currentSelectedWalletItem = onSelectedWalletItem ?: walletItems[0],
            sendButtonEnabled = sendButtonEnabled,
            sendTransactionLoading = sendTransactionLoading,
            showConfetti = showConfetti.value,
            showPinCode = showPinCode.value,
            nfcEnabled = nfcEnabled,
            onTokenSelected = { walletItem: WalletItem ->
                //SentryHelper.sendUserInteractionBreadcrumb(component = "Select token from Dropdown")
                viewModel.selectWalletItem(walletItem = walletItem)
            },
            onClickActivateNfc = {
                //SentryHelper.sendUserInteractionBreadcrumb(component = "Scan Button")
                if (nfcAdapter.isEnabled) {
                    viewModel.enableNfc()
                } else {
                    val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                    context.startActivity(intent)
                }
            },
            onClickSendTransaction = { walletItem: WalletItem, destinationPublicKey: String, amount: Double, pinCode: String ->
                //SentryHelper.sendUserInteractionBreadcrumb(component = "Send Transaction Button")
                viewModel.sendTransaction(
                    walletItem = walletItem,
                    destinationPublicKey = destinationPublicKey,
                    amount = amount,
                    pinCode = pinCode
                )
            }
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalCoilApi
@Composable
fun SendTokenModalLayout(
    closeModal: Boolean,
    walletItems: List<WalletItem>,
    currentSelectedWalletItem: WalletItem,
    sendButtonEnabled: Boolean,
    sendTransactionLoading: Boolean,
    showConfetti: Boolean,
    showPinCode: Boolean,
    nfcEnabled: Boolean,
    onTokenSelected: (WalletItem) -> Unit,
    onClickActivateNfc: () -> Unit,
    onClickSendTransaction: (WalletItem, String, Double, String) -> Unit
) {
    val amountTextFieldValue = remember { mutableStateOf(TextFieldValue()) }
    val walletAddressTextFieldValue = remember { mutableStateOf(TextFieldValue()) }
    val pinCodeTextFieldValue = remember { mutableStateOf(TextFieldValue()) }

    if (closeModal) {
        amountTextFieldValue.value = TextFieldValue(text = "")
        walletAddressTextFieldValue.value = TextFieldValue(text = "")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.75f)
            .padding(top = dimensionResource(id = R.dimen.padding_default))
    ) {
        if (showConfetti) {
            val sendTokenSuccess by rememberLottieComposition(LottieCompositionSpec.Asset("animations/confetti.json"))
            val sendTokenSuccessProgress by animateLottieCompositionAsState(sendTokenSuccess, iterations = 1)

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
            TokenDropdown(
                onTokenSelected = onTokenSelected,
                enabled = true,
                walletItems = walletItems,
                selectedWalletItem = currentSelectedWalletItem
            )
            Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default)))
            AmountTextField(
                value = amountTextFieldValue.value,
                onValueChange = { amountTextFieldValue.value = it },
                onClickMax = {
                    amountTextFieldValue.value = TextFieldValue(currentSelectedWalletItem.uiTotalAmount.toString())
                }
            )
            Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default)))
            WalletAddressTextField(value = walletAddressTextFieldValue.value, onValueChange = { walletAddressTextFieldValue.value = it })
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
                text = stringResource(id = R.string.button_confirm_transaction),
                loading = sendTransactionLoading,
                onClick = {
                    onClickSendTransaction.invoke(
                        currentSelectedWalletItem,
                        walletAddressTextFieldValue.value.text.trim(),
                        amountTextFieldValue.value.text.toDouble(),
                        pinCodeTextFieldValue.value.text
                    )
                    pinCodeTextFieldValue.value = TextFieldValue("")
                },
                enabled = sendTransactionLoading.not() && amountTextFieldValue.value.text.trim().isNotEmpty() &&
                        walletAddressTextFieldValue.value.text.trim().isNotEmpty()
            )
        } else {
            GradientButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.BottomCenter),
                text = stringResource(id = R.string.button_scan),
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.ic_scan_24dp), contentDescription = null)
                },
                loading = nfcEnabled,
                loadingText = stringResource(id = R.string.modal_nfc_active_title),
                onClick = onClickActivateNfc,
                enabled = nfcEnabled.not() && amountTextFieldValue.value.text.trim().isNotEmpty() &&
                        walletAddressTextFieldValue.value.text.trim().isNotEmpty()
            )
        }
    }
}
