package spl.cards.app.di

import android.content.Context
import com.solana.networking.HttpNetworkingRouter
import com.solana.networking.Network
import com.solana.networking.RPCEndpoint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import spl.cards.app.BuildConfig
import spl.cards.app.util.Constants
import java.net.URL
import java.util.concurrent.TimeUnit

val apiModule: Module = module {

    single {
        HttpNetworkingRouter(RPCEndpoint.custom(URL(Constants.RPC_ENDPOINT), URL(Constants.RPC_ENDPOINT), Network.devnet))
    }

    single {
        OkHttpClient.Builder().apply {
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)

            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().setLevel(level = HttpLoggingInterceptor.Level.BODY))
            }
        }.build()
    }

    single {
        val context: Context = androidContext()
        context.getSharedPreferences("spl_card_settings", Context.MODE_PRIVATE)
    }
}
