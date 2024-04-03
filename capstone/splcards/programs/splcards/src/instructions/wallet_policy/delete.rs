use anchor_lang::prelude::*;
use crate::state::WalletPolicyState;
use crate::constants::*;

#[derive(Accounts)]
pub struct DeleteWalletPolicy<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    #[account(
        mut,
        // payer can only create a wallet_policy that is associated with payer's publickey
        seeds = [SEED_WALLET_POLICY_ACCOUNT, payer.key().as_ref()],
        bump = wallet_policy.bump,
        close=payer
      )]
    // PDA that contains settings of the user for its wallet
    pub wallet_policy: Account<'info, WalletPolicyState>,
    pub system_program: Program<'info, System>,
}

impl<'info> DeleteWalletPolicy<'info> {

    pub fn delete_wallet_policy(&mut self) -> Result<()> {
        
        Ok(())
    }

}