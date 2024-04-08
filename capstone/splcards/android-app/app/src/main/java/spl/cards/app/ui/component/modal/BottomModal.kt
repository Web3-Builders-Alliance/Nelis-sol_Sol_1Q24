package spl.cards.app.ui.component.modal

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import spl.cards.app.R
import spl.cards.app.model.WalletItem
import spl.cards.app.ui.component.modal.layout.NFCModalLayout
import spl.cards.app.ui.component.modal.layout.NFCModalState
import spl.cards.app.ui.component.modal.layout.ReceiveTokenModalLayout
import spl.cards.app.ui.component.modal.layout.SecurityModal
import spl.cards.app.ui.component.modal.layout.SendTokenModal
import spl.cards.app.ui.component.modal.layout.TokenPolicyModal
import spl.cards.app.ui.component.modal.layout.WalletPolicyModal
import spl.cards.app.ui.component.modal.layout.WrapTokenModal
import spl.cards.app.ui.theme.BottomSheetScrimColor
import spl.cards.app.ui.theme.BottomSheetShape
import spl.cards.app.ui.theme.BoxBackground
import spl.cards.app.ui.theme.WhiteSnow
import spl.cards.app.util.ThemedPreview

sealed class BottomSheetLayout(val name: String) {
    object NotSet : BottomSheetLayout(name = "NotSet")
    class Nfc(
        val state: NFCModalState,
        val onPinCode: ((String) -> Unit)? = null
    ) : BottomSheetLayout(name = "NFC")

    class SendToken(
        val walletItems: List<WalletItem>,
        val selectedWalletItem: WalletItem? = null,
        val close: (() -> Unit)? = null,
        val onShowSnackbar: (String) -> Unit,
        val onShowTransactionSnackbar: (String) -> Unit
    ) : BottomSheetLayout(name = "SendToken")

    class ReceiveToken(val walletItems: List<WalletItem>) : BottomSheetLayout(name = "ReceiveToken")

    class WrapToken(
        val walletItems: List<WalletItem>,
        val selectedWalletItem: WalletItem? = null,
        val close: (() -> Unit)? = null,
        val onShowSnackbar: (String) -> Unit,
        val onShowTransactionSnackbar: (String) -> Unit
    ) : BottomSheetLayout(name = "WrapToken")

    class SecurityModal(
        val onClickOpenWalletPolicyModal: () -> Unit,
        val onClickOpenTokenPolicyModal: () -> Unit
    ) : BottomSheetLayout(name = "Security")

    class WalletPolicy(
        val close: (() -> Unit)? = null,
        val onShowSnackbar: (String) -> Unit,
        val onShowTransactionSnackbar: (String) -> Unit
    ) : BottomSheetLayout(name = "WalletPolicy")

    class TokenPolicy(
        val walletItems: List<WalletItem>,
        val selectedWalletItem: WalletItem? = null,
        val close: (() -> Unit)? = null,
        val onShowSnackbar: (String) -> Unit,
        val onShowTransactionSnackbar: (String) -> Unit
    ) : BottomSheetLayout(name = "TokenPolicy")
}

@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun BottomModal(
    modalBottomSheetState: ModalBottomSheetState,
    bottomSheetLayout: BottomSheetLayout,
    scrimColor: Color = BottomSheetScrimColor,
    sheetBackground: Color = BoxBackground,
    title: String? = null,
    onClose: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {

    // Track BottomModal.
    LaunchedEffect(key1 = modalBottomSheetState.isVisible, block = {
        if (modalBottomSheetState.isVisible) {
            //SentryHelper.sendInfoBreadcrumb(category = "BottomModal", message = "Show ${bottomSheetLayout.name}")
        } else {
            //SentryHelper.sendInfoBreadcrumb(category = "BottomModal", message = "Hide ${bottomSheetLayout.name}")
        }
    })

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetShape = BottomSheetShape,
        scrimColor = scrimColor,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(color = sheetBackground)
                    .padding(
                        vertical = dimensionResource(id = R.dimen.padding_default),
                        horizontal = dimensionResource(id = R.dimen.padding_default)
                    )
            ) {
                Column {
                    if (title != null || onClose != null) {
                        BottomModalHeader(title = title, onClose = onClose)
                    }
                    when (bottomSheetLayout) {
                        is BottomSheetLayout.Nfc -> NFCModalLayout(
                            state = bottomSheetLayout.state,
                            onPinCode = bottomSheetLayout.onPinCode
                        )

                        is BottomSheetLayout.SendToken -> SendTokenModal(
                            walletItems = bottomSheetLayout.walletItems,
                            selectedWalletItem = bottomSheetLayout.selectedWalletItem,
                            close = bottomSheetLayout.close,
                            onShowSnackbar = bottomSheetLayout.onShowSnackbar,
                            onShowTransactionSnackbar = bottomSheetLayout.onShowTransactionSnackbar
                        )

                        is BottomSheetLayout.ReceiveToken -> ReceiveTokenModalLayout(walletItems = bottomSheetLayout.walletItems)

                        is BottomSheetLayout.WrapToken -> WrapTokenModal(
                            walletItems = bottomSheetLayout.walletItems,
                            selectedWalletItem = bottomSheetLayout.selectedWalletItem,
                            close = bottomSheetLayout.close,
                            onShowSnackbar = bottomSheetLayout.onShowSnackbar,
                            onShowTransactionSnackbar = bottomSheetLayout.onShowTransactionSnackbar
                        )

                        is BottomSheetLayout.SecurityModal -> SecurityModal(
                            onClickOpenWalletPolicyModal =  bottomSheetLayout.onClickOpenWalletPolicyModal,
                            onClickOpenTokenPolicyModal = bottomSheetLayout.onClickOpenTokenPolicyModal,
                        )

                        is BottomSheetLayout.WalletPolicy -> WalletPolicyModal(
                            close = bottomSheetLayout.close,
                            onShowSnackbar = bottomSheetLayout.onShowSnackbar,
                            onShowTransactionSnackbar = bottomSheetLayout.onShowTransactionSnackbar
                        )

                        is BottomSheetLayout.TokenPolicy -> TokenPolicyModal(
                            walletItems = bottomSheetLayout.walletItems,
                            selectedWalletItem = bottomSheetLayout.selectedWalletItem,
                            close = bottomSheetLayout.close,
                            onShowSnackbar = bottomSheetLayout.onShowSnackbar,
                            onShowTransactionSnackbar = bottomSheetLayout.onShowTransactionSnackbar
                        )

                        else -> {
                            // Nothing to do.
                        }
                    }
                }
            }
        },
        content = content
    )
}

@Composable
private fun BottomModalHeader(title: String?, onClose: (() -> Unit)?) {
    Row(Modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .align(alignment = CenterVertically),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h3,
                color = WhiteSnow
            )
        }

        if (onClose != null) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF393A53), CircleShape)
            ) {
                Icon(
                    modifier = Modifier.padding(16.dp),
                    painter = painterResource(id = R.drawable.ic_close_24dp),
                    tint = Color(0xFFE1E2ED),
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewBottomModalHeaderDark() {
    ThemedPreview(darkTheme = true) {
        BottomModalHeader(title = "Title", onClose = {})
    }
}
