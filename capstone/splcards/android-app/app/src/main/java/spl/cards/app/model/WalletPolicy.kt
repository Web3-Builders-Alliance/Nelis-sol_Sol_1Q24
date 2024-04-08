package spl.cards.app.model

data class WalletPolicy(
    val address: String?,
    val allowList: List<String>,
    val spendingWindow: List<Long>
)
