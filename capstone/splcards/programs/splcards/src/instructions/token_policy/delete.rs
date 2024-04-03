use anchor_lang::prelude::*;
use crate::state::TokenPolicyState;
use crate::constants::*;
use anchor_spl::token_interface::Mint;

#[derive(Accounts)]
pub struct DeleteTokenPolicy<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    // Mint of the wrapped token
    pub mint_wrapped: InterfaceAccount<'info, Mint>,
    #[account(
        mut,
        // payer can only access token_policy that is associated with payer's publickey
        seeds = [SEED_TOKEN_POLICY_ACCOUNT, payer.key().as_ref(), mint_wrapped.key().as_ref()],
        bump = token_policy.bump,
        // close token_policy PDA and return rent to the payer
        close=payer
      )]
    // PDA that contains settings of the user for wrapped token
    pub token_policy: Account<'info, TokenPolicyState>,
    pub system_program: Program<'info, System>,
}

impl<'info> DeleteTokenPolicy<'info> {

    pub fn delete_token_policy(&mut self) -> Result<()> {
        Ok(())
    }
}