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
    pub wallet_policy: Account<'info, WalletPolicyState>,
    // /// CHECK: wallet policy can exist but doesn't need to
    // pub wallet_policy: UncheckedAccount<'info>,
    #[account(
        mut,
        seeds = [SEED_TOKEN_POLICY_ACCOUNT, owner.key().as_ref(), mint.key().as_ref()],
        bump
    )]
    pub token_policy: Account<'info, TokenPolicyState>,
}

impl<'info> TransferHook<'info> {
    pub fn transfer_hook(&mut self, amount: u64) -> Result<()> {

        let current_time = Clock::get()?.unix_timestamp;

        // check if transaction is compliant with wallet policies set by user
        self.wallet_policy.check_compliance(
            true, // override when signer1 is a signer
            true, // override when signer2 is a signer
            self.destination_token.owner, 
            current_time
        )?;


        // check if transaction is compliant with token policies set by user
        self.token_policy.check_compliance(
            amount as i64,
            false, // override when signer2 is a signer
            false, // override when signer2 is a signer
            current_time
        )?;


        Ok(())

    }
}

