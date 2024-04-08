package spl.cards.app.util

object Constants {

    const val HOME_PAGE_URL = "https://splcards.com"
    const val SOLSCAN_TRANSACTION_URL = "https://solscan.io/tx/"
    const val RPC_ENDPOINT = "https://devnet.helius-rpc.com/?api-key=2d6c544c-8fc7-4bac-9352-a60a7bb2a391"
    const val TOKEN_22 = "TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb"

    sealed class Screen(val route: String) {
        object GetStarted : Screen(route = "get_started_screen")
        object SetupRecoveryPhrase : Screen(route = "setup_recovery_phrase")
        data class CreateNewWallet(val secretKey: String = "secretKey") :
            Screen(route = "create_new_wallet?secretKey={secretKey}")
        object BalanceScreen : Screen(route = "balance_screen")
    }

    enum class ResultStatus {
        NOT_SET,
        SUCCESS,
        NOT_FOUND,
        API_ERROR,
        READ_NFC_TAG_ERROR,
        NFC_TAG_NOT_REGISTERED,
        SAME_ADDRESSES,
        NO_WALLET_FOUND,
        WRONG_PINCODE,
    }
}
