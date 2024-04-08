package spl.cards.app.ui.component.modal.layout

import android.nfc.Tag
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import spl.cards.app.extension.toHexString
import spl.cards.app.model.NfcTagWalletItem
import spl.cards.app.model.WalletPolicy
import spl.cards.app.usecase.CreateNewWalletPolicyUseCase
import spl.cards.app.usecase.GetSeedUseCase
import spl.cards.app.usecase.GetWalletPolicyUseCase
import spl.cards.app.usecase.ReadNfcTagUseCase
import spl.cards.app.usecase.UpdateWalletPolicyUseCase
import spl.cards.app.util.Constants
import spl.cards.app.util.Result
import spl.cards.app.util.SingleLiveEvent

class WalletPolicyViewModel(
    private val getWalletPolicyUseCase: GetWalletPolicyUseCase,
    private val newWalletPolicyUseCase: CreateNewWalletPolicyUseCase,
    private val updateWalletPolicyUseCase: UpdateWalletPolicyUseCase,
    private val readNfcTagUseCase: ReadNfcTagUseCase,
    private val getSeedUseCase: GetSeedUseCase
) : ViewModel() {

    // Mutable
    private val _onWalletPolicy: MutableLiveData<WalletPolicy> = MutableLiveData(getWalletPolicyUseCase())
    private val _onNFCEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _onSendButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _onSendTransactionLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _onCloseModal: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _onShowConfetti: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _onShowPinCode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _onTransactionId: MutableLiveData<String?> = MutableLiveData(null)
    private val _onFailure: MutableLiveData<Constants.ResultStatus> = SingleLiveEvent()

    // Observable
    val onWalletPolicy: LiveData<WalletPolicy> = _onWalletPolicy
    val onNFCEnabled: LiveData<Boolean> = _onNFCEnabled
    val onSendButtonEnabled: LiveData<Boolean> = _onSendButtonEnabled
    val onSendTransactionLoading: LiveData<Boolean> = _onSendTransactionLoading
    val onCloseModal: StateFlow<Boolean> = _onCloseModal
    val onShowConfetti: StateFlow<Boolean> = _onShowConfetti
    val onShowPinCode: StateFlow<Boolean> = _onShowPinCode
    val onTransactionId: LiveData<String?> = _onTransactionId
    val onFailure: LiveData<Constants.ResultStatus> = _onFailure

    // Private member variables
    private var scannedNfcTagId: String? = null
    private var scannedNfcTagWalletItem: NfcTagWalletItem? = null

    fun readNfcTag(tag: Tag) {
        viewModelScope.launch {
            disableNfc()

            scannedNfcTagId = tag.id.toHexString()

            val readNfcTagResult: Result<NfcTagWalletItem> = readNfcTagUseCase(tag)
            if (readNfcTagResult is Result.Success) {
                val seedResult: Result<String> = getSeedUseCase(
                    encryptedSeed = readNfcTagResult.data.seed,
                    tagId = scannedNfcTagId!!
                )
                if (seedResult is Result.Failure) {
                    _onShowPinCode.value = true
                }
                scannedNfcTagWalletItem = readNfcTagResult.data
                _onSendButtonEnabled.value = true
            } else if (readNfcTagResult is Result.Failure) {
                _onFailure.value = Constants.ResultStatus.READ_NFC_TAG_ERROR
            }
        }
    }

    fun updateWalletPolicy(
        allowList: List<String>,
        spendingWindow: List<Long>,
        pinCode: String,
        retried: Boolean = false,
    ) {
        viewModelScope.launch {
            _onSendTransactionLoading.value = true

            val seedResult: Result<String> = getSeedUseCase(
                encryptedSeed = scannedNfcTagWalletItem!!.seed,
                tagId = scannedNfcTagId!!,
                pinCode = pinCode
            )
            if (seedResult is Result.Success) {
                val seed: String = seedResult.data
                _onWalletPolicy.value?.address?.let { walletPolicyAddress: String ->
                    updateWalletPolicyUseCase(
                        secretKey = seed,
                        walletPolicyAddress = walletPolicyAddress,
                        allowList = allowList,
                        spendingWindow = spendingWindow
                    ).handleResult(
                        success = ::handleSuccessfullyTransaction,
                        failure = ::handleFailureTransaction
                    )
                    _onSendTransactionLoading.value = false
                } ?: run {
                    // Create Wallet Policy first
                    newWalletPolicyUseCase(secretKey = seed).handleResult(
                        success = {
                            if (retried) {
                                Result.failure<String>(status = Constants.ResultStatus.API_ERROR)
                            } else {
                                _onWalletPolicy.value = getWalletPolicyUseCase()
                                // Retry to update wallet policy
                                updateWalletPolicy(allowList, spendingWindow, pinCode, retried = true)
                            }
                        },
                        failure = ::handleFailureTransaction
                    )
                    _onSendTransactionLoading.value = false
                }
            } else if (seedResult is Result.Failure) {
                _onSendTransactionLoading.value = false
                _onFailure.value = Constants.ResultStatus.WRONG_PINCODE
            }
        }
    }

    fun resetCloseModal() {
        _onCloseModal.value = false
    }

    fun enableNfc() {
        _onNFCEnabled.value = true
        //SentryHelper.sendInfoBreadcrumb(category = "NFC", message = "NFC Tag detection enabled.")
    }

    fun disableNfc() {
        _onNFCEnabled.value = false
        //SentryHelper.sendInfoBreadcrumb(category = "NFC", message = "NFC Tag detection disabled.")
    }

    fun clearError() {
        _onFailure.value = Constants.ResultStatus.NOT_SET
    }

    fun clearTransactionId() {
        _onTransactionId.value = null
    }

    private fun handleFailureTransaction(resultStatus: Constants.ResultStatus) {
        _onFailure.value = resultStatus
    }

    private fun handleSuccessfullyTransaction(transactionId: String) {
        _onWalletPolicy.value = getWalletPolicyUseCase()
        _onFailure.value = Constants.ResultStatus.SUCCESS
        _onShowConfetti.value = true
        scannedNfcTagId = null
        scannedNfcTagWalletItem = null

        Handler(Looper.getMainLooper()).postDelayed({
            _onShowConfetti.value = false
            _onCloseModal.value = true
            _onSendButtonEnabled.value = false
            _onShowPinCode.value = false
            _onTransactionId.value = transactionId
        }, 3000)
    }
}
