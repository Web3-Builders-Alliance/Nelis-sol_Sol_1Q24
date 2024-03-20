use anchor_lang::prelude::*;


#[account]
pub struct UserPolicy {
    pub authority: Pubkey,  // 32 bytes
    pub signer1: Pubkey,    // 32 bytes
    pub required_signer1: bool, // 1 byte
    pub signer2: Pubkey,    // 32 bytes
    pub required_signer2: bool, // 1 byte
    pub allow_list: Option<Vec<Pubkey>>,    // 1 + 4 + 32 = 37 bytes
    pub block_list: Option<Vec<Pubkey>>,    // 1 + 4 + 32 = 37 bytes
    pub spending_window: Option<(i64, i64)>,    // 1 + 8 + 8 = 17 bytes
    pub bump: u8,   // 1 byte
}


impl Space for UserPolicy {
    const INIT_SPACE: usize = 8 + 32 * 3 + 1 * 2 + 37 * 2 + 17 + 1;
    // total: 198 bytes
}


impl UserPolicy {

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