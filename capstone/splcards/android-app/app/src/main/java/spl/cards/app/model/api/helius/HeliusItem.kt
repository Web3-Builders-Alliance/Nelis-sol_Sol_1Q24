package spl.cards.app.model.api.helius

data class HeliusItem(
    val id: String,
    val content: Content,
    val authorities: List<Authority>,
    val compression: Compression,
    val grouping: List<Any>,
    val royalty: Royalty,
    val creators: List<Any>,
    val ownership: Ownership,
    val supply: Any?,
    val mutable: Boolean,
    val burnt: Boolean,
    val token_info: TokenInfo
)
