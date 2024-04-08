package spl.cards.app.usecase

import com.solana.core.HotAccount
import com.solana.core.PublicKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.bitcoinj.core.Base58
import spl.cards.app.repository.SolanaRepository
import spl.cards.app.util.Result

class CreateNewWrapperUseCase(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val solanaRepository: SolanaRepository
) {

    suspend operator fun invoke(secretKey: String, mintAddress: String, tokenProgram: String, name: String, symbol: String, uri: String): Result<String> =
        withContext(coroutineDispatcher) {
            solanaRepository.newWrapper(
                account = HotAccount(Base58.decode(secretKey)),
                mintAddress = PublicKey(mintAddress),
                tokenProgram = PublicKey(tokenProgram),
                name = name,
                symbol = symbol,
                uri = uri,
            )
        }
}
