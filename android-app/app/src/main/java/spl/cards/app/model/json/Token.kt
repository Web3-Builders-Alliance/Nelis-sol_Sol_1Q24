package spl.cards.app.model.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Token(
    @Json(name = "chainId") val chainId: Int,
    @Json(name = "address") val address: String,
    @Json(name = "symbol") val symbol: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "decimals") val decimals: Int?,
    @Json(name = "extensions") val extensions: TokenExtension?
)
