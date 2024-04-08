package spl.cards.app.model

import java.math.BigInteger

data class WalletItem(
    var mintAddress: String,
    val tokenProgram: String,
    val publicKey: String,
    var imageUrl: String,
    val name: String,
    val symbol: String,
    val uri: String,
    var totalValue: Double,
    val amountDecimal: Int,
    val totalAmount: BigInteger,
    val uiTotalAmount: Double
)
