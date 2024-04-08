//
// Errors
// Metaplex
//
// This code was generated locally by Funkatronics on 2024-04-07
//
package spl.cards.program

import kotlin.Int
import kotlin.String

sealed interface SplCardProgramError {
    val code: Int

    val message: String
}

class SpendLimitExceeded : SplCardProgramError {
    override val code: Int = 6000

    override val message: String = "Spend Limit Exceeded"
}

class PubkeyNotInAllowList : SplCardProgramError {
    override val code: Int = 6001

    override val message: String = "Pubkey Not In Allow List"
}

class PubkeyInBlockList : SplCardProgramError {
    override val code: Int = 6002

    override val message: String = "Pubkey In Block List"
}

class SpendingWindowViolation : SplCardProgramError {
    override val code: Int = 6003

    override val message: String = "Spending Window Violation"
}

class MissingRequiredSigners : SplCardProgramError {
    override val code: Int = 6004

    override val message: String = "Missing Required Signers"
}

class NotInSpendWindow : SplCardProgramError {
    override val code: Int = 6005

    override val message: String = "Not In Spend Window"
}

class TransferHookIntentionalFail : SplCardProgramError {
    override val code: Int = 6006

    override val message: String = "Transfer Hook fail - for testing"
}
