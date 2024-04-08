package spl.cards.app.repository

import android.content.SharedPreferences
import android.util.Log
import com.solana.Solana
import com.solana.actions.Action
import com.solana.actions.sendSOL
import com.solana.actions.serializeAndSendWithFee
import com.solana.api.AccountInfoSerializer
import com.solana.api.Api
import com.solana.core.Account
import com.solana.core.HotAccount
import com.solana.core.PublicKey
import com.solana.core.Sysvar
import com.solana.core.Transaction
import com.solana.models.buffer.AccountInfoData
import com.solana.networking.HttpNetworkingRouter
import com.solana.networking.serialization.serializers.base64.BorshAsBase64JsonArraySerializer
import com.solana.programs.AssociatedTokenProgram
import com.solana.programs.SystemProgram
import com.solana.programs.TokenProgram
import com.solana.rxsolana.api.getAccountInfo
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.rx2.await
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import spl.cards.app.model.api.helius.HeliusApiResponse
import spl.cards.app.model.api.helius.HeliusItem
import spl.cards.app.util.BigIntegerJsonAdapter
import spl.cards.app.util.Constants
import spl.cards.app.util.Result
import spl.cards.program.SplCardProgram
import spl.cards.program.SplCardProgramInstructions
import spl.cards.program.findSPLTokenDestinationAddress
import spl.cards.program.programExtraAccountMetaPda
import spl.cards.program.programPayerAtAPda
import spl.cards.program.programTokenPolicyPda
import spl.cards.program.programVaultPda
import spl.cards.program.programWalletPolicyPda
import spl.cards.program.programWrapperPda
import spl.cards.program.transfer
import spl.cards.program.transferChecked
import java.io.IOException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SolanaRepository(
    private val okHttpClient: OkHttpClient,
    private val networkingRouter: HttpNetworkingRouter,
    private val sharedPreferences: SharedPreferences,
) {

    private companion object {

        const val TAG = "SOLANA_REPOSITORY"
    }

    suspend fun getSolanaTokenBalance(solanaPublicKey: String): Result<Long> =
        suspendCoroutine { continuation: Continuation<Result<Long>> ->
            val jsonObject = JSONObject()
            jsonObject.put("jsonrpc", "2.0")
            jsonObject.put("id", (0..10).random())
            jsonObject.put("method", "getBalance")
            jsonObject.put("params", JSONArray(listOf(solanaPublicKey)))
            val body = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url = Constants.RPC_ENDPOINT)
                .addHeader(name = "Content-Type", value = "application/json")
                .post(body = body)
                .build()

            okHttpClient.newCall(request = request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, e.message, e)
                    continuation.resume(Result.failure(status = Constants.ResultStatus.API_ERROR))
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        var balance: Long = 0
                        response.body?.let { responseBody: ResponseBody ->
                            val responseJsonObject = JSONObject(responseBody.string())
                            if (responseJsonObject.has("result")) {
                                val resultJsonObject =
                                    responseJsonObject.get("result") as JSONObject

                                balance = if (resultJsonObject.get("value") is Int) {
                                    (resultJsonObject.get("value") as Int).toLong()
                                } else {
                                    (resultJsonObject.get("value") as Long)
                                }
                            }
                        }
                        continuation.resume(Result.success(data = balance))
                    } else {
                        continuation.resume(Result.failure(status = Constants.ResultStatus.API_ERROR))
                    }
                }
            })
        }

    suspend fun getTokenAccountsByOwner(solanaPublicKey: String): Result<List<HeliusItem>> =
        suspendCoroutine { continuation: Continuation<Result<List<HeliusItem>>> ->
            val jsonObject = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("id", (0..10).random().toString())
                put("method", "searchAssets")
                put("params", JSONObject().apply {
                    put("ownerAddress", solanaPublicKey)
                    put("tokenType", "fungible")
                })
            }
            val body = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(Constants.RPC_ENDPOINT)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, e.message, e)
                    continuation.resume(Result.failure(status = Constants.ResultStatus.API_ERROR))
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            val responseBody = it.body?.string()
                            responseBody?.let { jsonString ->
                                try {
                                    val moshi: Moshi = Moshi.Builder()
                                        .add(KotlinJsonAdapterFactory())
                                        .add(BigIntegerJsonAdapter())
                                        .build()
                                    val apiResponseAdapter: JsonAdapter<HeliusApiResponse> =
                                        moshi.adapter(HeliusApiResponse::class.java)
                                    val apiResponse = apiResponseAdapter.fromJson(jsonString)
                                    apiResponse?.result?.items?.let { items ->
                                        continuation.resume(Result.success(items))
                                    } ?: throw JSONException("No 'items' field")
                                } catch (e: Exception) {
                                    Log.e(TAG, "Parsing error", e)
                                    continuation.resume(Result.failure(status = Constants.ResultStatus.API_ERROR))
                                }
                            }
                                ?: continuation.resume(Result.failure(status = Constants.ResultStatus.API_ERROR))
                        } else {
                            continuation.resume(Result.failure(status = Constants.ResultStatus.API_ERROR))
                        }
                    }
                }
            })
        }

    suspend fun existTokenAccount(mint: PublicKey, owner: PublicKey): Boolean =
        suspendCoroutine { continuation: Continuation<Boolean> ->
            val jsonObject = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("id", (0..10).random().toString())
                put("method", "getTokenAccounts")
                put("params", JSONObject().apply {
                    put("mint", mint.toBase58())
                    put("owner", owner.toBase58())
                })
                put("options", JSONObject().apply {
                    put("showZeroBalance", true)
                })
            }
            val body = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(Constants.RPC_ENDPOINT)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, e.message, e)
                    continuation.resume(false)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { resp ->
                        if (resp.isSuccessful) {
                            val responseBody = resp.body?.string()
                            responseBody?.let { jsonString ->
                                try {
                                    val jsonObject = JSONObject(jsonString)
                                    val resultObject = jsonObject.optJSONObject("result")
                                    val tokenAccounts = resultObject?.optJSONArray("token_accounts")
                                    continuation.resume((tokenAccounts?.length() ?: 0) > 0)
                                } catch (e: JSONException) {
                                    Log.e(TAG, "JSON parsing error: ${e.message}", e)
                                    continuation.resume(false)
                                }
                            } ?: continuation.resume(false)
                        } else {
                            continuation.resume(false)
                        }
                    }
                }
            })
        }


    suspend fun sendSOLTransaction(
        account: Account,
        destination: String,
        amount: Long
    ): Result<String> =
        suspendCoroutine { continuation: Continuation<Result<String>> ->
            if (destination == account.publicKey.toBase58()) {
                continuation.resume(value = Result.failure(status = Constants.ResultStatus.SAME_ADDRESSES))
                return@suspendCoroutine
            }
            Action(api = Api(router = networkingRouter), supportedTokens = listOf()).sendSOL(
                account = account,
                destination = PublicKey(pubkeyString = destination),
                amount = amount,
                onComplete = { transactionResult: kotlin.Result<String> ->
                    transactionResult.onSuccess { transactionId: String ->
                        continuation.resume(value = Result.success(data = transactionId))
                    }
                    transactionResult.onFailure { exception: Throwable ->
                        Log.e(TAG, exception.message, exception)
                        continuation.resume(value = Result.failure(status = Constants.ResultStatus.API_ERROR))
                    }
                })
        }

    suspend fun sendSPLTokenTransaction(
        account: Account,
        mintAddress: PublicKey,
        tokenProgram: PublicKey,
        destination: PublicKey,
        amount: Long,
        decimals: Int,
    ): Result<String> {
        // Derive token accounts
        val senderTokenAccount: PublicKey = SplCardProgram.findSPLTokenDestinationAddress(
            account.publicKey,
            mintAddress,
            tokenProgram
        ).address
        val receiverTokenAccount: PublicKey = SplCardProgram.findSPLTokenDestinationAddress(
            destination,
            mintAddress,
            tokenProgram
        ).address

        val tokenAccountExist: Boolean = existTokenAccount(mintAddress, destination)

        return suspendCoroutine { continuation: Continuation<Result<String>> ->
            // Define transaction
            val transaction = Transaction()

            if (!tokenAccountExist) {
                val createATokenInstruction =
                    AssociatedTokenProgram.createAssociatedTokenAccountInstruction(
                        programId = tokenProgram,
                        mint = mintAddress,
                        associatedAccount = receiverTokenAccount,
                        owner = destination,
                        payer = account.publicKey
                    )
                transaction.add(createATokenInstruction)
            }

            transaction.add(
                transferChecked(
                    senderTokenAccount,
                    receiverTokenAccount,
                    amount,
                    decimals.toByte(),
                    account.publicKey,
                    mintAddress,
                    tokenProgram
                )
            )
            transaction.feePayer = account.publicKey

            Action(
                api = Api(router = networkingRouter),
                supportedTokens = listOf()
            ).serializeAndSendWithFee(
                transaction = transaction,
                signers = listOf(account),
                onComplete = { transactionResult: kotlin.Result<String> ->
                    transactionResult.onSuccess { transactionId: String ->
                        continuation.resume(value = Result.success(data = transactionId))
                    }
                    transactionResult.onFailure { exception: Throwable ->
                        Log.e(TAG, exception.message, exception)
                        continuation.resume(value = Result.failure(status = Constants.ResultStatus.API_ERROR))
                    }
                }
            )
        }
    }

    suspend fun newWrapper(
        account: HotAccount,
        mintAddress: PublicKey,
        tokenProgram: PublicKey,
        name: String,
        symbol: String,
        uri: String
    ): Result<String> {
        return suspendCoroutine { continuation: Continuation<Result<String>> ->
            val wrapper: PublicKey =
                SplCardProgram.programWrapperPda(mintAddress).address
            val mintWrapped = HotAccount()

            // Define transaction
            val transaction = Transaction().add(
                SplCardProgramInstructions.newWrapper(
                    payer = account.publicKey,
                    mintWrapped = mintWrapped.publicKey,
                    mintOriginal = mintAddress,
                    wrapper = wrapper,
                    vault = SplCardProgram.programVaultPda(mintAddress).address,
                    extraAccountMetaList = SplCardProgram.programExtraAccountMetaPda(
                        mintWrapped.publicKey
                    ).address,
                    rent = Sysvar.SYSVAR_RENT_PUBKEY,
                    associatedTokenProgram = AssociatedTokenProgram.SPL_ASSOCIATED_TOKEN_ACCOUNT_PROGRAM_ID,
                    tokenProgram = tokenProgram,
                    tokenExtensionsProgram = PublicKey(Constants.TOKEN_22), // Token22
                    systemProgram = SystemProgram.PROGRAM_ID,
                    name = "spl${name}",
                    symbol = "spl${symbol.uppercase()}",
                    uri = uri
                )
            )
            transaction.feePayer = account.publicKey

            Action(
                api = Api(router = networkingRouter),
                supportedTokens = listOf()
            ).serializeAndSendWithFee(
                transaction = transaction,
                signers = listOf(account, mintWrapped),
                onComplete = { transactionResult: kotlin.Result<String> ->
                    transactionResult.onSuccess { transactionId: String ->
                        // Store generated mintWrapped into sharedPreferences
                        sharedPreferences.edit().let {
                            it.putString(mintAddress.toBase58(), mintWrapped.publicKey.toBase58())
                            it.apply()
                        }
                        continuation.resume(value = Result.success(data = transactionId))
                    }
                    transactionResult.onFailure { exception: Throwable ->
                        Log.e(TAG, exception.message, exception)
                        continuation.resume(value = Result.failure(status = Constants.ResultStatus.API_ERROR))
                    }
                }
            )
        }
    }

    suspend fun closeWrapper(
        account: HotAccount,
        mintAddress: PublicKey
    ): Result<String> {
        return suspendCoroutine { continuation: Continuation<Result<String>> ->
            val wrapper: PublicKey =
                SplCardProgram.programWrapperPda(mintAddress).address
            val mintWrapped = HotAccount()

            // Define transaction
            val transaction = Transaction().add(
                SplCardProgramInstructions.wrapperClose(
                    payer = account.publicKey,
                    mintOriginal = mintAddress,
                    wrapper = wrapper,
                    associatedTokenProgram = AssociatedTokenProgram.SPL_ASSOCIATED_TOKEN_ACCOUNT_PROGRAM_ID,
                    tokenProgram = PublicKey(Constants.TOKEN_22), // Token22
                    systemProgram = SystemProgram.PROGRAM_ID,
                )
            )
            transaction.feePayer = account.publicKey

            Action(
                api = Api(router = networkingRouter),
                supportedTokens = listOf()
            ).serializeAndSendWithFee(transaction = transaction,
                signers = listOf(account, mintWrapped),
                onComplete = { transactionResult: kotlin.Result<String> ->
                    transactionResult.onSuccess { transactionId: String ->
                        continuation.resume(value = Result.success(data = transactionId))
                    }
                    transactionResult.onFailure { exception: Throwable ->
                        Log.e(TAG, exception.message, exception)
                        continuation.resume(value = Result.failure(status = Constants.ResultStatus.API_ERROR))
                    }
                }
            )
        }
    }

    suspend fun wrapToken(
        account: HotAccount,
        mintWrappedAddress: PublicKey,
        mintAddress: PublicKey,
        tokenProgram: PublicKey,
        amount: Long
    ): Result<String> {
        return suspendCoroutine { continuation: Continuation<Result<String>> ->
            val wrapper: PublicKey = SplCardProgram.programWrapperPda(mintAddress).address
            val payerAtaOriginal = SplCardProgram.programPayerAtAPda(
                account.publicKey,
                mintAddress,
                tokenProgram
            ).address
            val payerAtaWrapped: PublicKey = SplCardProgram.programPayerAtAPda(
                account.publicKey,
                mintWrappedAddress,
                PublicKey(Constants.TOKEN_22)
            ).address

            // Define transaction
            val transaction = Transaction().add(
                SplCardProgramInstructions.wrap(
                    payer = account.publicKey,
                    payerAtaOriginal = payerAtaOriginal,
                    payerAtaWrapped = payerAtaWrapped,
                    mintOriginal = mintAddress,
                    mintWrapped = mintWrappedAddress,
                    wrapper = wrapper,
                    vault = SplCardProgram.programVaultPda(mintAddress).address,
                    associatedTokenProgram = AssociatedTokenProgram.SPL_ASSOCIATED_TOKEN_ACCOUNT_PROGRAM_ID,
                    tokenProgram = tokenProgram,
                    tokenExtensionsProgram = PublicKey(Constants.TOKEN_22), // Token22
                    systemProgram = SystemProgram.PROGRAM_ID,
                    amount = amount.toULong()
                )
            )
            transaction.feePayer = account.publicKey

            Action(
                api = Api(router = networkingRouter),
                supportedTokens = listOf()
            ).serializeAndSendWithFee(transaction = transaction,
                signers = listOf(account),
                onComplete = { transactionResult: kotlin.Result<String> ->
                    transactionResult.onSuccess { transactionId: String ->
                        continuation.resume(value = Result.success(data = transactionId))
                    }
                    transactionResult.onFailure { exception: Throwable ->
                        Log.e(TAG, exception.message, exception)
                        continuation.resume(value = Result.failure(status = Constants.ResultStatus.API_ERROR))
                    }
                }
            )
        }
    }

    suspend fun newWalletPolicy(account: HotAccount): Result<String> {
        return suspendCoroutine { continuation: Continuation<Result<String>> ->

            val walletPolicyPDA = SplCardProgram.programWalletPolicyPda(account.publicKey).address

            // Define transaction
            val transaction = Transaction().add(
                SplCardProgramInstructions.newWalletPolicy(
                    payer = account.publicKey,
                    walletPolicy = walletPolicyPDA,
                    systemProgram = SystemProgram.PROGRAM_ID,
                )
            )
            transaction.feePayer = account.publicKey

            Action(
                api = Api(router = networkingRouter),
                supportedTokens = listOf()
            ).serializeAndSendWithFee(transaction = transaction,
                signers = listOf(account),
                onComplete = { transactionResult: kotlin.Result<String> ->
                    transactionResult.onSuccess { transactionId: String ->
                        // Store generated policyWalletAddress into sharedPreferences
                        sharedPreferences.edit().let {
                            it.putString("walletPolicyAddress", walletPolicyPDA.toBase58())
                            it.apply()
                        }
                        continuation.resume(value = Result.success(data = transactionId))
                    }
                    transactionResult.onFailure { exception: Throwable ->
                        Log.e(TAG, exception.message, exception)
                        continuation.resume(value = Result.failure(status = Constants.ResultStatus.API_ERROR))
                    }
                }
            )
        }
    }

    suspend fun updateWalletPolicy(
        account: HotAccount,
        walletPolicyAddress: PublicKey,
        allowList: List<PublicKey>,
        spendingWindowList: List<Long>
    ): Result<String> {
        return suspendCoroutine { continuation: Continuation<Result<String>> ->

            // Define transaction
            val transaction = Transaction().add(
                SplCardProgramInstructions.addAllowedPublickeysToWalletPolicy(
                    payer = account.publicKey,
                    walletPolicy = walletPolicyAddress,
                    systemProgram = SystemProgram.PROGRAM_ID,
                    allowedPubkeyList = allowList
                )
            )
            if (spendingWindowList.isNotEmpty()) {
                transaction.add(
                    SplCardProgramInstructions.addSpendingWindowToWalletPolicy(
                        payer = account.publicKey,
                        walletPolicy = walletPolicyAddress,
                        spendingWindow = spendingWindowList,
                        systemProgram = SystemProgram.PROGRAM_ID
                    )
                )
            }
            transaction.feePayer = account.publicKey

            Action(
                api = Api(router = networkingRouter),
                supportedTokens = listOf()
            ).serializeAndSendWithFee(transaction = transaction,
                signers = listOf(account),
                onComplete = { transactionResult: kotlin.Result<String> ->
                    transactionResult.onSuccess { transactionId: String ->
                        // Store new allowList and spendingWindow into sharedPreferences
                        val spendingAllowListStringSet = allowList.map { it.toString() }.toSet()
                        val spendingWindowStringSet =
                            spendingWindowList.map { it.toString() }.toSet()
                        sharedPreferences.edit().let {
                            it.putStringSet("walletPolicyAllowList", spendingAllowListStringSet)
                            it.putStringSet("walletPolicySpendingWindow", spendingWindowStringSet)
                            it.apply()
                        }
                        continuation.resume(value = Result.success(data = transactionId))
                    }
                    transactionResult.onFailure { exception: Throwable ->
                        Log.e(TAG, exception.message, exception)
                        continuation.resume(value = Result.failure(status = Constants.ResultStatus.API_ERROR))
                    }
                }
            )
        }
    }

    suspend fun newTokenPolicy(
        account: HotAccount,
        mintAddress: PublicKey,
        amount: Long
    ): Result<String> {
        return suspendCoroutine { continuation: Continuation<Result<String>> ->

            val tokenPolicyPDA =
                SplCardProgram.programTokenPolicyPda(account.publicKey, mintAddress).address

            // Define transaction
            val transaction = Transaction().add(
                SplCardProgramInstructions.newFullTokenPolicy(
                    payer = account.publicKey,
                    mintWrapped = mintAddress,
                    tokenPolicy = tokenPolicyPDA,
                    systemProgram = SystemProgram.PROGRAM_ID,
                    spendLimitAmount = amount.toULong()
                )
            )
            transaction.feePayer = account.publicKey

            Action(
                api = Api(router = networkingRouter),
                supportedTokens = listOf()
            ).serializeAndSendWithFee(transaction = transaction,
                signers = listOf(account),
                onComplete = { transactionResult: kotlin.Result<String> ->
                    transactionResult.onSuccess { transactionId: String ->
                        // Store generated tokenPolicyPda into sharedPreferences
                        sharedPreferences.edit().let {
                            it.putString("token_policy_$mintAddress", tokenPolicyPDA.toBase58())
                            it.apply()
                        }
                        continuation.resume(value = Result.success(data = transactionId))
                    }
                    transactionResult.onFailure { exception: Throwable ->
                        Log.e(TAG, exception.message, exception)
                        continuation.resume(value = Result.failure(status = Constants.ResultStatus.API_ERROR))
                    }
                }
            )
        }
    }

    suspend fun updateTokenPolicy(
        account: HotAccount,
        mintAddress: PublicKey,
        amount: Long
    ): Result<String> {
        return suspendCoroutine { continuation: Continuation<Result<String>> ->

            val mintWrappedAddress: String = getMintWrappedAddress(mintAddress.toBase58()) ?: ""
            val tokenPolicyPDA = SplCardProgram.programTokenPolicyPda(
                account.publicKey,
                PublicKey(mintWrappedAddress)
            ).address

            // Define transaction
            val transaction = Transaction().add(
                SplCardProgramInstructions.addSpendLimitToTokenPolicy(
                    payer = account.publicKey,
                    mintWrapped = PublicKey(mintWrappedAddress),
                    tokenPolicy = tokenPolicyPDA,
                    systemProgram = SystemProgram.PROGRAM_ID,
                    amount = amount.toULong()
                )
            )
            transaction.feePayer = account.publicKey

            Action(
                api = Api(router = networkingRouter),
                supportedTokens = listOf()
            ).serializeAndSendWithFee(transaction = transaction,
                signers = listOf(account),
                onComplete = { transactionResult: kotlin.Result<String> ->
                    transactionResult.onSuccess { transactionId: String ->
                        // Store new allowList and spendingWindow into sharedPreferences
                        sharedPreferences.edit().let {
                            it.putLong("token_policy_amount_$mintAddress", amount)
                            it.apply()
                        }
                        continuation.resume(value = Result.success(data = transactionId))
                    }
                    transactionResult.onFailure { exception: Throwable ->
                        Log.e(TAG, exception.message, exception)
                        continuation.resume(value = Result.failure(status = Constants.ResultStatus.API_ERROR))
                    }
                }
            )
        }
    }

    fun getTokenPolicyAmount(mintAddress: String): Long {
        Log.d(TAG, "getTokenPolicyAddress - called!")
        Log.d(TAG, "mintAddress: $mintAddress")
        return sharedPreferences.getLong("token_policy_amount_$mintAddress", 0)
    }

    fun getTokenPolicyAddress(mintAddress: String): String? {
        Log.d(TAG, "getTokenPolicyAddress - called!")
        Log.d(TAG, "mintAddress: $mintAddress")
        return sharedPreferences.getString("token_policy_$mintAddress", null)
    }

    fun getMintWrappedAddress(mintAddress: String): String? {
        Log.d(TAG, "getMintWrappedAddress - called!")
        Log.d(TAG, "mintAddress: $mintAddress")
        return sharedPreferences.getString(mintAddress, null)
    }

    fun getMintByWrappedAddress(mintWrappedAddress: String): String? {
        Log.d(TAG, "getMintByWrappedAddress - called!")
        Log.d(TAG, "mintWrappedAddress: $mintWrappedAddress")
        sharedPreferences.all.forEach { (key, value) ->
            if (value == mintWrappedAddress) {
                return key
            }
        }
        return null
    }

    fun getWalletPolicyAddress(): String? {
        Log.d(TAG, "getWalletPolicyAddress - called!")
        return sharedPreferences.getString("walletPolicyAddress", null)
    }

    fun getWalletPolicyAllowList(): List<String> {
        Log.d(TAG, "getWalletPolicyAllowList - called!")
        return sharedPreferences.getStringSet("walletPolicyAllowList", setOf())?.toList()
            ?: listOf()
    }

    fun getWalletPolicySpendingWindow(): List<Long> {
        Log.d(TAG, "getWalletPolicySpendingWindow - called!")
        return sharedPreferences.getStringSet("walletPolicySpendingWindow", setOf())
            ?.mapNotNull { it.toLongOrNull() }
            ?: listOf()
    }
}
