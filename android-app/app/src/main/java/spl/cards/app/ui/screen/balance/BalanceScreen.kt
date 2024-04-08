package spl.cards.app.ui.screen.balance

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import spl.cards.app.MainActivity
import spl.cards.app.R
import spl.cards.app.extension.navigate
import spl.cards.app.model.WalletItem
import spl.cards.app.ui.component.*
import spl.cards.app.ui.component.modal.BottomModal
import spl.cards.app.ui.component.modal.BottomSheetLayout
import spl.cards.app.ui.theme.*
import spl.cards.app.util.BackHandler
import spl.cards.app.util.Constants
import spl.cards.app.util.ThemedPreview
import java.util.*

@ExperimentalFoundationApi
@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun BalanceScreen(navController: NavController, viewModel: BalanceViewModel = koinViewModel()) {
    val onWalletItems by viewModel.onWalletItems.observeAsState(initial = emptyList())
    val isRefreshing by viewModel.onRefresh.collectAsState(initial = false)

    val currentBalance: Double = onWalletItems.sumOf { it.totalValue }
    val scope = rememberCoroutineScope()
    val context: Context = LocalContext.current

    val searchTextFieldValue = remember { mutableStateOf(TextFieldValue()) }
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val currentBottomSheet: MutableState<BottomSheetLayout> =
        remember { mutableStateOf(BottomSheetLayout.NotSet) }
    val sendTokenModalTitle: String = stringResource(id = R.string.modal_send_token_title)
    val receiveTokenModalTitle: String = stringResource(id = R.string.modal_receive_token_subtitle)
    val wrapTokenModalTitle: String = stringResource(id = R.string.modal_wrap_token_title)
    val securityModalTitle: String = stringResource(id = R.string.button_wallet_security_options)
    val walletPolicyModalTitle: String = stringResource(id = R.string.modal_wallet_policy_title)
    val tokenPolicyModalTitle: String = stringResource(id = R.string.modal_token_policy_title)
    val currentBottomSheetTitle: MutableState<String> = remember {
        mutableStateOf(value = sendTokenModalTitle)
    }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val onShowSnackbar: (String) -> Unit = { message: String ->
        scope.launch {
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }

    val transactionSnackbarMessage: String =
        stringResource(id = R.string.modal_send_token_transaction_successfully)
    val transactionSnackbarActionLabel: String = stringResource(id = R.string.button_view)
    val onShowTransactionSnackbar: (String) -> Unit = { transactionId: String ->
        scope.launch {
            val snackbarResult: SnackbarResult =
                snackbarHostState.showSnackbar(
                    message = transactionSnackbarMessage,
                    actionLabel = transactionSnackbarActionLabel,
                    duration = SnackbarDuration.Long
                )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Constants.SOLSCAN_TRANSACTION_URL + transactionId)
                )
                context.startActivity(browserIntent)
            }
        }
    }

    OnLifecycleEvent(onEvent = { _, event: Lifecycle.Event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                viewModel.refreshData()
            }

            else -> {
                // Nothing to do.
            }
        }
    })

    val closeBottomSheet: () -> Unit = {
        scope.launch {
            modalBottomSheetState.hide()
        }
    }

    val showBottomSheet: () -> Unit = {
        scope.launch {
            modalBottomSheetState.show()
        }
    }

    BackHandler(onBack = {
        if (modalBottomSheetState.isVisible) {
            closeBottomSheet()
            return@BackHandler
        }
        (context as MainActivity).moveTaskToBack(true)
    })

    Box(modifier = Modifier.fillMaxSize()) {
        BottomModal(
            modalBottomSheetState = modalBottomSheetState,
            bottomSheetLayout = currentBottomSheet.value,
            scrimColor = BottomSheetScrimColorHalf,
            title = currentBottomSheetTitle.value,
            onClose = closeBottomSheet
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .padding(
                            vertical = 40.dp,
                            horizontal = dimensionResource(id = R.dimen.padding_default)
                        )
                ) {
                    IconButton(modifier = Modifier.size(24.dp),
                        onClick = {
                            //SentryHelper.sendUserInteractionBreadcrumb(component = "Back Button")
                            navController.navigate(
                                from = Constants.Screen.BalanceScreen,
                                to = Constants.Screen.GetStarted
                            )
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(
                                BlueButton
                            )
                        )
                    }
                    Column {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.label_current_balance),
                            style = MaterialTheme.typography.caption,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_default_half)))
                        AnimatedContent(
                            targetState = currentBalance,
                            transitionSpec = {
                                if (targetState > initialState) {
                                    (slideInVertically(
                                        initialOffsetY = { height -> height },
                                        animationSpec = tween(durationMillis = 300)
                                    ) + fadeIn(animationSpec = tween(durationMillis = 300)))
                                        .togetherWith(
                                            slideOutVertically(
                                                targetOffsetY = { height -> -height },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + fadeOut(animationSpec = tween(durationMillis = 300))
                                        )
                                } else {
                                    (slideInVertically(
                                        initialOffsetY = { height -> -height },
                                        animationSpec = tween(durationMillis = 300)
                                    ) + fadeIn(animationSpec = tween(durationMillis = 300)))
                                        .togetherWith(
                                            slideOutVertically(
                                                targetOffsetY = { height -> height },
                                                animationSpec = tween(durationMillis = 300)
                                            ) + fadeOut(animationSpec = tween(durationMillis = 300))
                                        )
                                }
                            },
                            label = "AnimatedContent"
                        ) { targetCount ->
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = convertAmountToCurrencyString(
                                    amount = targetCount,
                                    currency = Currency.getInstance("USD")
                                ),
                                style = MaterialTheme.typography.h1,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_default)))
                        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                RoundedIconButton(
                                    enabled = isRefreshing.not() && onWalletItems.isNotEmpty(),
                                    onClick = {
                                        //SentryHelper.sendUserInteractionBreadcrumb(component = "Send Button")
                                        currentBottomSheet.value = BottomSheetLayout.SendToken(
                                            walletItems = onWalletItems,
                                            close = {
                                                closeBottomSheet()
                                                viewModel.refreshData()
                                            },
                                            onShowSnackbar = onShowSnackbar,
                                            onShowTransactionSnackbar = onShowTransactionSnackbar
                                        )
                                        currentBottomSheetTitle.value = sendTokenModalTitle
                                        showBottomSheet()
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_arrow_up_right_24dp),
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default_half)))
                                Text(
                                    text = stringResource(id = R.string.button_send),
                                    style = MaterialTheme.typography.subtitle2,
                                    color = BlueButton
                                )
                            }
                            Spacer(modifier = Modifier.padding(end = 24.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                RoundedBorderIconButton(
                                    enabled = isRefreshing.not() && onWalletItems.isNotEmpty(),
                                    onClick = {
                                        currentBottomSheet.value = BottomSheetLayout.WrapToken(
                                            walletItems = onWalletItems,
                                            close = {
                                                closeBottomSheet()
                                                viewModel.refreshData()
                                            },
                                            onShowSnackbar = onShowSnackbar,
                                            onShowTransactionSnackbar = onShowTransactionSnackbar
                                        )
                                        currentBottomSheetTitle.value = wrapTokenModalTitle
                                        showBottomSheet()
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_stack_star_24dp),
                                        contentDescription = null,
                                        tint = BlueButton
                                    )
                                }
                                Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default_half)))
                                Text(
                                    text = stringResource(id = R.string.button_wrap),
                                    style = MaterialTheme.typography.subtitle2,
                                    color = BlueButton
                                )
                            }
                            Spacer(modifier = Modifier.padding(end = 24.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                RoundedBorderIconButton(
                                    enabled = isRefreshing.not() && onWalletItems.isNotEmpty(),
                                    onClick = {
                                        currentBottomSheet.value = BottomSheetLayout.SecurityModal(
                                            onClickOpenWalletPolicyModal = {
                                                closeBottomSheet()

                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    currentBottomSheet.value =
                                                        BottomSheetLayout.WalletPolicy(
                                                            close = {
                                                                closeBottomSheet()
                                                                viewModel.refreshData()
                                                            },
                                                            onShowSnackbar = onShowSnackbar,
                                                            onShowTransactionSnackbar = onShowTransactionSnackbar
                                                        )
                                                    currentBottomSheetTitle.value =
                                                        walletPolicyModalTitle
                                                    showBottomSheet()
                                                }, 500)
                                            },
                                            onClickOpenTokenPolicyModal = {
                                                closeBottomSheet()

                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    currentBottomSheet.value =
                                                        BottomSheetLayout.TokenPolicy(
                                                            walletItems = onWalletItems,
                                                            close = {
                                                                closeBottomSheet()
                                                                viewModel.refreshData()
                                                            },
                                                            onShowSnackbar = onShowSnackbar,
                                                            onShowTransactionSnackbar = onShowTransactionSnackbar
                                                        )
                                                    currentBottomSheetTitle.value =
                                                        tokenPolicyModalTitle
                                                    showBottomSheet()
                                                }, 500)
                                            }
                                        )
                                        currentBottomSheetTitle.value = securityModalTitle
                                        showBottomSheet()
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_security_24dp),
                                        contentDescription = null,
                                        tint = BlueButton
                                    )
                                }
                                Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default_half)))
                                Text(
                                    text = stringResource(id = R.string.button_security),
                                    style = MaterialTheme.typography.subtitle2,
                                    color = BlueButton
                                )
                            }
                            Spacer(modifier = Modifier.padding(end = 24.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                RoundedIconButton(
                                    enabled = isRefreshing.not() && onWalletItems.isNotEmpty(),
                                    onClick = {
                                        //SentryHelper.sendUserInteractionBreadcrumb(component = "Receive Button")
                                        currentBottomSheet.value =
                                            BottomSheetLayout.ReceiveToken(walletItems = onWalletItems)
                                        currentBottomSheetTitle.value = receiveTokenModalTitle
                                        showBottomSheet()
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_arrow_down_left_24dp),
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_default_half)))
                                Text(
                                    text = stringResource(id = R.string.button_receive),
                                    style = MaterialTheme.typography.subtitle2,
                                    color = BlueButton
                                )
                            }
                        }
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = BottomSheetShape,
                    elevation = ModalBottomSheetDefaults.Elevation,
                    color = BoxBackground
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.Transparent)
                            .padding(top = 40.dp)
                    ) {
                        SearchTextField(
                            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_default)),
                            value = searchTextFieldValue.value,
                            onValueChange = { searchTextFieldValue.value = it })
                        WalletList(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = dimensionResource(id = R.dimen.padding_default)),
                            walletItems = onWalletItems,
                            searchedText = searchTextFieldValue.value.text,
                            isRefreshing = isRefreshing,
                            onRefresh = {
                                viewModel.refreshData()
                            },
                            onClickWalletItem = { walletItem: WalletItem ->
                                //SentryHelper.sendUserInteractionBreadcrumb(component = "WalletItem")
                                currentBottomSheet.value = BottomSheetLayout.SendToken(
                                    walletItems = onWalletItems,
                                    selectedWalletItem = walletItem,
                                    close = {
                                        closeBottomSheet()
                                        viewModel.refreshData()
                                    },
                                    onShowSnackbar = onShowSnackbar,
                                    onShowTransactionSnackbar = onShowTransactionSnackbar
                                )
                                currentBottomSheetTitle.value = sendTokenModalTitle
                                showBottomSheet()
                            }
                        )
                    }
                }
            }
        }
        CustomSnackbar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(
                    vertical = 40.dp,
                    horizontal = dimensionResource(id = R.dimen.padding_default)
                ),
            snackbarHostState = snackbarHostState
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalCoilApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun PreviewCreateNewWalletScreenDark() {
    ThemedPreview(darkTheme = true) {
        BalanceScreen(navController = rememberNavController())
    }
}
