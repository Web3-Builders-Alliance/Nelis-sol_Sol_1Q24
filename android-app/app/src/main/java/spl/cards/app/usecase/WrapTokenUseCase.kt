package spl.cards.app.usecase

import com.solana.core.HotAccount
import com.solana.core.PublicKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.bitcoinj.core.Base58
import spl.cards.app.repository.SolanaRepository
import spl.cards.app.util.Result

class WrapTokenUseCase(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val solanaRepository: SolanaRepository
) {

    suspend operator fun invoke(
        secretKey: String,
        mintAddress: String,
        mintWrappedAddress: String,
        tokenProgramAddress: String,
        amount: Double,
        decimal: Int
    ): Result<String> =
        withContext(coroutineDispatcher) {
            val lamports: Long = "1".padEnd(decimal + 1, '0').toLong()
            solanaRepository.wrapToken(
                account = HotAccount(Base58.decode(secretKey)),
                mintWrappedAddress = PublicKey(mintWrappedAddress),
                mintAddress = PublicKey(mintAddress),
                tokenProgram = PublicKey(tokenProgramAddress),
                amount = (lamports * amount).toLong()
            )
        }
}
