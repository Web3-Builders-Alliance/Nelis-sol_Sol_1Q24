use anchor_lang::prelude::*;
use crate::state::TokenPolicyState;
use crate::constants::*;
use anchor_spl::token_interface::Mint;

#[derive(Accounts)]
pub struct TokenPolicyInstructions<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    // Mint of the wrapped token
    pub mint_wrapped: InterfaceAccount<'info, Mint>,
    #[account(
        mut,
        // payer can only access token_policy that is associated with payer's publickey
        seeds = [SEED_TOKEN_POLICY_ACCOUNT, payer.key().as_ref(), mint_wrapped.key().as_ref()],
        bump,
      )]
    // PDA that contains settings of the user for wrapped token
    pub token_policy: Account<'info, TokenPolicyState>,
    pub system_program: Program<'info, System>,
}

impl<'info> TokenPolicyInstructions<'info> {

    pub fn add_spend_limit_to_token_policy(&mut self, amount: u64) -> Result<()> {

        // use token_policy method to add a spend limit per 24 hours and return result
        self.token_policy.add_spend_limit(amount)
    }

    pub fn remove_spend_limit_from_token_policy(&mut self) -> Result<()> {
        
        // use token_policy method to remove a spend limit and return result
        self.token_policy.remove_spend_limit()
    }

    pub fn update_spent_last_24_in_token_policy(&mut self, spent_last_24: [i64;2]) -> Result<()> {
        
        // use token_policy method to add a new spent and return result
        // will mostly be used with a transfer, and not as a separate call
        self.token_policy.update_spent_last_24(spent_last_24)
    }

}