use anchor_lang::prelude::*;
use crate::state::WalletPolicyState;
use crate::constants::*;

#[derive(Accounts)]
pub struct CreateWalletPolicy<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    #[account(
        init,
        payer = payer,
        space = WalletPolicyState::INIT_SPACE,
        // payer can only create a wallet_policy that is associated with payer's publickey
        seeds = [SEED_WALLET_POLICY_ACCOUNT, payer.key().as_ref()],
        bump,
      )]
    // PDA that contains settings of the user for its wallet
    pub wallet_policy: Account<'info, WalletPolicyState>,
    pub system_program: Program<'info, System>,
}

impl<'info> CreateWalletPolicy<'info> {

    // Create new wallet policy PDA with default settings
    pub fn new_wallet_policy(&mut self, bumps: &CreateWalletPolicyBumps) -> Result<()> {

        // use wallet_policy method to create new and return result
        self.wallet_policy.new(
            self.payer.key(),
            bumps.wallet_policy,
        )
    }

    // Create new wallet policy PDA with specified settings
    pub fn new_full_wallet_policy(&mut self,
        signer1: Option<Pubkey>,
        required_signer1: bool, 
        signer2: Option<Pubkey>,
        required_signer2: bool,
        allow_list: Vec<Pubkey>,
        block_list: Vec<Pubkey>,
        spending_window: Option<[i64;2]>,
        bumps: &CreateWalletPolicyBumps) -> Result<()> {

            // use wallet_policy method to create new and return result
            self.wallet_policy.new_full(
                self.payer.key(),
                signer1,
                required_signer1,
                signer2,
                required_signer2,
                allow_list,
                block_list,
                spending_window,
                bumps.wallet_policy,
            )
    }

}