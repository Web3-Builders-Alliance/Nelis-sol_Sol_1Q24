package spl.cards.app.util

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class SeedSecurityHelper {

    companion object {

        /** Minimum values recommended by PKCS#5. https://en.wikipedia.org/wiki/PKCS  */
        private const val ITERATION_COUNT = 1000

        /** 256-bits for AES-256, 128-bits for AES-128, etc  */
        private const val KEY_LENGTH = 128

        /** Encryption Algorithm PBKDF2. https://en.wikipedia.org/wiki/PBKDF2  */
        private const val PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1"

        /** The name of the transformation to create a cipher.  */
        private const val CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"

        /** Salt length depend from key length (128 / 8 = 16).  */
        private const val PKCS5_SALT_LENGTH = 16

        private const val DELIMITER = "]"

        private val random = SecureRandom()

        /**
         * @param plaintext Text to be encrypted.
         * @param password The password for the encryption key.
         */
        fun encrypt(plaintext: String, password: String): String? = try {
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val iv = generateIv(cipher.blockSize)
            val ivParams = IvParameterSpec(iv)
            val salt = generateSalt()
            cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey(password, salt), ivParams)
            val cipherText = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))
            String.format("%s%s%s%s%s", toBase64(salt), DELIMITER, toBase64(iv), DELIMITER, toBase64(cipherText))
            /*if (salt != null) {
                String.format("%s%s%s%s%s", toBase64(salt), DELIMITER, toBase64(iv), DELIMITER, toBase64(cipherText))
            } else String.format("%s%s%s", toBase64(iv), DELIMITER, toBase64(cipherText))
             */
        } catch (exception: GeneralSecurityException) {
            //Sentry.captureException(exception)
            null
        }

        /**
         * @param ciphertext Encrypted text, which will be subject to decoding.
         * @param password The password for the encryption key.
         */
        fun decrypt(ciphertext: String, password: String): String? {
            val fields = ciphertext.split(DELIMITER).toTypedArray()
            require(value = fields.size == 3) {
                //SentryHelper.sendErrorBreadcrumb(message = "Invalid encypted text format")
                return null
            }
            val salt = fromBase64(fields[0])
            val iv = fromBase64(fields[1])
            val cipherBytes = fromBase64(fields[2])
            val key = generateSecretKey(password, salt)
            return try {
                val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
                val ivParams = IvParameterSpec(iv)
                cipher.init(Cipher.DECRYPT_MODE, key, ivParams)
                val plaintext = cipher.doFinal(cipherBytes)
                String(plaintext, StandardCharsets.UTF_8)
            } catch (exception: GeneralSecurityException) {
                if (exception is BadPaddingException) {
                    // Wrong secretKey to decrypt the seed.
                } else {
                    //Sentry.captureException(exception)
                }
                null
            }
        }

        /** Salt is random data that is used as an additional input to a one-way function that "hashes" a password or passphrase. */
        private fun generateSalt(): ByteArray {
            val b = ByteArray(PKCS5_SALT_LENGTH)
            random.nextBytes(b)
            return b
        }

        /** Initialization vector - to achieve semantic security, does not allow an attacker
         * to infer relationships between segments of the encrypted message.  */
        private fun generateIv(length: Int): ByteArray {
            val b = ByteArray(length)
            random.nextBytes(b)
            return b
        }

        /** Use this to derive the key from the password
         * @param password The password for the encryption key.
         * @param salt The generated salt.
         */
        private fun generateSecretKey(password: String, salt: ByteArray): SecretKey? = try {
            val keySpec: KeySpec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
            val keyFactory = SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM)
            val keyBytes = keyFactory.generateSecret(keySpec).encoded
            SecretKeySpec(keyBytes, "AES")
        } catch (exception: GeneralSecurityException) {
            //Sentry.captureException(exception)
            null
        }

        /** Represent binary data in an ASCII string format */
        private fun toBase64(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)

        private fun fromBase64(base64: String): ByteArray = Base64.decode(base64, Base64.NO_WRAP)
    }
}
