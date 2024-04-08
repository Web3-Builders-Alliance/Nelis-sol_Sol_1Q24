package spl.cards.app.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import spl.cards.app.repository.SolanaRepository

class GetTokenPolicyAddressUseCase(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val solanaRepository: SolanaRepository
) {

    suspend operator fun invoke(mintAddress: String): String? = withContext(coroutineDispatcher) {
       solanaRepository.getTokenPolicyAddress(mintAddress)
    }
}
