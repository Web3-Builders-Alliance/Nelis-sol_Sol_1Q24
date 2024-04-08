package spl.cards.app.usecase

import android.nfc.Tag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import spl.cards.app.extension.toHexString
import spl.cards.app.repository.AuthRepository
import spl.cards.app.util.Result

class IsNfcTagRegisteredUseCase(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(tag: Tag): Result<Boolean> = withContext(coroutineDispatcher) {
        authRepository.isNfcTagRegistered(nfcTagId = tag.id.toHexString())
    }
}
