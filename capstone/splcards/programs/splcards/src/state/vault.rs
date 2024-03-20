use anchor_lang::prelude::*;


#[account]
pub struct Vault {
    pub authority: Pubkey, // 32 bytes
    pub mints: Vec<Pubkey>, // 32 bytes
    pub seed: u64, // 8 bytes
    pub amount: u64, // 8 bytes
    pub bump: u8,   // 1 byte
}

impl Space for Vault {
    const INIT_SPACE: usize = 8 + 32 + 32 + 8 + 8 + 1;
    // total: 81 bytes
}

 
// use a global vault (not per user) - don't need a vault if we do it global
// if I have time left - add blockers / extra checks for suspicious behaviour (like 50% of all original tokens are withdrawn within 1 hour)



impl Vault {

    pub fn new() -> Result<()> {
        unimplemented!()
    }

    pub fn update() -> Result<()> {
        unimplemented!()
    }

    pub fn deposit() -> Result<()> {
        unimplemented!()
    }

    pub fn withdraw() -> Result<()> {
        unimplemented!()
    }

    pub fn get_address() -> Result<()> {
        unimplemented!()
    }

    pub fn get_token_account() -> Result<()> {
        unimplemented!()
    }
    
    pub fn get_token_accounts() -> Result<()> {
        unimplemented!()
    }

}