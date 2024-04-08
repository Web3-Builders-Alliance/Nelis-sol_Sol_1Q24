package spl.cards.app.usecase

import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import spl.cards.app.model.NfcTagWalletItem
import spl.cards.app.util.Constants
import spl.cards.app.util.Result
import java.lang.reflect.ParameterizedType

class ReadNfcTagUseCase(private val coroutineDispatcher: CoroutineDispatcher) {

    companion object {

        private const val TAG = "READ_NFC_TAG_USE_CASE"
    }

    suspend operator fun invoke(tag: Tag): Result<NfcTagWalletItem> = withContext(coroutineDispatcher) {
        val ndef: Ndef = Ndef.get(tag) ?: kotlin.run {
            return@withContext Result.failure(status = Constants.ResultStatus.NOT_FOUND)
        }
        val ndefMessage: NdefMessage = ndef.cachedNdefMessage ?: kotlin.run {
            return@withContext Result.failure(status = Constants.ResultStatus.NOT_FOUND)
        }
        if (ndefMessage.records.isEmpty() || ndefMessage.records.size < 2) {
            Log.d(TAG, "NFC Tag empty or wrong configured.")
            //SentryHelper.sendErrorBreadcrumb(message = "NFC Tag empty or wrong configured.")
            return@withContext Result.failure(status = Constants.ResultStatus.NOT_FOUND)
        }

        val payload: String = ndefMessage.records[1].payload.decodeToString().trim()
        val nfcTagWalletItems: List<NfcTagWalletItem> = getNfcTagWalletItems(payload = payload)

        if (nfcTagWalletItems.isEmpty()) {
            Log.d(TAG, "No wallet on NFC Tag configures.")
            //SentryHelper.sendErrorBreadcrumb(message = "No wallet on NFC Tag configures.")
            return@withContext Result.failure(status = Constants.ResultStatus.NO_WALLET_FOUND)
        }

        return@withContext Result.success(data = nfcTagWalletItems.first())
    }

    private fun getNfcTagWalletItems(payload: String): List<NfcTagWalletItem> {
        val type: ParameterizedType = Types.newParameterizedType(MutableList::class.java, NfcTagWalletItem::class.java)
        val adapter: JsonAdapter<List<NfcTagWalletItem>> = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
            .adapter(type)
        return adapter.fromJson(payload) ?: emptyList()
    }
}
