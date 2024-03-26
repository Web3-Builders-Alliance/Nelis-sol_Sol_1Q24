use anchor_lang::prelude::*;
use crate::state::TokenPolicyState;
use crate::constants::*;
use anchor_spl::token_interface::Mint;

#[derive(Accounts)]
pub struct TokenPolicyInstructions<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    // Mint of the token account
    pub mint: InterfaceAccount<'info, Mint>,
    #[account(
        init_if_needed, 
        payer = payer,
        space = TokenPolicyState::INIT_SPACE,
        seeds = [SEED_TOKEN_POLICY_ACCOUNT, payer.key().as_ref(), mint.key().as_ref()],
        bump,
      )]
    pub token_policy: Account<'info, TokenPolicyState>,
    pub system_program: Program<'info, System>,
}

impl<'info> TokenPolicyInstructions<'info> {

    pub fn new_token_policy(&mut self, bumps: &TokenPolicyInstructionsBumps) -> Result<()> {
        
        self.token_policy.new(
            self.payer.key(),
            self.mint.key(),
            bumps.token_policy,
        )?;
        Ok(())
    }

    pub fn new_full_token_policy(&mut self, 
        spend_limit_amount: Option<u64>, 
        bumps: &TokenPolicyInstructionsBumps) -> Result<()> {
        
        self.token_policy.new_full(
            self.payer.key(),
            self.mint.key(),
            spend_limit_amount,
            bumps.token_policy,
        )?;
        Ok(())
    }

    pub fn add_spend_limit_to_token_policy(&mut self, amount: u64) -> Result<()> {
        
        self.token_policy.add_spend_limit(amount);
        Ok(())
    }

    pub fn remove_spend_limit_from_token_policy(&mut self) -> Result<()> {
        
        self.token_policy.remove_spend_limit();
        Ok(())
    }

    pub fn update_spent_last_24_in_token_policy(&mut self, spent_last_24: [i64;2]) -> Result<()> {
        
        self.token_policy.update_spent_last_24(spent_last_24);
        Ok(())
    }

}