package spl.cards.app.usecase

import com.solana.core.HotAccount
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.bitcoinj.core.Base58
import spl.cards.app.repository.SolanaRepository
import spl.cards.app.util.Result

class SendSOLTransactionUseCase(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val solanaRepository: SolanaRepository
) {

    suspend operator fun invoke(secretKey: String, destination: String, amount: Double, decimal: Int): Result<String> =
        withContext(coroutineDispatcher) {
            val lamports: Long = "1".padEnd(decimal + 1, '0').toLong()
            solanaRepository.sendSOLTransaction(
                account = HotAccount(Base58.decode(secretKey)),
                destination = destination,
                amount = (lamports * amount).toLong()
            )
        }
}
