package spl.cards.app.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import spl.cards.app.util.Constants
import spl.cards.app.util.Result
import spl.cards.app.util.SeedSecurityHelper

class GetSeedUseCase(private val coroutineDispatcher: CoroutineDispatcher) {

    companion object {

        private const val TAG = "READ_SEED_USE_CASE"
    }

    suspend operator fun invoke(encryptedSeed: String, tagId: String, pinCode: String = ""): Result<String> = withContext(coroutineDispatcher) {
        return@withContext SeedSecurityHelper.decrypt(ciphertext = encryptedSeed, password = tagId + pinCode)?.let { seed: String ->
            Result.success(data = seed)
        } ?: kotlin.run {
            Result.failure(status = Constants.ResultStatus.WRONG_PINCODE, data = encryptedSeed)
        }
    }
}
