package spl.cards.app.usecase

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.nfc.*
import android.nfc.tech.Ndef
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import spl.cards.app.extension.toHexString
import spl.cards.app.model.NfcTagWalletItem
import spl.cards.app.util.*
import java.io.IOException
import java.lang.reflect.ParameterizedType

class WriteCardUseCase(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val context: Context
) {

    companion object {

        private const val TAG = "WRITE_CARD_USE_CASE"
        private const val MIME_TYPE = "application/json"
    }

    suspend operator fun invoke(tag: Tag, secretKey: String, pinCode: String = ""): Result<Unit> = withContext(coroutineDispatcher) {
        val ndef: Ndef = Ndef.get(tag) ?: kotlin.run {
            return@withContext Result.failure(status = Constants.ResultStatus.NOT_FOUND)
        }

        val signatureKey: String = tag.id.toHexString() + pinCode
        val encryptedSecretKey: String = SeedSecurityHelper.encrypt(secretKey, signatureKey) ?: kotlin.run {
            return@withContext Result.failure(status = Constants.ResultStatus.NOT_FOUND)
        }

        val type: ParameterizedType = Types.newParameterizedType(MutableList::class.java, NfcTagWalletItem::class.java)
        val adapter: JsonAdapter<List<NfcTagWalletItem>> = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
            .adapter(type)
        val mimeNdefJsonMessage: String = adapter.toJson(listOf(NfcTagWalletItem(seed = encryptedSecretKey, token = "solana")))

        val uriNdefRecord: NdefRecord = NdefRecord.createUri("https://splcards.com/spl.cards.app/getStarted")
        val mimeNdefRecord: NdefRecord = NdefRecord.createMime(MIME_TYPE, mimeNdefJsonMessage.toByteArray())
        val ndefMessage = NdefMessage(uriNdefRecord, mimeNdefRecord)

        @Suppress("BlockingMethodInNonBlockingContext")
        return@withContext try {
            ndef.connect()
            ndef.writeNdefMessage(ndefMessage)

            // Play a Sound
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone: Ringtone = RingtoneManager.getRingtone(context, notification)
            ringtone.play()

            Result.success(data = Unit)

        } catch (exception: Throwable) {
            when (exception) {
                is FormatException -> {
                    //SentryHelper.sendErrorBreadcrumb(message = "NDEF Message write is malformed.")
                }
                is TagLostException -> {
                    //SentryHelper.sendErrorBreadcrumb(message = "Tag went out of range before operations were complete.")
                }
                is IOException -> {
                    //SentryHelper.sendErrorBreadcrumb(message = "I/O failure, or the operation was cancelled.")
                }
                else -> {
                    //Sentry.captureException(exception)
                }
            }
            Result.failure(status = Constants.ResultStatus.NOT_FOUND)
        } finally {
            try {
                ndef.close()
            } catch (exception: IOException) {
                //Sentry.captureException(exception)
            }
        }
    }
}
