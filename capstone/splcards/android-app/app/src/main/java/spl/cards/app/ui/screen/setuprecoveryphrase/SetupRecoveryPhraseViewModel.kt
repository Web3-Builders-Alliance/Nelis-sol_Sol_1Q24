package spl.cards.app.ui.screen.setuprecoveryphrase

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import cash.z.ecc.android.bip39.Mnemonics
import com.solana.core.DerivationPath
import com.solana.core.HotAccount
import com.solana.vendor.TweetNaclFast
import com.solana.vendor.bip32.wallet.DerivableType
import com.solana.vendor.bip32.wallet.SolanaBip44
import com.solana.vendor.bip39.Mnemonic
import com.solana.vendor.bip39.WordCount
import org.bitcoinj.core.Base58
import org.bitcoinj.crypto.MnemonicCode
import spl.cards.app.extension.navigate
import spl.cards.app.util.Constants

class SetupRecoveryPhraseViewModel : ViewModel() {

    val mnemonicCode: List<String> = Mnemonic(WordCount.COUNT_24).phrase

    fun navigateToCreateNewWallet(navController: NavController) {
        val privateKey = SolanaBip44().getPrivateKeyFromSeed(MnemonicCode.toSeed(mnemonicCode, ""), DerivableType.BIP44CHANGE)
        val secretKey: String = Base58.encode(TweetNaclFast.Signature.keyPair_fromSeed(privateKey).secretKey)
        navController.navigate(from = Constants.Screen.SetupRecoveryPhrase, to = Constants.Screen.CreateNewWallet(secretKey = secretKey))
    }
}
