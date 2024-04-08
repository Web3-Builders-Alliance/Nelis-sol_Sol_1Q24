package spl.cards.app.ui.screen.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import spl.cards.app.model.WalletItem
import spl.cards.app.usecase.GetWalletItemsUseCase
import spl.cards.app.usecase.preference.GetCachedPublicKeyUseCase
import spl.cards.app.util.Constants
import spl.cards.app.util.SingleLiveEvent

class BalanceViewModel(
    private val getCachedPublicKeyUseCase: GetCachedPublicKeyUseCase,
    private val getWalletItemsUseCase: GetWalletItemsUseCase
) : ViewModel() {

    // Mutable
    private val _onWalletItems: MutableLiveData<List<WalletItem>> = MutableLiveData(emptyList())
    private val _onRefresh: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _onFailure: MutableLiveData<Constants.ResultStatus> = SingleLiveEvent()

    // Observable
    val onWalletItems: LiveData<List<WalletItem>> = _onWalletItems
    val onRefresh: StateFlow<Boolean> = _onRefresh
    val onFailure: LiveData<Constants.ResultStatus> = _onFailure

    fun refreshData() {
        getCachedPublicKeyUseCase()?.let { publicKey: String ->
            _onRefresh.value = true

            viewModelScope.launch {
                getWalletItemsUseCase(publicKey).handleResult(success = { walletItems: List<WalletItem> ->
                    _onFailure.value = Constants.ResultStatus.SUCCESS
                    _onWalletItems.value = walletItems
                }, failure = { resultStatus: Constants.ResultStatus ->
                    _onWalletItems.value = emptyList()
                    _onFailure.value = resultStatus
                })
                _onRefresh.value = false
            }
        }
    }
}
