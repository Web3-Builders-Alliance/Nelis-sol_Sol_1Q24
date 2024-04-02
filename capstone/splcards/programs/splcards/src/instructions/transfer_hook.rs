use anchor_lang::prelude::*;

use anchor_spl::{
    associated_token::AssociatedToken,
    token_interface::{Mint, TokenAccount, Token2022},
};

use crate::{
    constants::*, 
    state::WalletPolicyState,
    state::TokenPolicyState,
};

use crate::error::WalletPolicyErrorCodes;


#[derive(Accounts)]
pub struct TransferHook<'info> {
    #[account(
        token::mint = mint,
        token::authority = owner,
    )]
    pub source_token: InterfaceAccount<'info, TokenAccount>,
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
    pub wallet_policy: UncheckedAccount<'info>, // Account<'info, WalletPolicyState>,
    #[account(
        mut,
        seeds = [SEED_TOKEN_POLICY_ACCOUNT, owner.key().as_ref(), mint.key().as_ref()],
        bump
    )]
    /// CHECK: token policy can exist but doesn't need to
    pub token_policy: UncheckedAccount<'info>, // Account<'info, WalletPolicyState>,
}

impl<'info> TransferHook<'info> {
    pub fn transfer_hook(&mut self, amount: u64) -> Result<()> {

        let wallet_policy_info = self.wallet_policy.to_account_info();
        // let wallet_policy_data = wallet_policy_info.try_borrow_mut_data()?;
        msg!("Wallet policy data: {:?}", wallet_policy_info);


        // // Try and Deserialize the Account, if it deserialize then we know that the sender has a wallet policy account and we should check it.
        // match  WalletPolicyState::try_deserialize(&mut &wallet_policy_data[..]) {
        //     Ok(wallet_policy) => {

        //         let current_time = Clock::get()?.unix_timestamp;

        //         wallet_policy.check_compliance(
        //             true, // signer 1 - hardcoded for now
        //             true, // signer 2 - hardcoded for now
        //             self.destination_token.owner, 
        //             current_time
        //         )?;

        //     },
        //     Err(error) => {
        //         return Err(error).into()
        //     }
        // }

        let token_policy_info = self.token_policy.to_account_info();
        let token_policy_data = token_policy_info.try_borrow_mut_data()?;

        // Try and Deserialize the Account, if it deserialize then we know that the sender has a wallet policy account and we should check it.
        match  TokenPolicyState::try_deserialize(&mut &token_policy_data[..]) {
            Ok(token_policy) => {

                let current_time = Clock::get()?.unix_timestamp;

                token_policy.check_compliance(
                    amount,
                    true, // signer 1 - hardcoded for now
                    true, // signer 2 - hardcoded for now
                    current_time
                )?;

            },
            Err(_) => {
                msg!("Token policy error")
            }
        }


        Ok(())

    }
}

