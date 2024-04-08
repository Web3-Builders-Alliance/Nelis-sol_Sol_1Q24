package spl.cards.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import spl.cards.app.ui.screen.balance.BalanceScreen
import spl.cards.app.ui.screen.createnewwallet.CreateNewWalletScreen
import spl.cards.app.ui.screen.getstarted.GetStartedScreen
import spl.cards.app.ui.screen.hasNfcAdapter
import spl.cards.app.ui.screen.setuprecoveryphrase.SetupRecoveryPhraseScreen
import spl.cards.app.ui.theme.SPLCardsTheme
import spl.cards.app.util.Constants

@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasNfcAdapter(context = this).not()) {
            Toast.makeText(this, getString(R.string.label_device_has_no_nfc), Toast.LENGTH_LONG).show()
            finish()
        } else {
            setContent {
                val navController = rememberNavController()

                SPLCardsTheme {
                    androidx.compose.material.Surface(color = androidx.compose.material.MaterialTheme.colors.background) {
                        ComposeNavigation(navController)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun ComposeNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Constants.Screen.GetStarted.route
    ) {
        composable(route = Constants.Screen.GetStarted.route) {
            GetStartedScreen(navController = navController)
        }
        composable(route = Constants.Screen.SetupRecoveryPhrase.route) {
                    SetupRecoveryPhraseScreen(navController = navController)
        }
        composable(route = Constants.Screen.CreateNewWallet().route) {
            it.arguments?.getString("secretKey")?.let { secretKey: String ->
                CreateNewWalletScreen(navController = navController, secretKey = secretKey)
            }
        }
        composable(route = Constants.Screen.BalanceScreen.route) {
            BalanceScreen(navController = navController)
        }
    }
}
