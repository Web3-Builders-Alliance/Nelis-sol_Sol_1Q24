package spl.cards.app.usecase.preference

import spl.cards.app.repository.PreferenceRepository

class GetCachedPublicKeyUseCase(private val preferenceRepository: PreferenceRepository) {

    operator fun invoke(): String? = preferenceRepository.getCachedPublicKey()
}
