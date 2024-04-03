use anchor_lang::prelude::*;
use crate::state::WalletPolicyState;
use crate::constants::*;

#[derive(Accounts)]
pub struct WalletPolicyInstructions<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    #[account(
        mut,
        // payer can only create a wallet_policy that is associated with payer's publickey
        seeds = [SEED_WALLET_POLICY_ACCOUNT, payer.key().as_ref()],
        bump,
      )]
    // PDA that contains settings of the user for its wallet
    pub wallet_policy: Account<'info, WalletPolicyState>,
    pub system_program: Program<'info, System>,
}

impl<'info> WalletPolicyInstructions<'info> {

    pub fn add_signer1_to_wallet_policy(&mut self, signer1: Pubkey, required: bool) -> Result<()> {
        
        // use wallet_policy method to add a 2nd signer and return result
        self.wallet_policy.add_signer1(
            signer1,
            required,
        )
    }


    pub fn remove_signer1_from_wallet_policy(&mut self) -> Result<()> {

        // use wallet_policy method to remove signer1 and return result
        self.wallet_policy.remove_signer1()
    }


    pub fn add_signer2_to_wallet_policy(&mut self, signer2: Pubkey, required: bool) -> Result<()> {
        
        // use wallet_policy method to add a 3rd signer and return result
        self.wallet_policy.add_signer2(
            signer2,
            required,
        )
    }


    pub fn remove_signer2_from_wallet_policy(&mut self) -> Result<()> {

        // use wallet_policy method to remove signer2 and return result
        self.wallet_policy.remove_signer2()
    }


    pub fn add_allowed_publickeys_to_wallet_policy(&mut self, allowed_pubkey_list: Vec<Pubkey>) -> Result<()> {

        // use wallet_policy method to add a list of allowed destination addresses and return result
        // add 1 or more addresses to the existing allow list
        self.wallet_policy.add_to_allow_list(allowed_pubkey_list)
    }


    pub fn remove_allowed_publickeys_to_wallet_policy(&mut self, remove_pubkey_list: Vec<Pubkey>) -> Result<()> {
        
        // use wallet_policy method to remove a list of destination address from the allow list and return result
        // remove 1 or more addresses from the existing allow list 
        self.wallet_policy.remove_from_allow_list(remove_pubkey_list)
    }


    pub fn add_blocked_publickeys_to_wallet_policy(&mut self, blocked_pubkey_list: Vec<Pubkey>) -> Result<()> {
        
        // use wallet_policy method to add a list of destination addresses that will be blocked and return result
        // add 1 or more addresses to the existing block list
        self.wallet_policy.add_to_block_list(blocked_pubkey_list)
    }


    pub fn remove_blocked_publickeys_to_wallet_policy(&mut self, remove_pubkey_list: Vec<Pubkey>) -> Result<()> {
        
        // use wallet_policy method to remove a list of destination address from the block list and return result
        // remove 1 or more addresses from the existing block list 
        self.wallet_policy.remove_from_block_list(remove_pubkey_list)
    }


    pub fn add_spending_window_to_wallet_policy(&mut self, spending_window: [i64; 2]) -> Result<()> {
        
        // use wallet_policy method to add a spending window and return result
        // spending_window contains 2 timestamp (begin and end), the method will extract the time in UTC (and leave out other information)
        self.wallet_policy.add_spending_window(spending_window)
    }


    pub fn remove_spending_window_from_wallet_policy(&mut self) -> Result<()> {
        
        // use wallet_policy method to remove a spending window and return result
        self.wallet_policy.remove_spending_window()
    }

}