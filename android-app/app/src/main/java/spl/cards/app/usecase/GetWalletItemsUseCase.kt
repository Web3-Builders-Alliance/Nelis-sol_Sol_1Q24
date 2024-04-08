package spl.cards.app.usecase

import android.util.Log
import com.metaplex.lib.programs.tokens.TokenProgram
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import spl.cards.app.model.WalletItem
import spl.cards.app.model.api.CoingeckoCoin
import spl.cards.app.model.api.helius.HeliusItem
import spl.cards.app.repository.CoingeckoRepository
import spl.cards.app.repository.SolanaRepository
import spl.cards.app.util.Result
import java.math.RoundingMode
import kotlin.math.pow

class GetWalletItemsUseCase(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val solanaRepository: SolanaRepository,
    private val coingeckoRepository: CoingeckoRepository
) {

    suspend operator fun invoke(publicKey: String): Result<List<WalletItem>> =
        withContext(coroutineDispatcher) {
            val walletItems: MutableList<WalletItem> = mutableListOf()

            // Read Solana Token Info
            solanaRepository.getSolanaTokenBalance(solanaPublicKey = publicKey).isSuccess()
                ?.let { balance: Long ->
                    walletItems.add(
                        WalletItem(
                            mintAddress = "",
                            tokenProgram = TokenProgram.publicKey.toBase58(),
                            publicKey = publicKey,
                            imageUrl = "",
                            name = "Solana",
                            symbol = "sol",
                            uri = "https://shdw-drive.genesysgo.net/6NtZ6mGHc7WKirqTSN692Vui9Cf1XHumGJjg5arNXD9k/sol.json",
                            totalValue = 0.0,
                            amountDecimal = 9,
                            totalAmount = balance.toBigInteger(),
                            uiTotalAmount = roundDecimal(number = (balance.toDouble() / 1000000000))
                        )
                    )
                }

            // Get Token Accounts
            solanaRepository.getTokenAccountsByOwner(solanaPublicKey = publicKey).isSuccess()
                ?.let { tokenValues: List<HeliusItem> ->
                    if (tokenValues.isEmpty().not()) {
                        tokenValues.forEach { token: HeliusItem ->
                            val symbol = token.content.metadata.symbol ?: "UnknownSymbol"
                            Log.d("HOHO", "token id: ${token.id}")
                            walletItems.add(
                                WalletItem(
                                    mintAddress = token.id,
                                    tokenProgram = token.token_info.token_program,
                                    publicKey = token.token_info.associated_token_address,
                                    imageUrl = if (token.content.files.isNotEmpty()) token.content.files[0].uri else "",
                                    name = token.content.metadata.name ?: "Unknown",
                                    symbol = symbol,
                                    uri = "https://shdw-drive.genesysgo.net/6NtZ6mGHc7WKirqTSN692Vui9Cf1XHumGJjg5arNXD9k/${symbol.lowercase()}.json",
                                    totalValue = token.token_info.price_info?.total_price ?: 0.0,
                                    amountDecimal = token.token_info.decimals,
                                    totalAmount = token.token_info.balance,
                                    uiTotalAmount = token.token_info.balance.toDouble() / 10.0.pow(token.token_info.decimals.toDouble())
                                )
                            )
                        }
                    }
                }

        // Set Token Prices and ImageUrls
        coingeckoRepository.getPrice(ids = listOf("solana", "usd-coin").joinToString(",")).isSuccess()
            ?.let { coingeckoCoins: List<CoingeckoCoin> ->
                coingeckoCoins.forEach { coingeckoCoin ->
                    when (coingeckoCoin.id) {
                        "solana" -> {
                            walletItems.find { it.name == "Solana" }?.apply {
                                imageUrl = coingeckoCoin.imageUrl
                                totalValue = roundDecimal(coingeckoCoin.price * uiTotalAmount)
                            }
                        }

                        "usd-coin" -> {
                            walletItems.find { it.name == "USD Coin" }?.apply {
                                imageUrl = coingeckoCoin.imageUrl
                            }
                        }
                    }
                }
            }

            return@withContext Result.success(data = walletItems.toList())
        }

    private fun roundDecimal(number: Double): Double =
        number.toBigDecimal().setScale(4, RoundingMode.HALF_UP).toDouble()
}
