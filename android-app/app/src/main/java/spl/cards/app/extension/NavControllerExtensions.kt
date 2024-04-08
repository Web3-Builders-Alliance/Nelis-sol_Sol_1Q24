package spl.cards.app.extension

import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import spl.cards.app.util.Constants
import java.security.InvalidParameterException

fun NavController.navigate(from: Constants.Screen, to: Constants.Screen) {
    if (from == to) throw InvalidParameterException("Can't navigate to $to")
    when (to) {
        is Constants.Screen.GetStarted -> {
            navigate(route = Constants.Screen.GetStarted.route, builder = {
                launchSingleTop = true
            })
        }
        is Constants.Screen.SetupRecoveryPhrase -> {
            navigate(route = Constants.Screen.SetupRecoveryPhrase.route, builder = {
                launchSingleTop = true
            })
        }
        is Constants.Screen.CreateNewWallet -> {
            navigate(route = to.route.replace("{secretKey}", to.secretKey), builder = {
                launchSingleTop = true
                navArgument("secretKey") {
                    nullable = false
                    type = NavType.StringType
                }
            })
        }
        is Constants.Screen.BalanceScreen -> {
            navigate(route = to.route, builder = {
                launchSingleTop = true
            })
        }
    }
}

fun NavController.navigateBack() {
    popBackStack()
}
