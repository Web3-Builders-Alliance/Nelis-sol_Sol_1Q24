package spl.cards.app.di

import org.koin.core.module.Module
import org.koin.dsl.module
import spl.cards.app.repository.AuthRepository
import spl.cards.app.repository.CoingeckoRepository
import spl.cards.app.repository.PreferenceRepository
import spl.cards.app.repository.SolanaRepository

val repositoryModule: Module = module {

    single { AuthRepository(okHttpClient = get()) }

    single {
        SolanaRepository(
            okHttpClient = get(),
            networkingRouter = get(),
            sharedPreferences = get(),
        )
    }

    single { CoingeckoRepository(okHttpClient = get()) }

    single { PreferenceRepository() }
}
