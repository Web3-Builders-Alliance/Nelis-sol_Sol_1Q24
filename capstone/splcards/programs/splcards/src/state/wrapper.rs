use anchor_lang::prelude::*;

use crate::constants::SEED_WRAPPER_ACCOUNT;

#[account]
pub struct Wrapper {
    pub symbol: String, // 4 + ~4
    pub mint_original: Pubkey,  // 32 bytes
    pub mint_wrapped: Pubkey,   // 32 bytes
    pub vault: Pubkey,   // 32 bytes
    pub bump: u8,
}

// create a PDA and call it USDC-22 (example)
// you can do this stateless, you can derive the mint of the wrapped on the fly, based off the original mint
// you store the USDC in a ATA/Vault
// Wrapper is just to reference, it doesn't do transactions, so no bump is needed

impl Space for Wrapper {
  const INIT_SPACE: usize = 8 + 32 + 32 + 32 + 1;
  // total: 89 bytes
}

impl Wrapper {

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
        symbol_option: Option<String>,
    ) -> Result<()> {
        if let Some(symbol) = symbol_option { self.symbol = symbol; }

        Ok(())
    }


    pub fn get_pubkey(&mut self) -> Pubkey {

        Pubkey::find_program_address(
            &[SEED_WRAPPER_ACCOUNT, self.mint_original.as_ref()],
            &crate::ID,
        ).0
    }

    pub fn get_address(&mut self) -> String {

        Pubkey::find_program_address(
            &[SEED_WRAPPER_ACCOUNT, self.mint_original.as_ref()],
            &crate::ID,
        )
        .0
        .to_string()
    }

}