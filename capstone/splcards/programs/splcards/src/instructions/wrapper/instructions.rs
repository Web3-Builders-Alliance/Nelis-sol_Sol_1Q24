pub use anchor_lang::prelude::*;

use anchor_spl::{
    associated_token::AssociatedToken,
    token_interface::{Mint, Token2022},
};

pub use spl_token_2022::{
    extension::ExtensionType,
    instruction::{initialize_mint_close_authority, initialize_permanent_delegate, initialize_mint, initialize_mint2},
    extension::{
        transfer_hook::instruction::initialize as intialize_transfer_hook,
        metadata_pointer::instruction::initialize as initialize_metadata_pointer,
    },
    state::Mint as SplMint,
};



use crate::state::WrapperState;
use crate::constants::*;

#[derive(Accounts)]
pub struct WrapperInstructions<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    #[account(mut)]
    // (non-existing) Mint of the wrapped tokens
    pub mint_wrapped: Signer<'info>,
    #[account(mut)]
    // (existing) Mint of the original tokens
    pub mint_original: Box<InterfaceAccount<'info, Mint>>,
    #[account(
        mut,
        // Wrapper is unique for a given mint
        seeds = [SEED_WRAPPER_ACCOUNT, mint_original.key().as_ref()],
        bump
    )]
    pub wrapper: Box<Account<'info, WrapperState>>,
    pub associated_token_program: Program<'info, AssociatedToken>,
    pub token_program: Program<'info, Token2022>,
    pub system_program: Program<'info, System>,
}


impl<'info> WrapperInstructions<'info> {

    pub fn update_wrapper(
        &mut self, 
        symbol_string: String
    ) -> Result<()> {

        // use wrapper method to update the symbol of the wrapper and return result
        self.wrapper.update(
            symbol_string,
        )
    }
}