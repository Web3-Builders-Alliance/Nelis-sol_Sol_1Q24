use anchor_lang::prelude::*;
use anchor_spl::{
    associated_token::AssociatedToken, 
    token::{Token}, 
    token_2022::{mint_to, MintTo, Token2022}, 
    token_interface::{Mint, TokenAccount, TransferChecked, transfer_checked}
};
pub use spl_transfer_hook_interface::instruction::{ExecuteInstruction, TransferHookInstruction};

use crate::state::WrapperState;
use crate::constants::*;

#[derive(Accounts)]
pub struct Wrap<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    #[account(
        init_if_needed,  
        payer = payer, 
        associated_token::authority = payer,      
        associated_token::mint = mint_original
    )]
    pub payer_ata_original: InterfaceAccount<'info, TokenAccount>,
    #[account(
        init_if_needed,  
        payer = payer,
        associated_token::authority = payer,      
        associated_token::mint = mint_wrapped
    )]
    pub payer_ata_wrapped: InterfaceAccount<'info, TokenAccount>,
    // Mint of the wrapped tokens
    pub mint_original: InterfaceAccount<'info, Mint>,
    // Mint of the wrapped tokens
    pub mint_wrapped: InterfaceAccount<'info, Mint>,
    #[account(
        seeds = [SEED_WRAPPER_ACCOUNT, wrapper.mint_original.key().as_ref()],
        bump = wrapper.bump
    )]
    pub wrapper: Account<'info, WrapperState>,
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
    pub token_extensions_program: Program<'info, Token2022>,
    pub system_program: Program<'info, System>,
}

impl<'info> Wrap<'info> {
    pub fn wrap(&mut self, amount: u64, bumps: &WrapBumps) -> Result<()> {


        // let cpi_accounts = TransferChecked {
        //     from: self.payer_ata_original.to_account_info(),
        //     mint: self.mint_original.to_account_info(),
        //     to: self.vault.to_account_info(),
        //     authority: self.payer.to_account_info(),
        // };
        // let ctx = CpiContext::new(
        //     self.token_program.to_account_info(), 
        //     cpi_accounts
        // );

        // transfer_checked(ctx, amount, self.mint_original.decimals)?;


        // let signer_seeds: [&[&[u8]];1] = [&[
        //     SEED_WRAPPER_ACCOUNT, 
        //     self.wrapper.mint_original.as_ref(),
        //     &[self.wrapper.bump],
        // ]];
        
        // let cpi_accounts_mint = MintTo {
        //     mint: self.mint_wrapped.to_account_info(),
        //     to: self.payer_ata_wrapped.to_account_info(),
        //     authority: self.wrapper.to_account_info(),
        // };

        // let ctx_mint = CpiContext::new_with_signer(
        //     self.token_extensions_program.to_account_info(), 
        //     cpi_accounts_mint, 
        //     &signer_seeds
        // );

        // mint_to(ctx_mint, amount)?;

        Ok(())
    }
}