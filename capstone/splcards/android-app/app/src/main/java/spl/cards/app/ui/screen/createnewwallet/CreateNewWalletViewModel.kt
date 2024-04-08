package spl.cards.app.ui.screen.createnewwallet

import android.nfc.Tag
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import spl.cards.app.extension.navigate
import spl.cards.app.ui.component.modal.layout.NFCModalState
import spl.cards.app.usecase.IsNfcTagRegisteredUseCase
import spl.cards.app.usecase.WriteCardUseCase
import spl.cards.app.util.Constants
import spl.cards.app.util.Result
import spl.cards.app.util.SingleLiveEvent

class CreateNewWalletViewModel(
    private val writeCardUseCase: WriteCardUseCase,
    private val isNfcTagRegisteredUseCase: IsNfcTagRegisteredUseCase
) : ViewModel() {

    // Mutable
    private val _onNfcState: MutableLiveData<NFCModalState> = MutableLiveData(NFCModalState.INITIAL)
    private val _onFailure: MutableLiveData<Constants.ResultStatus> = SingleLiveEvent()

    // Observable
    val onNfcState: LiveData<NFCModalState> = _onNfcState
    val onFailure: LiveData<Constants.ResultStatus> = _onFailure

    private var nfcEnabled: Boolean = false

    fun writeCard(navController: NavController, tag: Tag, secretKey: String, pinCode: String) {
        viewModelScope.launch {
            _onNfcState.value = NFCModalState.INITIAL

            val isNfcTagRegisteredResult: Result<Boolean> = isNfcTagRegisteredUseCase(tag)
            if (isNfcTagRegisteredResult is Result.Success) {
                if (isNfcTagRegisteredResult.data) { // Nfc tag is registered.
                    writeCardUseCase(tag = tag, secretKey = secretKey, pinCode = pinCode).handleResult(success = {
                        _onNfcState.value = NFCModalState.WRITE_SUCCESS
                        Handler(Looper.getMainLooper()).postDelayed({
                            navController.navigate(
                                from = Constants.Screen.CreateNewWallet(),
                                to = Constants.Screen.GetStarted
                            )
                        }, 3500)
                    }, failure = { resultStatus: Constants.ResultStatus ->
                        _onFailure.value = resultStatus
                        _onNfcState.value = NFCModalState.FAILURE
                        Handler(Looper.getMainLooper()).postDelayed({
                            _onNfcState.value = NFCModalState.INITIAL
                            nfcEnabled = true
                        }, 1000)
                    })
                } else { // Nfc tag is registered.
                    _onFailure.value = Constants.ResultStatus.NFC_TAG_NOT_REGISTERED
                    _onNfcState.value = NFCModalState.FAILURE
                    Handler(Looper.getMainLooper()).postDelayed({
                        _onNfcState.value = NFCModalState.INITIAL
                        nfcEnabled = true
                    }, 1000)
                }
            } else {
                _onFailure.value = isNfcTagRegisteredResult.status
                _onNfcState.value = NFCModalState.FAILURE
                Handler(Looper.getMainLooper()).postDelayed({
                    _onNfcState.value = NFCModalState.INITIAL
                    nfcEnabled = true
                }, 1000)
            }
        }
    }

    fun isNfcEnabled(): Boolean = nfcEnabled

    fun enableNfc() {
        nfcEnabled = true
        //SentryHelper.sendInfoBreadcrumb(category = "NFC", message = "NFC Tag detection enabled.")
    }

    fun disableNfc() {
        nfcEnabled = false
        //SentryHelper.sendInfoBreadcrumb(category = "NFC", message = "NFC Tag detection disabled.")
    }

    fun clearError() {
        _onFailure.value = Constants.ResultStatus.NOT_SET
    }
}
