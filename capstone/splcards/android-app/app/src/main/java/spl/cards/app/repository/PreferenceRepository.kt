package spl.cards.app.repository

class PreferenceRepository {

    private var cachedPublicKey: String? = null

    fun setCachedPublicKey(publicKey: String) {
        cachedPublicKey = publicKey
    }

    fun removeCachedPublicKey() {
        cachedPublicKey = null
    }

    fun getCachedPublicKey(): String? = cachedPublicKey
}
