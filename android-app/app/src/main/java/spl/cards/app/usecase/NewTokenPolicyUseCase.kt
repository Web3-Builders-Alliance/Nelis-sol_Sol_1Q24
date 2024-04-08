package spl.cards.app.usecase

import com.solana.core.HotAccount
import com.solana.core.PublicKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.bitcoinj.core.Base58
import spl.cards.app.repository.SolanaRepository
import spl.cards.app.util.Result

class NewTokenPolicyUseCase(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val solanaRepository: SolanaRepository
) {

    suspend operator fun invoke(secretKey: String, mint: String, amount: Double, decimal: Int): Result<String> =
        withContext(coroutineDispatcher) {
            val lamports: Long = "1".padEnd(decimal + 1, '0').toLong()
            solanaRepository.newTokenPolicy(
                account = HotAccount(Base58.decode(secretKey)),
                mintAddress = PublicKey(mint),
                amount = (lamports * amount).toLong()
            )
        }
}
