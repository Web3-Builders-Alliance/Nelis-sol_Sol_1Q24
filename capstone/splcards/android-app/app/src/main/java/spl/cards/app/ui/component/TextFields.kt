package spl.cards.app.ui.component

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.text.isDigitsOnly
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import spl.cards.app.R
import spl.cards.app.model.WalletItem
import spl.cards.app.ui.theme.Background
import spl.cards.app.ui.theme.BaeColor
import spl.cards.app.ui.theme.BlueButton
import spl.cards.app.ui.theme.BoxBackground
import spl.cards.app.util.ThemedPreview
import java.math.BigInteger
import java.util.Calendar
import java.util.TimeZone

@ExperimentalAnimationApi
@Composable
fun YourWalletAddressTextField(textFieldValue: TextFieldValue) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val copiedString: String = stringResource(id = R.string.label_copied_to_clipboard)

    Column {
        Text(
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.padding_default_half),
                bottom = dimensionResource(id = R.dimen.padding_default_half)
            ),
            text = stringResource(id = R.string.label_your_wallet_address),
            style = MaterialTheme.typography.subtitle1,
            color = BaeColor,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .defaultMinSize(
                    minWidth = TextFieldDefaults.MinWidth,
                    minHeight = TextFieldDefaults.MinHeight
                )
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(25))
                .background(Color(red = 118, green = 118, blue = 118, alpha = 0x1A))
                .clickable(onClick = {
                    clipboardManager.setText(AnnotatedString(text = textFieldValue.text))
                    Toast
                        .makeText(context, copiedString, Toast.LENGTH_SHORT)
                        .show()
                })
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.Center)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_default_half)),
                    maxLines = 1,
                    text = textFieldValue.text,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFFE1E2ED),
                    style = MaterialTheme.typography.subtitle2
                )
                Icon(
                    modifier = Modifier.wrapContentWidth(align = Alignment.End, unbounded = true),
                    painter = painterResource(id = R.drawable.ic_copy),
                    contentDescription = null
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewYourWalletAddressTextFieldDark() {
    ThemedPreview(darkTheme = true) {
        YourWalletAddressTextField(textFieldValue = TextFieldValue(text = "KDWksw4ycoT2ok9ldj3tDrQB4yT4QbC1LnHHC7TpwWfd"))
    }
}

@ExperimentalAnimationApi
@Composable
fun WalletAddressTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    CustomizedTextField(
        backgroundColor = Color(red = 118, green = 118, blue = 118, alpha = 0x1A),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        enabled = enabled,
        label = stringResource(id = R.string.label_wallet_address),
        placeholder = stringResource(id = R.string.label_wallet_address_placeholder),
        keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        })
    )
}

@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewWalletAddressTextFieldDark() {
    ThemedPreview(darkTheme = true) {
        WalletAddressTextField(value = TextFieldValue(text = ""), onValueChange = {})
    }
}

@ExperimentalAnimationApi
@Composable
fun AmountTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onClickMax: () -> Unit,
    enabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current

    CustomizedTextField(
        backgroundColor = Color(red = 118, green = 118, blue = 118, alpha = 0x1A),
        value = value,
        onValueChange = { textFieldValue: TextFieldValue ->
            if (textFieldValue.text.isEmpty() || textFieldValue.text.toDoubleOrNull() != null) {
                onValueChange.invoke(textFieldValue)
            }
        },
        singleLine = true,
        enabled = enabled,
        label = stringResource(id = R.string.label_amount),
        placeholder = stringResource(id = R.string.label_amount_placeholder),
        trailingIcon = {
            TextButton(
                modifier = Modifier.wrapContentWidth(align = Alignment.End),
                onClick = onClickMax,
            ) {
                Text(
                    text = stringResource(id = R.string.button_max),
                    style = MaterialTheme.typography.subtitle2,
                    color = BlueButton
                )
            }
        },
        keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.moveFocus(focusDirection = FocusDirection.Down)
        })
    )
}

@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewAmountTextFieldDark() {
    ThemedPreview(darkTheme = true) {
        AmountTextField(value = TextFieldValue(text = ""), onValueChange = {}, onClickMax = {})
    }
}

