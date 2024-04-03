use anchor_lang::prelude::*;
use anchor_spl::{
    associated_token::AssociatedToken, 
    token::{transfer, Token, Transfer}, 
    token_2022::{Token2022, burn, Burn}, 
    token_interface::{Mint, TokenAccount}
};

use crate::state::WrapperState;
use crate::constants::*;

#[derive(Accounts)]
pub struct Unwrap<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    #[account(
        mut,  
        associated_token::authority = payer,      
        associated_token::mint = mint_original
    )]
    pub payer_ata_original: InterfaceAccount<'info, TokenAccount>,
    #[account(
        mut,
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

impl<'info> Unwrap<'info> {

    pub fn unwrap(&mut self, amount: u64, _bumps: &UnwrapBumps) -> Result<()> {

        burn(
            CpiContext::new(
                self.token_program.to_account_info(),
                Burn {
                    mint: self.mint_wrapped.to_account_info(),
                    from: self.payer_ata_wrapped.to_account_info(),
                    authority: self.payer.to_account_info(),
                },
            ),
            amount
        )?;


        let mint_original = self.mint_original.to_account_info().key().clone();

        let signer_seeds: [&[&[u8]];1] = [&[
            SEED_WRAPPER_ACCOUNT, 
            mint_original.as_ref(),
            &[self.wrapper.bump],
        ]];
    
        let cpi_accounts = Transfer {
          from: self.vault.to_account_info(),
          to: self.payer_ata_original.to_account_info(),
          authority: self.wrapper.to_account_info()
        };
    
        let ctx = CpiContext::new_with_signer(
            self.token_program.to_account_info(), 
            cpi_accounts, 
            &signer_seeds
        );
    
        transfer(ctx, amount)

    }
}