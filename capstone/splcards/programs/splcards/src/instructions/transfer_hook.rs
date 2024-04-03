use anchor_lang::prelude::*;

use anchor_spl::token_interface::{Mint, TokenAccount};

use crate::{
    constants::*, 
    state::TokenPolicyState,
};

use crate::state::WalletPolicyState;


#[derive(Accounts)]
pub struct TransferHook<'info> {
    #[account(
        token::mint = mint,
        token::authority = owner,
    )]
    pub source_token: InterfaceAccount<'info, TokenAccount>,
    // Mint is the mint of the wrapped token
    pub mint: InterfaceAccount<'info, Mint>,
    #[account(
        token::mint = mint,
    )]
    pub destination_token: InterfaceAccount<'info, TokenAccount>,
    /// CHECK: source token account owner, can be SystemAccount or PDA owned by another program
    pub owner: UncheckedAccount<'info>,
    /// CHECK: ExtraAccountMetaList Account,
    #[account(
        seeds = [b"extra-account-metas", mint.key().as_ref()],
        bump
    )]
    pub extra_account_meta_list: UncheckedAccount<'info>,
    #[account(
        mut,
        seeds = [SEED_WALLET_POLICY_ACCOUNT, owner.key().as_ref()],
        bump
    )]
    /// CHECK: wallet policy can exist but doesn't need to
    pub wallet_policy: UncheckedAccount<'info>,
    #[account(
        mut,
        seeds = [SEED_TOKEN_POLICY_ACCOUNT, owner.key().as_ref(), mint.key().as_ref()],
        bump
    )]
    /// CHECK: token policy can exist but doesn't need to
    pub token_policy: UncheckedAccount<'info>,
}

impl<'info> TransferHook<'info> {
    pub fn transfer_hook(&mut self, amount: u64) -> Result<()> {

        // retrieve data of wallet_policy acount for compliance check
        let wallet_policy_info = self.wallet_policy.to_account_info();
        let wallet_policy_data = wallet_policy_info.try_borrow_mut_data()?;

        // Try and Deserialize the Account, if it deserialize then we know that the sender has a wallet policy account and we should check it.
        match  WalletPolicyState::try_deserialize(&mut &wallet_policy_data[..]) {
            Ok(wallet_policy) => {

                let current_time = Clock::get()?.unix_timestamp;

                // check if transaction is compliant with wallet policies set by user
                let wallet_compliance_result = wallet_policy.check_compliance(
                    true, // signer 1 - hardcoded for now
                    true, // signer 2 - hardcoded for now
                    self.destination_token.owner, 
                    current_time
                );

                // return ok or error
                return wallet_compliance_result
            },

            Err(_) => {
                // Do nothing: the user has no wallet_policy account
            }
        }

        // retrieve data of token_policy acount for compliance check
        let token_policy_info = self.token_policy.to_account_info();
        let token_policy_data = token_policy_info.try_borrow_mut_data()?;

        // Try and Deserialize the Account, if it deserialize then we know that the sender has a token policy account and we should check it.
        match  TokenPolicyState::try_deserialize(&mut &token_policy_data[..]) {
            Ok(token_policy) => {

                let current_time = Clock::get()?.unix_timestamp;

                // check if transaction is compliant with token policies set by user
                let token_compliance_result = token_policy.check_compliance(
                    amount,
                    true, // signer 1 - hardcoded for now
                    true, // signer 2 - hardcoded for now
                    current_time
                );

                // return ok or error
                return token_compliance_result

            },
            Err(_) => {
                // Do nothing: the user has no token_policy account
            }
        }

        Ok(())

    }
}

