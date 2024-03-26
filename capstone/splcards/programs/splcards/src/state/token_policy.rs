use anchor_lang::prelude::*;


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

    pub fn new(&mut self,
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

    pub fn new_full(&mut self,
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

    pub fn add_spend_limit(&mut self, spend_limit_amount: u64) -> &mut Self {
        self.spend_limit_amount = Some(spend_limit_amount);
        self
    }

    pub fn remove_spend_limit(&mut self) -> &mut Self {
        self.spend_limit_amount = None;
        self
    }

    pub fn update_spent_last_24(&mut self, spent_last_24: [i64; 2]) -> &mut Self {
        self.spent_last_24 = spent_last_24;
        self
    }

    
    pub fn check_compliance() -> Result<()> {
        unimplemented!()
    }

}