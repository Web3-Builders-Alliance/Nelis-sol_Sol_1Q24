package spl.cards.app.usecase

import com.solana.core.HotAccount
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.bitcoinj.core.Base58
import spl.cards.app.util.*

class GetPublicKeyUseCase(private val coroutineDispatcher: CoroutineDispatcher) {

    companion object {

        private const val TAG = "GET_PUBLIC_KEY_USE_CASE"
    }

    suspend operator fun invoke(encryptedSeed: String, tagId: String, pinCode: String = ""): Result<String> = withContext(coroutineDispatcher) {
        return@withContext SeedSecurityHelper.decrypt(ciphertext = encryptedSeed, password = tagId + pinCode)?.let { seed: String ->
            Result.success(data = HotAccount(Base58.decode(seed)).publicKey.toBase58())
        } ?: kotlin.run {
            Result.failure(status = Constants.ResultStatus.WRONG_PINCODE, data = encryptedSeed)
        }
    }
}
