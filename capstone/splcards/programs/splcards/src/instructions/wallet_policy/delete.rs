use anchor_lang::prelude::*;
use crate::state::WalletPolicyState;
use crate::constants::*;

#[derive(Accounts)]
pub struct DeleteWalletPolicy<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    #[account(
        mut,
        seeds = [SEED_USER_POLICY_ACCOUNT, payer.key().as_ref()],
        bump = wallet_policy.bump,
        close=payer
      )]
    pub wallet_policy: Account<'info, WalletPolicyState>,
    pub system_program: Program<'info, System>,
}

impl<'info> DeleteWalletPolicy<'info> {

    pub fn delete_wallet_policy(&mut self) -> Result<()> {
        
        Ok(())
    }

}