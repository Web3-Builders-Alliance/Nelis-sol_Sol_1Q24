use anchor_lang::prelude::*;

#[account]
pub struct WrapperState {
    pub symbol: String, // 4 + ~4
    pub mint_original: Pubkey,  // 32 bytes
    pub mint_wrapped: Pubkey,   // 32 bytes
    pub vault: Pubkey,   // 32 bytes
    pub bump: u8,
}

impl Space for WrapperState {
  const INIT_SPACE: usize = 8 + 32 + 32 + 32 + 32 + 1;
  // total: 89 bytes
}

impl WrapperState {

    pub fn new(
        &mut self,
        symbol: String,
        mint_original: Pubkey,
        mint_wrapped: Pubkey,
        vault: Pubkey,
        bump: u8,
    ) -> Result<()> {
        self.symbol = symbol;
        self.mint_original = mint_original;
        self.mint_wrapped = mint_wrapped;
        self.vault = vault;
        self.bump = bump;

        Ok(())
    }

    // for now, only the symbol can be updated
    // for security considerations, other fields can't be changed
    pub fn update(
        &mut self,
        symbol: String,
    ) -> Result<()> {
        self.symbol = symbol;

        Ok(())
    }

}