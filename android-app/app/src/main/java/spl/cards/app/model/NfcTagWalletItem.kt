package spl.cards.app.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NfcTagWalletItem(
    @Json(name = "seed") val seed: String,
    @Json(name = "token") val token: String
)
