use anchor_lang::prelude::*;
use crate::state::UserPolicyState;
use crate::constants::*;

#[derive(Accounts)]
pub struct DeleteUserPolicy<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    #[account(
        mut,
        seeds = [SEED_USER_POLICY_ACCOUNT, payer.key().as_ref()],
        bump = user_policy.bump,
        close=payer
      )]
    pub user_policy: Account<'info, UserPolicyState>,
    pub system_program: Program<'info, System>,
}

impl<'info> DeleteUserPolicy<'info> {

    pub fn delete_user_policy(&mut self) -> Result<()> {
        
        Ok(())
    }

}