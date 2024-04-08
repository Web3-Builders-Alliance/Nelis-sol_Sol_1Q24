package spl.cards.app.ui.component.modal.layout

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import coil.annotation.ExperimentalCoilApi
import spl.cards.app.R
import spl.cards.app.ui.component.*

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun SecurityModal(
    onClickOpenWalletPolicyModal: () -> Unit,
    onClickOpenTokenPolicyModal: () -> Unit
) {
    SecurityModalLayout(
        onClickOpenWalletPolicyModal = onClickOpenWalletPolicyModal,
        onClickOpenTokenPolicyModal = onClickOpenTokenPolicyModal
    )
}

@ExperimentalAnimationApi
@ExperimentalCoilApi
@Composable
fun SecurityModalLayout(
    onClickOpenWalletPolicyModal: () -> Unit,
    onClickOpenTokenPolicyModal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.25f)
            .padding(top = dimensionResource(id = R.dimen.padding_default))
    ) {
        GradientButton(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(id = R.string.button_wallet_security),
            onClick = onClickOpenWalletPolicyModal
        )
        Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default)))
        GradientButton(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(id = R.string.button_token_security),
            onClick = onClickOpenTokenPolicyModal
        )
    }
}
