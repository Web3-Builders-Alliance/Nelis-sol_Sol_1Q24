use anchor_lang::prelude::*;


#[account]
pub struct Wrapper {
    pub mint_original: Pubkey,  // 32 bytes
    pub mint_wrapped: Pubkey,   // 32 bytes
    pub amount: u128,   // 16 bytes
    pub bump: u8,   // 1 byte
}

impl Space for Wrapper {
  const INIT_SPACE: usize = 8 + 32 + 32 + 16 + 1;
  // total: 89 bytes
}

impl Wrapper {

    pub fn new() -> Result<()> {
        unimplemented!()
    }

    pub fn update() -> Result<()> {
        unimplemented!()
    }

    pub fn wrap() -> Result<()> {
        unimplemented!()
    }

    pub fn unwrap() -> Result<()> {
        unimplemented!()
    }

    pub fn get_address() -> Result<()> {
        unimplemented!()
    }

}