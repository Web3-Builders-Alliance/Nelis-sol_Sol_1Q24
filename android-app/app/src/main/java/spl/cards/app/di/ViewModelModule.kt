package spl.cards.app.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import spl.cards.app.ui.component.modal.layout.SendTokenViewModel
import spl.cards.app.ui.component.modal.layout.TokenPolicyViewModel
import spl.cards.app.ui.component.modal.layout.WalletPolicyViewModel
import spl.cards.app.ui.component.modal.layout.WrapTokenViewModel
import spl.cards.app.ui.screen.balance.BalanceViewModel
import spl.cards.app.ui.screen.createnewwallet.CreateNewWalletViewModel
import spl.cards.app.ui.screen.getstarted.GetStartedViewModel
import spl.cards.app.ui.screen.setuprecoveryphrase.SetupRecoveryPhraseViewModel

val viewModelModule: Module = module {

    viewModel {
        GetStartedViewModel(
            getPublicKeyUseCase = get(),
            readNfcTagUseCase = get(),
            setCachedPublicKeyUseCase = get()
        )
    }

    viewModel {
        SetupRecoveryPhraseViewModel()
    }

    viewModel {
        CreateNewWalletViewModel(writeCardUseCase = get(), isNfcTagRegisteredUseCase = get())
    }

    viewModel {
        BalanceViewModel(
            getCachedPublicKeyUseCase = get(),
            getWalletItemsUseCase = get()
        )
    }

    viewModel {
        SendTokenViewModel(
            sendSOLTransactionUseCase = get(),
            sendSPLTokenTransactionUseCase = get(),
            readNfcTagUseCase = get(),
            getSeedUseCase = get()
        )
    }

    viewModel {
        WrapTokenViewModel(
            wrapTokenUseCase = get(),
            mintWrappedAddressUseCase = get(),
            newWrapperUseCase = get(),
            readNfcTagUseCase = get(),
            getSeedUseCase = get()
        )
    }

    viewModel {
        WalletPolicyViewModel(
            getWalletPolicyUseCase = get(),
            newWalletPolicyUseCase = get(),
            updateWalletPolicyUseCase = get(),
            readNfcTagUseCase = get(),
            getSeedUseCase = get()
        )
    }

    viewModel {
        TokenPolicyViewModel(
            getTokenPolicyAddressUseCase = get(),
            newTokenPolicyUseCase = get(),
            updateTokenPolicyUseCase = get(),
            readNfcTagUseCase = get(),
            getSeedUseCase = get()
        )
    }
}
