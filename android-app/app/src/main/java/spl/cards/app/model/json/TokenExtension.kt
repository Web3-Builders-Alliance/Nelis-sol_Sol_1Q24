package spl.cards.app.model.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TokenExtension(@Json(name = "coingeckoId") val coingeckoId: String?)
