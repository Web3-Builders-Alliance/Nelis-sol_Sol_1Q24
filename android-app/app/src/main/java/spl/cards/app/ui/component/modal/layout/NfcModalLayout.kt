package spl.cards.app.ui.component.modal.layout

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import spl.cards.app.R
import spl.cards.app.ui.component.GradientButton
import spl.cards.app.ui.component.PinCodeTextField
import spl.cards.app.ui.theme.BaeColor

enum class NFCModalState {
    INITIAL,
    READ_SUCCESS,
    WRITE_SUCCESS,
    FAILURE,
    PIN_CODE
}

@ExperimentalAnimationApi
@Composable
fun NFCModalLayout(state: NFCModalState, onPinCode: ((String) -> Unit)? = null) {
    val pinCodeTextFieldValue = remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.3f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state == NFCModalState.PIN_CODE) {
            PinCodeContent(pinCodeTextFieldValue = pinCodeTextFieldValue.value,
                onValueChange = { pinCodeTextFieldValue.value = it },
                onClickUnlock = {
                    onPinCode?.invoke(pinCodeTextFieldValue.value.text)
                }
            )
        } else {
            NfcContent(state = state)
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun PinCodeContent(
    pinCodeTextFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onClickUnlock: () -> Unit
) {
    PinCodeTextField(
        backgroundColor = Color(red = 118, green = 118, blue = 118, alpha = 0x1A),
        value = pinCodeTextFieldValue,
        onValueChange = onValueChange
    )
    Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default)))
    GradientButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.button_unlock),
        onClick = onClickUnlock,
        enabled = pinCodeTextFieldValue.text.length == 6
    )
}

@Composable
private fun NfcContent(state: NFCModalState) {
    val lottiePair: Pair<LottieComposition?, Float>? = when (state) {
        NFCModalState.INITIAL -> {
            val readNFCLoading by rememberLottieComposition(LottieCompositionSpec.Asset("animations/read-nfc-loading.json"))
            val readNFCLoadingProgress by animateLottieCompositionAsState(readNFCLoading, iterations = LottieConstants.IterateForever)

            Pair(readNFCLoading, readNFCLoadingProgress)
        }
        NFCModalState.READ_SUCCESS -> {
            val readNFCSuccess by rememberLottieComposition(LottieCompositionSpec.Asset("animations/read-nfc-success.json"))
            val readNFCSuccessProgress by animateLottieCompositionAsState(readNFCSuccess, iterations = 1)

            Pair(readNFCSuccess, readNFCSuccessProgress)
        }
        NFCModalState.WRITE_SUCCESS -> {
            val writeNFCSuccess by rememberLottieComposition(LottieCompositionSpec.Asset("animations/confetti.json"))
            val writeNFCSuccessProgress by animateLottieCompositionAsState(writeNFCSuccess, iterations = 1)

            Pair(writeNFCSuccess, writeNFCSuccessProgress)
        }
        NFCModalState.FAILURE -> {
            val readNFCFailure by rememberLottieComposition(LottieCompositionSpec.Asset("animations/read-nfc-failure.json"))
            val readNFCFailureProgress by animateLottieCompositionAsState(readNFCFailure, iterations = 1)

            Pair(readNFCFailure, readNFCFailureProgress)
        }
        NFCModalState.PIN_CODE -> {
            // Nothing to do.
            null
        }
    }

    val message: String? = when (state) {
        NFCModalState.INITIAL -> {
            stringResource(id = R.string.modal_nfc_active_subtitle)
        }
        NFCModalState.READ_SUCCESS -> {
            stringResource(id = R.string.modal_nfc_active_subtitle)
        }
        NFCModalState.WRITE_SUCCESS -> {
            stringResource(id = R.string.modal_nfc_write_success_subtitle)
        }
        NFCModalState.FAILURE -> {
            stringResource(id = R.string.modal_nfc_active_subtitle)
        }
        NFCModalState.PIN_CODE -> {
            // Nothing to do.
            null
        }
    }

    LottieAnimation(
        modifier = Modifier.size(200.dp, 50.dp),
        composition = lottiePair!!.first,
        progress = lottiePair.second,
        contentScale = ContentScale.Crop
    )
    Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default)))
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = message!!,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.subtitle1, color = BaeColor
    )
}
