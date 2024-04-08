use anchor_lang::prelude::*;
use crate::state::TokenPolicyState;
use crate::constants::*;
use anchor_spl::token_interface::Mint;

#[derive(Accounts)]
pub struct CreateTokenPolicy<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    // Mint of the wrapped token
    pub mint_wrapped: InterfaceAccount<'info, Mint>,
    #[account(
        init, 
        payer = payer,
        space = TokenPolicyState::INIT_SPACE,
        // payer can only create a token_policy that is associated with payer's publickey
        seeds = [SEED_TOKEN_POLICY_ACCOUNT, payer.key().as_ref(), mint_wrapped.key().as_ref()],
        bump,
      )]
    // PDA that contains settings of the user for wrapped token
    pub token_policy: Account<'info, TokenPolicyState>,
    pub system_program: Program<'info, System>,
}

impl<'info> CreateTokenPolicy<'info> {

    // Create new token policy PDA with default settings
    pub fn new_token_policy(
        &mut self, 
        bumps: &CreateTokenPolicyBumps
    ) -> Result<()> {

        // use token_policy method to create new and return result
        self.token_policy.new(
            self.payer.key(),
            self.mint_wrapped.key(),
            bumps.token_policy,
        )
    }

    // Create new token policy PDA with specified settings
    pub fn new_full_token_policy(
        &mut self, 
        spend_limit_amount: Option<u64>, 
        bumps: &CreateTokenPolicyBumps
    ) -> Result<()> {
        
        // use token_policy method to create new and return result
        self.token_policy.new_full(
            self.payer.key(),
            self.mint_wrapped.key(),
            spend_limit_amount,
            bumps.token_policy,
        )
    }
}