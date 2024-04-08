package spl.cards.app.model.api.helius

data class HeliusResult(
    val total: Int,
    val limit: Int,
    val cursor: String,
    val items: List<HeliusItem>
)
