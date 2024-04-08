package spl.cards.app.model.api.helius

data class Compression(
    val eligible: Boolean,
    val compressed: Boolean,
    val data_hash: String,
    val creator_hash: String,
    val asset_hash: String,
    val tree: String,
    val seq: Int,
    val leaf_id: Int
)
