package spl.cards.app.model.api.helius

data class Ownership(
    val frozen: Boolean,
    val delegated: Boolean,
    val delegate: String?, // Assuming it's a string, change if needed
    val ownership_model: String,
    val owner: String
)
