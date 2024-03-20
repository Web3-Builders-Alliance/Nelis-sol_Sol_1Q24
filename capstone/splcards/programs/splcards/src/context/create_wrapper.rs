use anchor_lang::prelude::*;
use anchor_spl::{
    token::Token,
    associated_token::AssociatedToken,
    token_interface::{Mint, TokenAccount, TokenInterface},
};

use crate::state::Wrapper;
use crate::constants::*;

#[derive(Accounts)]
pub struct CreateWrapper<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    // Mint of the original tokens
    pub mint: InterfaceAccount<'info, Mint>,
    #[account(
        init,
        payer = payer,
        space = Wrapper::INIT_SPACE,
        seeds = [SEED_WRAPPER_ACCOUNT, mint.key().as_ref()],
        bump
    )]
    pub wrapper: Account<'info, Wrapper>,
    #[account(
        init, 
        payer = payer,
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


impl<'info> CreateWrapper<'info> {
    pub fn process_create_wrapper(&mut self, _bumps: &CreateWrapperBumps) -> Result<()> {

        Ok(())
    }
}