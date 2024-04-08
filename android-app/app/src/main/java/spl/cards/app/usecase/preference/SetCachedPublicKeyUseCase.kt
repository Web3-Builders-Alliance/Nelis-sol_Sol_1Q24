package spl.cards.app.usecase.preference

import spl.cards.app.repository.PreferenceRepository

class SetCachedPublicKeyUseCase(private val preferenceRepository: PreferenceRepository) {

    operator fun invoke(publicKey: String) {
        preferenceRepository.setCachedPublicKey(publicKey = publicKey)
    }
}
