use anchor_lang::prelude::*;

use crate::error::WalletPolicyErrorCodes;

#[account]
pub struct WalletPolicyState {
    pub authority: Pubkey,          // 32 bytes
    pub signer1: Option<Pubkey>,    // 1 + 32 bytes
    pub required_signer1: bool,     // 1 byte
    pub signer2: Option<Pubkey>,    // 1 + 32 bytes
    pub required_signer2: bool,     // 1 byte
    pub allow_list: Vec<Pubkey>,    // 1 + 4 + 32 = 37 bytes
    pub block_list: Vec<Pubkey>,    // 1 + 4 + 32 = 37 bytes
    pub spending_window: Option<[i64; 2]>,    // 1 + 8 + 8 = 17 bytes
    pub bump: u8,   // 1 byte
    // + 32 * 3 allow list pubkeys
}


impl Space for WalletPolicyState {
    const INIT_SPACE: usize = 8 + 32 * 3 + 1 * 2 + 37 * 2 + 17 + 1 + 32 * 3;
}


impl WalletPolicyState {

    pub fn new(&mut self,
        authority: Pubkey,
        bump: u8,
        ) -> Result<()> {
            self.authority = authority;
            self.signer1 = None;
            self.required_signer1 = false;
            self.signer2 = None;
            self.required_signer2 = false;
            self.allow_list = Vec::new();
            self.block_list = Vec::new();
            self.spending_window = None;
            self.bump = bump;

            Ok(())
    }

    pub fn new_full(&mut self,
        authority: Pubkey,
        signer1: Option<Pubkey>,
        required_signer1: bool,
        signer2: Option<Pubkey>,
        required_signer2: bool,
        allow_list: Vec<Pubkey>,
        block_list: Vec<Pubkey>,
        spending_window: Option<[i64;2]>,
        bump: u8,
        ) -> Result<()> {
            self.authority = authority;
            self.signer1 = signer1;
            self.required_signer1 = required_signer1;
            self.signer2 = signer2;
            self.required_signer2 = required_signer2;
            self.allow_list = allow_list;
            self.block_list = block_list;
            self.spending_window = spending_window;
            self.bump = bump;

            Ok(())
    }

    pub fn add_signer1(
        &mut self, 
        signer1: Pubkey, 
        required_signer1: bool
    ) -> Result<()> {

        self.signer1 = Some(signer1);
        self.required_signer1 = required_signer1;

        Ok(())
    }

    pub fn remove_signer1(&mut self) -> Result<()> {

        self.signer1 = None;
        self.required_signer1 = false;
        
        Ok(())
    }

    pub fn add_signer2(
        &mut self, 
        signer2: Pubkey, 
        required_signer2: bool
    ) -> Result<()> {

        self.signer2 = Some(signer2);
        self.required_signer2 = required_signer2;
        
        Ok(())
    }

    pub fn remove_signer2(&mut self) -> Result<()> {

        self.signer2 = None;
        self.required_signer2 = false;
        
        Ok(())
    }

    pub fn add_to_allow_list(
        &mut self, 
        allowed_pubkey_list: Vec<Pubkey>
    ) -> Result<()> {

        for item_to_be_added in allowed_pubkey_list {
            self.allow_list.push(item_to_be_added);
        }

        Ok(())
    }

    pub fn remove_from_allow_list(
        &mut self, 
        remove_pubkey_list: Vec<Pubkey>
    ) -> Result<()> {

        for item_to_be_removed in remove_pubkey_list {
            self.allow_list.retain(|&x| x != item_to_be_removed);
        }

        Ok(())
    }


    pub fn add_to_block_list(
        &mut self, 
        blocked_pubkey_list: Vec<Pubkey>
    ) -> Result<()> {

        for item_to_be_added in blocked_pubkey_list {
            self.block_list.push(item_to_be_added);
        }
        
        Ok(())
    }

    pub fn remove_from_block_list(
        &mut self, 
        remove_pubkey_list: Vec<Pubkey>
    ) -> Result<()> {

        for item_to_be_removed in remove_pubkey_list {
            self.block_list.retain(|&x| x != item_to_be_removed);
        }
        
        Ok(())
    }

    pub fn add_spending_window(
        &mut self, 
        spending_window: [i64; 2] 
    ) -> Result<()> {

        self.spending_window = Some(spending_window);
        Ok(())
    }

    pub fn remove_spending_window(&mut self) -> Result<()> {
        
        self.spending_window = None;
        Ok(())
    }


    pub fn check_compliance(
        &mut self,
        signer1_is_signer: bool,
        signer2_is_signer: bool,
        destination: Pubkey,
        current_timestamp: i64,
    ) -> Result<()> {

        // requires both signers if requires_signers is true
        if (self.required_signer1 && !signer1_is_signer) || (self.required_signer2 && !signer2_is_signer) {
            return Err(WalletPolicyErrorCodes::MissingRequiredSigners.into());
        }

        // block list
        if self.block_list.contains(&destination) {
            return Err(WalletPolicyErrorCodes::PubkeyInBlockList.into());
        }

        // allow list
        if !self.allow_list.contains(&destination) {
            return Err(WalletPolicyErrorCodes::PubkeyNotInAllowList.into());
        }

        // spending window
        if let Some([start, end]) = self.spending_window {
            if current_timestamp % 86400 < start % 86400 || current_timestamp % 86400 > end % 86400
            {
                return Err(WalletPolicyErrorCodes::SpendingWindowViolation.into());
            }
        }

        Ok(())
    }

}