package spl.cards.app.usecase

import spl.cards.app.model.WalletPolicy
import spl.cards.app.repository.SolanaRepository

class GetWalletPolicyUseCase(private val solanaRepository: SolanaRepository) {

    operator fun invoke(): WalletPolicy {
        val address: String? = solanaRepository.getWalletPolicyAddress()
        val allowList: List<String> = solanaRepository.getWalletPolicyAllowList()
        val spendingWindow: List<Long> = solanaRepository.getWalletPolicySpendingWindow()
        return WalletPolicy(
            address = address,
            allowList = allowList,
            spendingWindow = spendingWindow
        )
    }
}
