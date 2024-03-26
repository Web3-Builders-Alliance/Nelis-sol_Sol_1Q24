use anchor_lang::prelude::*;
use crate::state::TokenPolicyState;
use crate::constants::*;
use anchor_spl::token_interface::Mint;

#[derive(Accounts)]
pub struct DeleteTokenPolicy<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    // Mint of the token account
    pub mint: InterfaceAccount<'info, Mint>,
    #[account(
        mut,
        seeds = [SEED_TOKEN_POLICY_ACCOUNT, payer.key().as_ref(), mint.key().as_ref()],
        bump = token_policy.bump,
        close=payer
      )]
    pub token_policy: Account<'info, TokenPolicyState>,
    pub system_program: Program<'info, System>,
}

impl<'info> DeleteTokenPolicy<'info> {

    pub fn delete_token_policy(&mut self) -> Result<()> {
        Ok(())
    }
}