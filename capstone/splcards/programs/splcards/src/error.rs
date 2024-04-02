use anchor_lang::prelude::*;

#[error_code]
pub enum WalletPolicyErrorCodes {
    #[msg("Spend Limit Exceeded")]
    SpendLimitExceeded,
    #[msg("Pubkey Not In Allow List")]
    PubkeyNotInAllowList,
    #[msg("Pubkey In Block List")]
    PubkeyInBlockList,
    #[msg("Spending Window Violation")]
    SpendingWindowViolation,
    #[msg("Missing Required Signers")]
    MissingRequiredSigners,
    #[msg("Not In Spend Window")]
    NotInSpendWindow,
    #[msg("Transfer Hook fail - for testing")]
    TransferHookIntentionalFail,
}