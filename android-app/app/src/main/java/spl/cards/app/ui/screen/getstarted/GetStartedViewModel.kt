package spl.cards.app.ui.screen.getstarted

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
import spl.cards.app.extension.toHexString
import spl.cards.app.model.NfcTagWalletItem
import spl.cards.app.ui.component.modal.layout.NFCModalState
import spl.cards.app.usecase.GetPublicKeyUseCase
import spl.cards.app.usecase.ReadNfcTagUseCase
import spl.cards.app.usecase.preference.SetCachedPublicKeyUseCase
import spl.cards.app.util.Constants
import spl.cards.app.util.Result
import spl.cards.app.util.SingleLiveEvent

class GetStartedViewModel(
    private val readNfcTagUseCase: ReadNfcTagUseCase,
    private val getPublicKeyUseCase: GetPublicKeyUseCase,
    private val setCachedPublicKeyUseCase: SetCachedPublicKeyUseCase
) : ViewModel() {

    // Mutable
    private val _onNfcState: MutableLiveData<NFCModalState> = MutableLiveData(NFCModalState.INITIAL)
    private val _onFailure: MutableLiveData<Constants.ResultStatus> = SingleLiveEvent()

    // Observable
    val onNfcState: LiveData<NFCModalState> = _onNfcState
    val onFailure: LiveData<Constants.ResultStatus> = _onFailure

    private var nfcEnabled: Boolean = false
    private var scannedNfcTagId: String? = null
    private var scannedNfcTagWalletItem: NfcTagWalletItem? = null

    fun readCard(navController: NavController, tag: Tag) {
        viewModelScope.launch {
            disableNfc()

            _onFailure.value = Constants.ResultStatus.NOT_SET
            _onNfcState.value = NFCModalState.INITIAL

            scannedNfcTagId = tag.id.toHexString()

            val readNfcTagResult: Result<NfcTagWalletItem> = readNfcTagUseCase(tag)
            if (readNfcTagResult is Result.Success) {
                val publicKeyResult: Result<String> = getPublicKeyUseCase(encryptedSeed = readNfcTagResult.data.seed, tagId = scannedNfcTagId!!)
                if (publicKeyResult is Result.Success) {
                    setCachedPublicKeyUseCase(publicKey = publicKeyResult.data)
                    _onNfcState.value = NFCModalState.READ_SUCCESS
                    Handler(Looper.getMainLooper()).postDelayed({
                        navController.navigate(from = Constants.Screen.GetStarted, to = Constants.Screen.BalanceScreen)
                    }, 1000)
                } else if (publicKeyResult is Result.Failure) {
                    scannedNfcTagWalletItem = readNfcTagResult.data

                    _onNfcState.value = NFCModalState.READ_SUCCESS
                    Handler(Looper.getMainLooper()).postDelayed({
                        _onNfcState.value = NFCModalState.PIN_CODE
                    }, 1000)
                }
            } else if (readNfcTagResult is Result.Failure) {
                enableNfc()

                _onNfcState.value = NFCModalState.FAILURE
                Handler(Looper.getMainLooper()).postDelayed({
                    _onNfcState.value = NFCModalState.INITIAL
                    nfcEnabled = true
                }, 1000)
            }
        }
    }

    fun decryptSeed(navController: NavController, pinCode: String) {
        viewModelScope.launch {
            val publicKeyResult: Result<String> =
                getPublicKeyUseCase(encryptedSeed = scannedNfcTagWalletItem!!.seed, tagId = scannedNfcTagId!!, pinCode = pinCode)
            if (publicKeyResult is Result.Success) {
                setCachedPublicKeyUseCase(publicKey = publicKeyResult.data)
                scannedNfcTagId = null
                scannedNfcTagWalletItem = null
                navController.navigate(from = Constants.Screen.GetStarted, to = Constants.Screen.BalanceScreen)
            } else if (publicKeyResult is Result.Failure) {
                _onFailure.value = publicKeyResult.status
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
        _onNfcState.value = NFCModalState.INITIAL
        //SentryHelper.sendInfoBreadcrumb(category = "NFC", message = "NFC Tag detection disabled.")
    }

    fun clearError() {
        _onFailure.value = Constants.ResultStatus.NOT_SET
    }
}
