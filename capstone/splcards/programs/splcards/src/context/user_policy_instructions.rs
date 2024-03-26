use anchor_lang::prelude::*;
use crate::state::UserPolicyState;
use crate::constants::*;

#[derive(Accounts)]
pub struct UserPolicyInstructions<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    #[account(
        init_if_needed, 
        payer = payer,
        space = UserPolicyState::INIT_SPACE,
        seeds = [SEED_USER_POLICY_ACCOUNT, payer.key().as_ref()],
        bump,
      )]
    pub user_policy: Account<'info, UserPolicyState>,
    pub system_program: Program<'info, System>,
}

impl<'info> UserPolicyInstructions<'info> {

    pub fn new_full_user_policy(&mut self,
        signer1: Option<Pubkey>,
        required_signer1: bool, 
        signer2: Option<Pubkey>,
        required_signer2: bool,
        allow_list: Vec<Pubkey>,
        block_list: Vec<Pubkey>,
        spending_window: Option<[i64;2]>,
        bumps: &UserPolicyInstructionsBumps) -> Result<()> {

            self.user_policy.new_full(
                self.payer.key(),
                signer1,
                required_signer1,
                signer2,
                required_signer2,
                allow_list,
                block_list,
                spending_window,
                bumps.user_policy,
            )
    }


    pub fn new_user_policy(&mut self, bumps: &UserPolicyInstructionsBumps) -> Result<()> {

        self.user_policy.new(
            self.payer.key(),
            bumps.user_policy,
        )
    }

    pub fn add_signer1_to_user_policy(&mut self, signer1: Pubkey, required: bool) -> Result<()> {
        
        self.user_policy.add_signer1(
            signer1,
            required,
        );
        Ok(())
    }

    pub fn remove_signer1_from_user_policy(&mut self) -> Result<()> {
        self.user_policy.remove_signer1();
        Ok(())
    }

    pub fn add_signer2_to_user_policy(&mut self, signer2: Pubkey, required: bool) -> Result<()> {
        
        self.user_policy.add_signer2(
            signer2,
            required,
        );
        Ok(())
    }

    pub fn remove_signer2_from_user_policy(&mut self) -> Result<()> {
        self.user_policy.remove_signer2();
        Ok(())
    }

    pub fn add_allowed_publickeys_to_user_policy(&mut self, allowed_pubkey_list: Vec<Pubkey>) -> Result<()> {
        self.user_policy.add_to_allow_list(allowed_pubkey_list);
        Ok(())
    }

    pub fn remove_allowed_publickeys_to_user_policy(&mut self, remove_pubkey_list: Vec<Pubkey>) -> Result<()> {
        self.user_policy.remove_from_allow_list(remove_pubkey_list);
        Ok(())
    }

    pub fn add_blocked_publickeys_to_user_policy(&mut self, blocked_pubkey_list: Vec<Pubkey>) -> Result<()> {
        self.user_policy.add_to_block_list(blocked_pubkey_list);
        Ok(())
    }

    pub fn remove_blocked_publickeys_to_user_policy(&mut self, remove_pubkey_list: Vec<Pubkey>) -> Result<()> {
        self.user_policy.remove_from_block_list(remove_pubkey_list);
        Ok(())
    }

    pub fn add_spending_window_to_user_policy(&mut self, spending_window: [i64; 2]) -> Result<()> {
        self.user_policy.add_spending_window(spending_window);
        Ok(())
    }

    pub fn remove_spending_window_from_user_policy(&mut self) -> Result<()> {
        self.user_policy.remove_spending_window();
        Ok(())
    }

}