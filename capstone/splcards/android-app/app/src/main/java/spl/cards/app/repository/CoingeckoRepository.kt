package spl.cards.app.repository

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import spl.cards.app.model.api.CoingeckoCoin
import spl.cards.app.util.Constants
import spl.cards.app.util.Result
import java.io.IOException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CoingeckoRepository(private val okHttpClient: OkHttpClient) {

    companion object {

        private const val TAG = "COINGECKO_REPOSITORY"
        private const val COINGECKO_API_ENDPOINT = "https://api.coingecko.com/api/v3"
    }

    suspend fun getPrice(ids: String, currency: String = "usd"): Result<List<CoingeckoCoin>> = suspendCoroutine { continuation: Continuation<Result<List<CoingeckoCoin>>> ->
        val request = Request.Builder()
                .url(url = "$COINGECKO_API_ENDPOINT/coins/markets?vs_currency=$currency&ids=$ids")
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
                    val coingeckoCoins: MutableList<CoingeckoCoin> = mutableListOf()
                    response.body?.let { responseBody: ResponseBody ->
                        val moshi: Moshi = Moshi.Builder()
                                .add(KotlinJsonAdapterFactory())
                                .build()
                        val type = Types.newParameterizedType(List::class.java, CoingeckoCoin::class.java)
                        val adapter: JsonAdapter<List<CoingeckoCoin>> = moshi.adapter(type)
                        coingeckoCoins.addAll(adapter.fromJson(responseBody.string()) ?: listOf())
                    }
                    continuation.resume(Result.success(data = coingeckoCoins.toList()))
                } else {
                    continuation.resume(Result.failure(status = Constants.ResultStatus.API_ERROR))
                }
            }
        })
    }
}
