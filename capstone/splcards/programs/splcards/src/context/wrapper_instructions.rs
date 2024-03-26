use anchor_lang::prelude::*;
use anchor_spl::{
    token::Token,
    associated_token::AssociatedToken,
    token_interface::{Mint, TokenAccount},
};

use crate::state::Wrapper;
use crate::constants::*;

#[derive(Accounts)]
pub struct WrapperInstructions<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    // Mint of the original tokens
    pub mint_original: InterfaceAccount<'info, Mint>,
    // Mint of the original tokens
    pub mint_wrapped: InterfaceAccount<'info, Mint>,
    #[account(
        init,
        payer = payer,
        space = Wrapper::INIT_SPACE,
        seeds = [SEED_WRAPPER_ACCOUNT, mint_original.key().as_ref()],
        bump
    )]
    pub wrapper: Account<'info, Wrapper>,
    #[account(
        init_if_needed, 
        payer = payer,
        seeds = [SEED_VAULT_ACCOUNT, mint_original.key().as_ref()],
        bump,
        token::authority = wrapper,
        token::mint = mint_original
      )]
    pub vault: InterfaceAccount<'info, TokenAccount>,
    pub associated_token_program: Program<'info, AssociatedToken>,
    pub token_program: Program<'info, Token>,
    pub system_program: Program<'info, System>,
}


impl<'info> WrapperInstructions<'info> {
    pub fn new_wrapper(&mut self, symbol_string: String, bumps: &WrapperInstructionsBumps) -> Result<()> {
        
        self.wrapper.new(
            symbol_string,
            self.mint_original.key(),
            self.mint_wrapped.key(),
            self.vault.key(),
            bumps.wrapper,
        )?;

        Ok(())
    }
}