@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun TokenDropdown(
    onTokenSelected: (WalletItem) -> Unit,
    enabled: Boolean = true,
    walletItems: List<WalletItem>,
    selectedWalletItem: WalletItem,
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

    Column {
        CustomizedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                }
                .clickable { expanded = !expanded },
            backgroundColor = Color(red = 118, green = 118, blue = 118, alpha = 0x1A),
            value = TextFieldValue(selectedWalletItem.name),
            onValueChange = { },
            singleLine = true,
            enabled = enabled,
            label = stringResource(id = R.string.label_choose_token),
            leadingIcon = {
                Image(
                    modifier = Modifier.size(32.dp),
                    painter = rememberImagePainter(
                        data = selectedWalletItem.imageUrl,
                        builder = {
                            crossfade(true)
                        }
                    ), contentDescription = null)
            },
            trailingIcon = {
                Icon(imageVector = icon, contentDescription = null)
            })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(color = Background)
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            walletItems.forEach { walletItem ->
                DropdownMenuItem(
                    enabled = selectedWalletItem != walletItem,
                    onClick = {
                        expanded = false
                        onTokenSelected.invoke(walletItem)
                    }) {
                    Image(
                        modifier = Modifier
                            .alpha(alpha = if (selectedWalletItem == walletItem) 0.5f else 1f)
                            .size(32.dp),
                        painter = rememberImagePainter(
                            data = walletItem.imageUrl,
                            builder = {
                                crossfade(true)
                            }
                        ),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding_default_half)))
                    Text(text = walletItem.name)
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@ExperimentalCoilApi
@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewTokenDropdownDark() {
    ThemedPreview(darkTheme = true) {
        TokenDropdown(
            onTokenSelected = {},
            walletItems = listOf(
                WalletItem(
                    mintAddress = "mintAddress",
                    tokenProgram = "tokenProgram",
                    publicKey = "publicKey",
                    imageUrl = "imageUrl",
                    name = "name",
                    symbol = "symbol",
                    uri = "uri",
                    totalValue = 500.00,
                    amountDecimal = 9,
                    totalAmount = BigInteger.valueOf(25000000000),
                    uiTotalAmount = 25.0000
                )
            ),
            selectedWalletItem = WalletItem(
                mintAddress = "mintAddress",
                tokenProgram = "tokenProgram",
                publicKey = "publicKey",
                imageUrl = "imageUrl",
                name = "name",
                symbol = "symbol",
                uri = "uri",
                totalValue = 500.00,
                amountDecimal = 9,
                totalAmount = BigInteger.valueOf(25000000000),
                uiTotalAmount = 25.0000
            )
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun SearchTextField(
    modifier: Modifier? = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSearch: (() -> Unit)? = null,
    onMicrophoneClicked: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    CustomizedTextField(
        modifier = modifier,
        backgroundColor = Color(red = 118, green = 118, blue = 118, alpha = 0x1A),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = stringResource(id = R.string.label_search_placeholder),
        leadingIcon = {
            Icon(painter = painterResource(id = R.drawable.ic_search_24dp), contentDescription = null, tint = BaeColor)
        },
        trailingIcon = if (onMicrophoneClicked != null) {
            {
                Icon(
                    modifier = Modifier.clickable { onMicrophoneClicked.invoke() },
                    painter = painterResource(id = R.drawable.ic_microphone), contentDescription = "Microphone Icon", tint = BaeColor
                )
            }
        } else null,
        keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
            onSearch?.invoke()
        })
    )
}

@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewSearchTextFieldDark() {
    ThemedPreview(darkTheme = true) {
        SearchTextField(value = TextFieldValue(text = ""), onValueChange = {}, onMicrophoneClicked = {})
    }
}

@ExperimentalAnimationApi
@Composable
fun RecoveryPhraseTextField(textFieldValue: TextFieldValue) {
    val clipboardManager = LocalClipboardManager.current
    CustomizedTextField(
        modifier = Modifier.clickable(onClick = {
            clipboardManager.setText(AnnotatedString(text = textFieldValue.text))
        }),
        value = textFieldValue,
        onValueChange = { },
        readOnly = true,
        label = stringResource(id = R.string.label_recovery_phrase)
    )
}

@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewRecoveryPhraseTextFieldDark() {
    ThemedPreview(darkTheme = true) {
        RecoveryPhraseTextField(textFieldValue = TextFieldValue(text = "car table move blue dog start middle flowers couch run gradient card balcony flat book"))
    }
}

@ExperimentalAnimationApi
@Composable
fun PinCodeTextField(
    backgroundColor: Color = BoxBackground,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onDone: (() -> Unit)? = null,
    enabled: Boolean = true,
    label: String = stringResource(id = R.string.label_pincode)
) {
    val focusManager = LocalFocusManager.current
    CustomizedTextField(
        backgroundColor = backgroundColor,
        value = value,
        onValueChange = {
            if (it.text.isDigitsOnly() && it.text.length <= 6) {
                onValueChange.invoke(it)
            }
        },
        singleLine = true,
        isPassword = true,
        enabled = enabled,
        label = label,
        placeholder = stringResource(id = R.string.label_pincode_placeholder),
        keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            onDone?.invoke()
        })
    )
}

