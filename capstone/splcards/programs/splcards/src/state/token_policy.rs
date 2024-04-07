use anchor_lang::prelude::*;

use crate::error::WalletPolicyErrorCodes;


#[account]
pub struct TokenPolicyState {
    pub authority: Pubkey, // 32 bytes
    pub mint: Pubkey,   // 32 bytes
    pub spent_last_24: [i64; 2],  // 8 + 8 = 16 bytes
    pub spend_limit_amount: Option<u64>,    // 1 + 8 = 9 bytes
    pub bump: u8,   // 1 byte
}

impl Space for TokenPolicyState {
    const INIT_SPACE: usize = 8 + 32 * 2 + 16 + 9 + 1;
    // total: 122 bytes
}

impl TokenPolicyState {

    pub fn new(
        &mut self,
        authority: Pubkey,
        mint: Pubkey,
        bump: u8,
        ) -> Result<()> {
            self.authority = authority;
            self.mint = mint;
            self.spent_last_24 = [0, 0];
            self.spend_limit_amount = None;
            self.bump = bump;

            Ok(())
    }

    pub fn new_full(
        &mut self,
        authority: Pubkey,
        mint: Pubkey,
        spend_limit_amount: Option<u64>,
        bump: u8,
        ) -> Result<()> {
            self.authority = authority;
            self.mint = mint;
            self.spent_last_24 = [0, 0];
            self.spend_limit_amount = spend_limit_amount;
            self.bump = bump;

            Ok(())
    }

    pub fn add_spend_limit(
        &mut self, 
        spend_limit_amount: u64
    ) -> Result<()> {
        
        self.spend_limit_amount = Some(spend_limit_amount);
        Ok(())
    }

    pub fn remove_spend_limit(&mut self) -> Result<()> {

        self.spend_limit_amount = None;
        Ok(())
    }

    pub fn update_spent_last_24(
        &mut self, 
        spent_last_24: [i64; 2]
    ) -> Result<()> {
        
        self.spent_last_24 = spent_last_24;
        Ok(())
    }

    
    pub fn check_compliance(
        &mut self,
        amount: i64,
        signer1: bool,
        signer2: bool,
        current_timestamp: i64,
    ) -> Result<()> {

        if let Some(spend_limit_amount) = self.spend_limit_amount {
            // get start of today timestamp
            let today_timestamp = current_timestamp - (current_timestamp % 86400);

            // check if the amount spent in the last 24 hours is greater than the spend limit
            if self.spent_last_24[0] == today_timestamp {
                if self.spent_last_24[1] + amount as i64 > spend_limit_amount as i64 {
                    if !signer1 && !signer2 {
                        return Err(WalletPolicyErrorCodes::SpendLimitExceeded.into());
                    }
                }
                
                self.spent_last_24[1] += amount;
        
            } else {

                if amount > spend_limit_amount as i64 {
                    if !signer1 && !signer2 {
                        return Err(WalletPolicyErrorCodes::SpendLimitExceeded.into());
                    }
                }
                self.spent_last_24[0] = today_timestamp;
                self.spent_last_24[1] = amount as i64;

            }
        }

        Ok(())

    }

}

