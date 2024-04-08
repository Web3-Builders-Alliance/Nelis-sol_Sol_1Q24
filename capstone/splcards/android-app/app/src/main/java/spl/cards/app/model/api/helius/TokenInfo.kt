package spl.cards.app.model.api.helius

import java.math.BigInteger

data class TokenInfo(
    val symbol: String?,
    val balance: BigInteger,
    val supply: BigInteger,
    val decimals: Int,
    val token_program: String,
    val associated_token_address: String,
    val price_info: PriceInfo?,
    val mint_authority: String?,
    val freeze_authority: String?
)
