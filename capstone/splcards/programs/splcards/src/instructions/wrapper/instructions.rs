pub use anchor_lang::{
    solana_program::{
        sysvar::rent::ID as RENT_ID,
        program::{invoke, invoke_signed}
    },
    prelude::*
};

use anchor_spl::{
    associated_token::AssociatedToken,
    token_interface::{Mint, TokenAccount, Token2022},
};

pub use spl_token_2022::{
    extension::ExtensionType,
    instruction::{initialize_mint_close_authority, initialize_permanent_delegate, initialize_mint, initialize_mint2},
    extension::{
        transfer_hook::instruction::initialize as intialize_transfer_hook,
        metadata_pointer::instruction::initialize as initialize_metadata_pointer,
    },
};

pub use spl_token_metadata_interface::{
    state::{TokenMetadata, Field},
    instruction::{initialize as initialize_metadata_account, update_field as update_metadata_account},
};

use crate::state::WrapperState;
use crate::constants::*;

#[derive(Accounts)]
pub struct WrapperInstructions<'info> {
    #[account(mut)]
    /// CHECK
    pub payer: Signer<'info>,
    // (non-existing) mint of the wrapper tokens
    #[account(mut)]
    /// CHECK
    pub mint_wrapped: Signer<'info>,
    // (existing) mint of the original tokens
    #[account(mut)]
    pub mint_original: InterfaceAccount<'info, Mint>,
    #[account(
        init_if_needed,
        payer = payer,
        space = WrapperState::INIT_SPACE,
        seeds = [SEED_WRAPPER_ACCOUNT, mint_original.key().as_ref()],
        bump
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
    /// CHECK: this is fine since we are hard coding the rent sysvar.
    pub rent: UncheckedAccount<'info>,
    pub associated_token_program: Program<'info, AssociatedToken>,
    pub token_program: Program<'info, Token2022>,
    pub system_program: Program<'info, System>,
}


impl<'info> WrapperInstructions<'info> {
    pub fn new_wrapper(&mut self, name: String, symbol: String, uri: String, bumps: &WrapperInstructionsBumps) -> Result<()> {
        
        self.wrapper.new(
            symbol.clone(),
            self.mint_original.key(),
            self.mint_wrapped.key(),
            self.vault.key(),
            bumps.wrapper,
        )?;


        // Step 1: Initialize Account
        let size = ExtensionType::try_calculate_account_len::<spl_token_2022::state::Mint>(
            &[
                ExtensionType::TransferHook,
                ExtensionType::MetadataPointer,
            ],
        ).unwrap();

        let metadata = TokenMetadata {
            update_authority: spl_pod::optional_keys::OptionalNonZeroPubkey::try_from(Some(self.wrapper.key())).unwrap(),
            mint: self.mint_wrapped.key(),
            name: name.clone(),
            symbol: symbol.clone(),
            uri: uri.clone(),
            additional_metadata: vec![]
        };

        let extension_extra_space = metadata.tlv_size_of().unwrap();
        let rent = &Rent::from_account_info(&self.rent.to_account_info())?;
        let lamports = rent.minimum_balance(size + extension_extra_space);

        invoke(
            &solana_program::system_instruction::create_account(
                &self.payer.key(),
                &self.mint_wrapped.key(),
                lamports,
                size.try_into().unwrap(),
                &spl_token_2022::id(),
            ),
            &vec![
                self.payer.to_account_info(),
                self.mint_wrapped.to_account_info(),
            ],
        )?;


        // 2.2: Transfer Hook,
        invoke(
            &intialize_transfer_hook(
                &self.token_program.key(),
                &self.mint_wrapped.key(),
                Some(self.wrapper.key()),
                None,  // TO-DO: Add Transfer Hook
            )?,
            &vec![
                self.mint_wrapped.to_account_info(),
            ],
        )?;

        // 2.4: Metadata Pointer
        invoke(
            &initialize_metadata_pointer(
                &self.token_program.key(),
                &self.mint_wrapped.key(),
                Some(self.wrapper.key()),
                Some(self.mint_wrapped.key()),
            )?,
            &vec![
                self.mint_wrapped.to_account_info(),
            ],
        )?;


        // // sender, mint, receiver accounts are always present in transfer hook
        // // amount is in the instructions

        // // create a look up table to pass the user policy account
        // // medium article
        

        // Step 3: Initialize Mint & Metadata Account

        let mint_original = self.mint_original.to_account_info().key().clone();

        let signer_seeds: [&[&[u8]];1] = [&[
            SEED_WRAPPER_ACCOUNT, 
            mint_original.as_ref(),
            &[bumps.wrapper],
        ]];

        invoke_signed(
            &initialize_mint2(
                &self.token_program.key(),
                &self.mint_wrapped.key(),
                &self.wrapper.key(),
                Some(&self.wrapper.key()),
                self.mint_original.decimals,
            )?,
            &vec![
                self.mint_wrapped.to_account_info(),
                self.wrapper.to_account_info(),
                self.payer.to_account_info(),
            ],
            &signer_seeds
        )?;


        // invoke_signed(
        //     &initialize_metadata_account(
        //         &self.token_program.key(),
        //         &self.mint_wrapped.key(),
        //         &self.wrapper.key(),
        //         &self.mint_wrapped.key(),
        //         &self.payer.key(),
        //         name.clone(),
        //         symbol,
        //         uri.clone(),
        //     ),
        //     &vec![
        //         self.mint_wrapped.to_account_info(),
        //         self.wrapper.to_account_info(),
        //         self.payer.to_account_info(),
        //     ],
        //     &signer_seeds
        // )?;


        Ok(())


    }

    pub fn update_wrapper(&mut self, symbol_string: String) -> Result<()> {

        self.wrapper.update(
            symbol_string,
        )
    }
}