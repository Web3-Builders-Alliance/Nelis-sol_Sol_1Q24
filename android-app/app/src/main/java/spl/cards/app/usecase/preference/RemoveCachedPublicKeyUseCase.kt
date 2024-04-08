package spl.cards.app.usecase.preference

import spl.cards.app.repository.PreferenceRepository

class RemoveCachedPublicKeyUseCase(private val preferenceRepository: PreferenceRepository) {

    operator fun invoke() {
        preferenceRepository.removeCachedPublicKey()
    }
}
