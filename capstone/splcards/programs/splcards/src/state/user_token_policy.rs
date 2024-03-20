use anchor_lang::prelude::*;


#[account]
pub struct UserTokenPolicy {
    pub wallet: Pubkey, // 32 bytes
    pub bank: Pubkey,   // 32 bytes
    pub mint: Pubkey,   // 32 bytes
    pub spent_last_24: (i64, u64),  // 8 + 8 = 16 bytes
    pub spend_limit_amount: Option<u64>,    // 1 + 8 = 9 bytes
    pub bump: u8,   // 1 byte
}


impl Space for UserTokenPolicy {
    const INIT_SPACE: usize = 8 + 32 * 3 + 16 + 9 + 1;
    // total: 122 bytes
}

impl UserTokenPolicy {

    pub fn new() -> Result<()> {
        unimplemented!()
    }

    pub fn update() -> Result<()> {
        unimplemented!()
    }

    pub fn check_compliance() -> Result<()> {
        unimplemented!()
    }

}