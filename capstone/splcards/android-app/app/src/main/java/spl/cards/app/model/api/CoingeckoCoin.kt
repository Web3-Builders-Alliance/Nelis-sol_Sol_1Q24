package spl.cards.app.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoingeckoCoin(
    val id: String,
    @Json(name = "image") val imageUrl: String,
    @Json(name = "current_price") val price: Float
)
