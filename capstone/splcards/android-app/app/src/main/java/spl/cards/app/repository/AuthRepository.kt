package spl.cards.app.repository

import android.util.Log
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import spl.cards.app.util.Constants
import spl.cards.app.util.Result
import java.io.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepository(private val okHttpClient: OkHttpClient) {

    companion object {

        private const val TAG = "AUTH_REPOSITORY_TAG"
        private const val NFC_TAG_REGISTERED_IDS_ENDPOINT =
            "https://t3a2tivfgohchy4xx7pflxjoiuirq3wnw64o2mvzxl5542vrbxsq.arweave.net/nsGpoqUzjiPjl7_eVd0uRREYbs23uO0yubr73mqxDeU"
    }

    suspend fun isNfcTagRegistered(nfcTagId: String): Result<Boolean> = suspendCoroutine { continuation: Continuation<Result<Boolean>> ->
        continuation.resume(Result.success(data = true))

        /*val request = Request.Builder()
            .url(url = NFC_TAG_REGISTERED_IDS_ENDPOINT)
            .addHeader(name = "accept", value = "application/json")
            .get()
            .build()

        okHttpClient.newCall(request = request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, e.message, e)
                continuation.resume(Result.failure(status = Constants.ResultStatus.API_ERROR))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    var isNfcTagRegistered = false
                    response.body?.let { responseBody: ResponseBody ->
                        val registeredIdsJsonArray = JSONObject(responseBody.string()).get("registeredIds") as JSONArray
                        for (i in 0 until registeredIdsJsonArray.length()) {
                            if ((registeredIdsJsonArray[i] as String).toLowerCase(locale = Locale.current) == nfcTagId.toLowerCase(locale = Locale.current)) {
                                isNfcTagRegistered = true
                            }
                        }
                    }
                    continuation.resume(Result.success(data = isNfcTagRegistered))
                } else {
                    continuation.resume(Result.failure(status = Constants.ResultStatus.API_ERROR))
                }
            }
        })*/
    }
}
