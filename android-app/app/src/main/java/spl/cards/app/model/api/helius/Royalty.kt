package spl.cards.app.model.api.helius

data class Royalty(
    val royalty_model: String,
    val target: String?, // Assuming it's a string, change if needed
    val percent: Int,
    val basis_points: Int,
    val primary_sale_happened: Boolean,
    val locked: Boolean
)
