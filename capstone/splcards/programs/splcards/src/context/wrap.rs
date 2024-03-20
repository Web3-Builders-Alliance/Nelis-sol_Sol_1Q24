use anchor_lang::prelude::*;
use anchor_spl::{
    token::Token,
    associated_token::AssociatedToken,
    token_interface::{Mint, TokenAccount, TokenInterface},
};

use crate::state::Wrapper;
use crate::constants::*;

#[derive(Accounts)]
pub struct Wrap<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    #[account(mut,  
        associated_token::authority = payer,      
        associated_token::mint = mint
    )]
    pub payer_ata: InterfaceAccount<'info, TokenAccount>,
    // Mint of the original tokens
    pub mint: InterfaceAccount<'info, Mint>,
    #[account(
        seeds = [SEED_WRAPPER_ACCOUNT, mint.key().as_ref()],
        bump = wrapper.bump
    )]
    pub wrapper: Account<'info, Wrapper>,
    #[account(
        mut,
        seeds = [SEED_VAULT_ACCOUNT, wrapper.key().as_ref()],
        bump,
        token::authority = wrapper,
        token::mint = mint
      )]
    pub vault: InterfaceAccount<'info, TokenAccount>,
    pub associated_token_program: Program<'info, AssociatedToken>,
    pub token_program: Program<'info, Token>,
    pub system_program: Program<'info, System>,
}

impl<'info> Wrap<'info> {
    pub fn process_wrapping(&mut self, _bumps: &WrapBumps) -> Result<()> {


        // transfer original tokens to vault

        Ok(())
    }
}