use anchor_lang::prelude::*;
use anchor_spl::{
    associated_token::AssociatedToken, 
    token::{transfer, Token, Transfer}, 
    token_2022::{mint_to, MintTo, Token2022}, 
    token_interface::{Mint, TokenAccount}
};
pub use spl_transfer_hook_interface::instruction::{ExecuteInstruction, TransferHookInstruction};

use crate::state::WrapperState;
use crate::constants::*;

#[derive(Accounts)]
pub struct Wrap<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    #[account(mut,  
        associated_token::authority = payer,      
        associated_token::mint = mint_original
    )]
    pub payer_ata_original: InterfaceAccount<'info, TokenAccount>,
    #[account(init_if_needed,  
        payer = payer,
        associated_token::authority = payer,      
        associated_token::mint = mint_wrapped
    )]
    pub payer_ata_wrapped: InterfaceAccount<'info, TokenAccount>,
    // Mint of the original tokens
    pub mint_original: InterfaceAccount<'info, Mint>,
    // Mint of the wrapped tokens
    pub mint_wrapped: InterfaceAccount<'info, Mint>,
    #[account(
        seeds = [SEED_WRAPPER_ACCOUNT, mint_original.key().as_ref()],
        bump = wrapper.bump
    )]
    pub wrapper: Account<'info, WrapperState>,
    #[account(
        mut,
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

        // transfer original tokens to vault
        let cpi_accounts = Transfer {
            from: self.payer_ata_original.to_account_info(),
            to: self.vault.to_account_info(),
            authority: self.payer.to_account_info()
          };
      
        let ctx = CpiContext::new(self.token_program.to_account_info(), cpi_accounts);
      
        transfer(ctx, amount)?;



        // mint tokens to user
        let mint_original = self.mint_original.to_account_info().key().clone();

        let signer_seeds: [&[&[u8]];1] = [&[
            SEED_VAULT_ACCOUNT, 
            mint_original.as_ref(),
            &[bumps.vault],
        ]];
        
        let cpi_accounts_mint = MintTo {
            mint: self.mint_wrapped.to_account_info(),
            to: self.payer_ata_wrapped.to_account_info(),
            authority: self.mint_wrapped.to_account_info(),
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