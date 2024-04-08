package spl.cards.app.model.api.helius

data class HeliusApiResponse(
    val jsonrpc: String,
    val result: HeliusResult,
    val id: String
)
