package spl.cards.app.di

import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import spl.cards.app.usecase.*
import spl.cards.app.usecase.preference.GetCachedPublicKeyUseCase
import spl.cards.app.usecase.preference.RemoveCachedPublicKeyUseCase
import spl.cards.app.usecase.preference.SetCachedPublicKeyUseCase

val useCaseModule: Module = module {

    factory {
        ReadNfcTagUseCase(coroutineDispatcher = Dispatchers.IO)
    }

    factory {
        GetPublicKeyUseCase(coroutineDispatcher = Dispatchers.IO)
    }

    factory {
        IsNfcTagRegisteredUseCase(
            coroutineDispatcher = Dispatchers.IO,
            authRepository = get()
        )
    }

    factory {
        GetSeedUseCase(coroutineDispatcher = Dispatchers.IO)
    }

    factory {
        WriteCardUseCase(
            coroutineDispatcher = Dispatchers.IO,
            context = androidContext()
        )
    }

    factory {
        GetWalletItemsUseCase(
            coroutineDispatcher = Dispatchers.IO,
            solanaRepository = get(),
            coingeckoRepository = get()
        )
    }

    factory {
        SendSOLTransactionUseCase(
            coroutineDispatcher = Dispatchers.IO,
            solanaRepository = get()
        )
    }

    factory {
        SendSPLTokenTransactionUseCase(
            coroutineDispatcher = Dispatchers.IO,
            solanaRepository = get()
        )
    }

    factory {
        CreateNewWrapperUseCase(
            coroutineDispatcher = Dispatchers.IO,
            solanaRepository = get()
        )
    }

    // Preference
    factory {
        SetCachedPublicKeyUseCase(preferenceRepository = get())
    }

    factory {
        RemoveCachedPublicKeyUseCase(preferenceRepository = get())
    }

    factory {
        GetCachedPublicKeyUseCase(preferenceRepository = get())
    }

    factory {
        WrapTokenUseCase(
            coroutineDispatcher = Dispatchers.IO,
            solanaRepository = get()
        )
    }

    factory {
        GetMintWrappedAddressUseCase(
            coroutineDispatcher = Dispatchers.IO,
            solanaRepository = get()
        )
    }

    factory {
        CreateNewWalletPolicyUseCase(
            coroutineDispatcher = Dispatchers.IO,
            solanaRepository = get()
        )
    }

    factory {
        UpdateWalletPolicyUseCase(
            coroutineDispatcher = Dispatchers.IO,
            solanaRepository = get()
        )
    }

    factory {
        GetWalletPolicyUseCase(
            solanaRepository = get()
        )
    }

    factory {
        GetTokenPolicyAddressUseCase(
            coroutineDispatcher = Dispatchers.IO,
            solanaRepository = get()
        )
    }

    factory {
        NewTokenPolicyUseCase(
            coroutineDispatcher = Dispatchers.IO,
            solanaRepository = get()
        )
    }

    factory {
        UpdateTokenPolicyUseCase(
            coroutineDispatcher = Dispatchers.IO,
            solanaRepository = get()
        )
    }
}
