package spl.cards.app.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import spl.cards.app.R
import spl.cards.app.model.WalletItem
import spl.cards.app.ui.theme.BaeColor
import spl.cards.app.ui.theme.BlueButton
import spl.cards.app.ui.theme.WhiteSnow
import spl.cards.app.util.ThemedPreview
import java.math.BigInteger
import java.text.NumberFormat
import java.util.*

@ExperimentalCoilApi
@Composable
fun WalletList(
    modifier: Modifier,
    walletItems: List<WalletItem>,
    searchedText: String,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onClickWalletItem: (walletItem: WalletItem) -> Unit
) {
    var filteredWalletItems: List<WalletItem>
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                scale = true,
                backgroundColor = BlueButton
            )
        }
    ) {
        LazyColumn(modifier = modifier) {
            filteredWalletItems = if (searchedText.isEmpty()) {
                walletItems
            } else {
                walletItems.filter { it.name.contains(searchedText, ignoreCase = true) }
            }
            items(filteredWalletItems) { walletItem: WalletItem ->
                WalletItem(walletItem = walletItem, onClick = onClickWalletItem)
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun WalletItem(walletItem: WalletItem, onClick: (walletItem: WalletItem) -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = { onClick(walletItem) })
            .padding(horizontal = dimensionResource(id = R.dimen.padding_default))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(id = R.dimen.padding_default),
                    bottom = dimensionResource(id = R.dimen.padding_default_half)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(48.dp),
                painter = rememberImagePainter(
                    data = walletItem.imageUrl,
                    builder = {
                        crossfade(true)
                    }
                ),
                contentDescription = null
            )
            Spacer(modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding_default)))
            Text(text = walletItem.name, style = MaterialTheme.typography.h3, color = WhiteSnow)
            Column(
                modifier = Modifier
                    .wrapContentWidth(align = Alignment.End)
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = convertAmountToCurrencyString(amount = walletItem.totalValue, currency = Currency.getInstance("USD")),
                    style = MaterialTheme.typography.h4,
                    color = WhiteSnow
                )
                Text(text = walletItem.uiTotalAmount.toString(), style = MaterialTheme.typography.subtitle1, color = BaeColor)
            }
        }
        Divider(color = Color(0xFF2C2F4B), startIndent = 68.dp)
    }
}

@ExperimentalCoilApi
@Preview(showBackground = true)
@Composable
fun PreviewWalletListDark() {
    ThemedPreview(darkTheme = true) {
        WalletList(
            modifier = Modifier.fillMaxSize(),
            walletItems = walletItemsMock(),
            searchedText = "",
            isRefreshing = true,
            onRefresh = {},
            onClickWalletItem = {})
    }
}

fun convertAmountToCurrencyString(amount: Double, currency: Currency): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US) // Or any locale that suits your format
    format.currency = Currency.getInstance(currency.currencyCode)
    return format.format(amount)
}

fun walletItemsMock(): List<WalletItem> = listOf(
    WalletItem(
        mintAddress = "mintAddress1",
        tokenProgram = "tokenProgram1",
        publicKey = "publicKey1",
        imageUrl = "https://assets.coingecko.com/coins/images/4128/small/coinmarketcap-solana-200.png",
        name = "SOL",
        symbol = "symbol",
        uri = "uri",
        totalValue = 500.00,
        amountDecimal = 9,
        totalAmount = BigInteger.valueOf(25000000000),
        uiTotalAmount = 25.0000
    ),
    WalletItem(
        mintAddress = "mintAddress2",
        tokenProgram = "tokenProgram2",
        publicKey = "publicKey2",
        imageUrl = "https://assets.coingecko.com/coins/images/11970/small/serum-logo.png",
        name = "SRM",
        symbol = "symbol",
        uri = "uri",
        totalValue = 1049.40,
        amountDecimal = 6,
        totalAmount = BigInteger.valueOf(180000000),
        uiTotalAmount = 180.0000
    ),
    WalletItem(
        mintAddress = "mintAddress3",
        tokenProgram = "tokenProgram3",
        publicKey = "publicKey3",
        imageUrl = "https://assets.coingecko.com/coins/images/13556/small/Copy_of_image_%28139%29.png",
        name = "MAPS",
        symbol = "symbol",
        uri = "uri",
        totalValue = 291.60,
        amountDecimal = 6,
        totalAmount = BigInteger.valueOf(450000000),
        uiTotalAmount = 450.0000
    ),
    WalletItem(
        mintAddress = "mintAddress4",
        tokenProgram = "tokenProgram4",
        publicKey = "publicKey4",
        imageUrl = "https://assets.coingecko.com/coins/images/9026/small/F.png",
        name = "FTT",
        symbol = "symbol",
        uri = "uri",
        totalValue = 845.00,
        amountDecimal = 6,
        totalAmount = BigInteger.valueOf(20000000),
        uiTotalAmount = 20.0000
    ),
)