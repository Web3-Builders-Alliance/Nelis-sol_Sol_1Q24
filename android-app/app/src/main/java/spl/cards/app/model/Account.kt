package spl.cards.app.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Account(val privateKey: String, val publicKey: String)
