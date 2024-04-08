use anchor_lang::prelude::*;
use anchor_spl::{
    associated_token::AssociatedToken,
    token_2022::{mint_to, MintTo, Token2022}, 
    token_interface::{Mint, TokenAccount, TransferChecked, transfer_checked, TokenInterface}
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
        associated_token::mint = mint_original,
    )]
    pub payer_ata_original: InterfaceAccount<'info, TokenAccount>,
    #[account(
        init_if_needed,  
        payer = payer,
        associated_token::authority = payer,
        associated_token::mint = mint_wrapped,
        associated_token::token_program = token_extensions_program
    )]
    pub payer_ata_wrapped: InterfaceAccount<'info, TokenAccount>,
    // Mint of the wrapped tokens
    #[account(address = wrapper.mint_original)]
    pub mint_original: InterfaceAccount<'info, Mint>,
    // Mint of the wrapped tokens
    #[account(mut, address = wrapper.mint_wrapped)]
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
    pub token_program: Interface<'info, TokenInterface>,
    pub token_extensions_program: Program<'info, Token2022>,
    pub system_program: Program<'info, System>,
}

impl<'info> Wrap<'info> {

    // Wrap function allows us to take in mint_original tokens, and mint new mint_wrapped tokens to the user
    pub fn wrap(&mut self, amount: u64, _bumps: &WrapBumps) -> Result<()> {

        // transfer original tokens to the vault 
        let cpi_accounts = TransferChecked {
            from: self.payer_ata_original.to_account_info(),
            mint: self.mint_original.to_account_info(),
            to: self.vault.to_account_info(),
            authority: self.payer.to_account_info(),
        };
        let ctx = CpiContext::new(
            self.token_program.to_account_info(), 
            cpi_accounts
        );

        transfer_checked(ctx, amount, self.mint_original.decimals)?;


        // prepare signer seeds for wrapper (authority of mint_wrapped)
        let signer_seeds: [&[&[u8]];1] = [&[
            SEED_WRAPPER_ACCOUNT, 
            self.wrapper.mint_original.as_ref(),
            &[self.wrapper.bump],
        ]];
        
        // mint tokens to user
        let cpi_accounts_mint = MintTo {
            mint: self.mint_wrapped.to_account_info(),
            to: self.payer_ata_wrapped.to_account_info(),
            authority: self.wrapper.to_account_info(),
        };

        let ctx_mint = CpiContext::new_with_signer(
            self.token_extensions_program.to_account_info(), 
            cpi_accounts_mint, 
            &signer_seeds
        );

        mint_to(ctx_mint, amount)?;


        Ok(())
    }
}