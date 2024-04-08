package spl.cards.app.usecase

import android.util.Log
import com.solana.core.HotAccount
import com.solana.core.PublicKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.bitcoinj.core.Base58
import spl.cards.app.repository.SolanaRepository
import spl.cards.app.util.Result

class UpdateWalletPolicyUseCase(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val solanaRepository: SolanaRepository
) {

    suspend operator fun invoke(
        secretKey: String,
        walletPolicyAddress: String,
        allowList: List<String>,
        spendingWindow: List<Long>
    ): Result<String> =
        withContext(coroutineDispatcher) {
            Log.d("JAJA", "allowList: $allowList")
            Log.d("JAJA", "spendingWindow: $spendingWindow")
            solanaRepository.updateWalletPolicy(
                account = HotAccount(Base58.decode(secretKey)),
                walletPolicyAddress = PublicKey(walletPolicyAddress),
                allowList = allowList.map {
                    Log.d("JAJA", "allow pk: $it")
                    PublicKey(it)
                },
                spendingWindowList = spendingWindow
            )
        }
}
