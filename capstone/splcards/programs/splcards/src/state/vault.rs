use anchor_lang::prelude::*;


#[account]
pub struct Vault {
    pub authority: Pubkey, // 32 bytes
    pub mints: vec<Pubkey>, // 32 bytes
    pub seed: u64, // 8 bytes
    pub amount: u64, // 8 bytes
    pub bump: u8,   // 1 byte
}

impl Space for Vault {
    const INIT_SPACE: usize = 8 + 32 + 32 + 8 + 8 + 1;
    // total: 81 bytes
}