@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewPinCodeTextFieldDark() {
    ThemedPreview(darkTheme = true) {
        PinCodeTextField(value = TextFieldValue(text = ""), onValueChange = {})
    }
}

@ExperimentalAnimationApi
@Composable
fun WhitelistTextField(
    backgroundColor: Color = BoxBackground,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onDone: (() -> Unit)? = null,
    enabled: Boolean = true,
    label: String = stringResource(id = R.string.label_white_list)
) {
    val focusManager = LocalFocusManager.current
    CustomizedTextField(
        backgroundColor = backgroundColor,
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        enabled = enabled,
        label = label,
        placeholder = stringResource(id = R.string.label_wallet_address_placeholder),
        keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            onDone?.invoke()
        })
    )
}

@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewWhitelistTextFieldDark() {
    ThemedPreview(darkTheme = true) {
        WhitelistTextField(value = TextFieldValue(text = ""), onValueChange = {})
    }
}

@ExperimentalAnimationApi
@Composable
fun EmailTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onDone: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    CustomizedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        enabled = enabled,
        label = stringResource(id = R.string.label_email),
        placeholder = stringResource(id = R.string.label_email_placeholder),
        keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            onDone?.invoke()
        })
    )
}

@ExperimentalAnimationApi
@Preview
@Composable
fun PreviewEmailTextFieldDark() {
    ThemedPreview(darkTheme = true) {
        EmailTextField(value = TextFieldValue(text = ""), onValueChange = {})
    }
}

@ExperimentalAnimationApi
@Composable
private fun CustomizedTextField(
    modifier: Modifier? = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = BoxBackground,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    readOnly: Boolean = false,
    singleLine: Boolean = false,
    isPassword: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions()
) {
    Column {
        if (label != null) {
            Text(
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.padding_default_half),
                    bottom = dimensionResource(id = R.dimen.padding_default_half)
                ),
                text = label,
                style = MaterialTheme.typography.subtitle1,
                color = BaeColor,
                fontWeight = FontWeight.Bold
            )
        }
        Box {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(modifier ?: Modifier),
                enabled = enabled,
                value = value,
                onValueChange = onValueChange,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                placeholder = if (placeholder != null) {
                    { Text(text = placeholder, style = MaterialTheme.typography.subtitle2, color = BaeColor) }
                } else null,
                readOnly = readOnly,
                singleLine = singleLine,
                maxLines = maxLines,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                textStyle = MaterialTheme.typography.subtitle2,
                shape = RoundedCornerShape(25),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = backgroundColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    textColor = Color(0xFFE1E2ED)
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape = RoundedCornerShape(25))
                    .then(modifier ?: Modifier)
            )
        }
    }
}

@Composable
fun SpendingWindowPicker(preSelectedTimestamps: List<Long>, onTimestampsSelected: (List<Long>) -> Unit) {
    val context = LocalContext.current
    var startTime by remember {
        mutableStateOf(preSelectedTimestamps.getOrNull(1)?.let { timestampToCalendar(it) })
    }
    var endTime by remember {
        mutableStateOf(preSelectedTimestamps.getOrNull(0)?.let { timestampToCalendar(it) })
    }

    fun showTimePicker(isStartTime: Boolean) {
        val currentTime = Calendar.getInstance()
        TimePickerDialog(
            context, { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                if (isStartTime) {
                    startTime = selectedTime
                } else {
                    endTime = selectedTime
                }

                if (startTime != null && endTime != null) {
                    val startTimestamp = startTime!!.timeInMillis / 1000
                    val endTimestamp = endTime!!.timeInMillis / 1000
                    onTimestampsSelected(listOf(startTimestamp, endTimestamp))
                }
            },
            currentTime.get(Calendar.HOUR_OF_DAY),
            currentTime.get(Calendar.MINUTE),
            true // Set to 'false' for 12-hour mode if needed
        ).show()
    }

    Column {
        Text(text = "Select Spending Window")
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            GradientButton(text = "Select Start Time", onClick = { showTimePicker(isStartTime = true) })
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = startTime?.let { "${it.get(Calendar.HOUR_OF_DAY)}:${it.get(Calendar.MINUTE)}" } ?: "")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            GradientButton(text = "Select End Time", onClick = { showTimePicker(isStartTime = false) })
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = endTime?.let { "${it.get(Calendar.HOUR_OF_DAY)}:${it.get(Calendar.MINUTE)}" } ?: "")
        }
    }
}

fun timestampToCalendar(timestamp: Long): Calendar {
    return Calendar.getInstance().apply {
        timeInMillis = timestamp * 1000
        timeZone = TimeZone.getTimeZone("UTC")
    }
}
