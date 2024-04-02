use anchor_lang::accounts::signer;
pub use anchor_lang::{
    solana_program::{
        sysvar::rent::ID as RENT_ID,
        program::{invoke, invoke_signed}
    },
    prelude::*,
    system_program::{ create_account, CreateAccount, Transfer, transfer }
};

use solana_program::program_pack::Pack;

use anchor_spl::{
    associated_token::AssociatedToken,
    token_interface::{Mint, TokenAccount, Token2022, InitializeMint2},
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

pub use spl_token_metadata_interface::{
    state::{TokenMetadata, Field},
    instruction::{initialize as initialize_metadata_account, update_field as update_metadata_account},
};

pub use spl_tlv_account_resolution::{
    account::ExtraAccountMeta, seeds::Seed, state::ExtraAccountMetaList,
};

pub use spl_transfer_hook_interface::instruction::{ExecuteInstruction, TransferHookInstruction};

use crate::state::WrapperState;
use crate::constants::*;

#[derive(Accounts)]
pub struct WrapperInstructions<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    // (non-existing) mint of the wrapper tokens
    #[account(mut)]
    pub mint_wrapped: Signer<'info>,
    // (existing) mint of the original tokens
    #[account(mut)]
    pub mint_original: Box<InterfaceAccount<'info, Mint>>,
    #[account(
        init_if_needed,
        payer = payer,
        space = WrapperState::INIT_SPACE,
        seeds = [SEED_WRAPPER_ACCOUNT, mint_original.key().as_ref()],
        bump
    )]
    pub wrapper: Box<Account<'info, WrapperState>>,
    #[account(
        init_if_needed, 
        payer = payer,
        seeds = [SEED_VAULT_ACCOUNT, mint_original.key().as_ref()],
        bump,
        token::authority = wrapper,
        token::mint = mint_original
      )]
    pub vault: Box<InterfaceAccount<'info, TokenAccount>>,
    #[account(
        mut,
        seeds = [b"extra-account-metas", mint_wrapped.key().as_ref()],
        bump
    )]
    /// CHECK: this is ok.
    pub extra_account_meta_list: AccountInfo<'info>,
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


        let funding_amount: u64 = 1000000;
        let accts = Transfer {
            from: self.payer.to_account_info(),
            to: self.wrapper.to_account_info(),
        };

        let cpi_ctx = CpiContext::new(
            self.system_program.to_account_info(),
            accts
        );

        transfer(cpi_ctx, funding_amount)?;

        msg!("Wrapper balance: {}", self.wrapper.to_account_info().get_lamports());

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
                (size).try_into().unwrap(),
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
                Some(crate::ID),
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
        

        // Step 3: Initialize Mint & Metadata Account

        let mint_original = self.mint_original.key().clone();

        // let (pda, bump) = Pubkey::find_program_address(
        //     &[SEED_WRAPPER_ACCOUNT, mint_original.as_ref()],
        //     &crate::ID, // This is the public key of your program.
        // );


        let seeds: &[&[u8]; 3] = &[
            SEED_WRAPPER_ACCOUNT,
            mint_original.as_ref(),
            &[bumps.wrapper],
        ];
        let signer_seeds = [&seeds[..]];
        
                
        invoke_signed(
            &initialize_mint2(
                &self.token_program.key(),
                &self.mint_wrapped.key(),
                &self.wrapper.key(),
                Some(&self.wrapper.key()),
                self.mint_original.decimals,
            )?,
            &vec![
                self.wrapper.to_account_info(),
                self.mint_wrapped.to_account_info(),
            ],
            &signer_seeds
        )?;


        invoke_signed(
            &initialize_metadata_account(
                &self.token_program.key(),
                &self.mint_wrapped.key(),
                &self.wrapper.key(),
                &self.mint_wrapped.key(),
                &self.wrapper.key(),
                name.clone(),
                symbol,
                uri.clone(),
            ),
            &vec![
                self.mint_wrapped.to_account_info(),
                self.wrapper.to_account_info(),
            ],
            &signer_seeds
        )?;


        // index 0-3 are the accounts required for token transfer (source, mint, destination, owner)
        // index 4 is address of ExtraAccountMetaList account
        let account_metas = vec![
        // index 5, vesting_account
        ExtraAccountMeta::new_with_seeds(
            &[
                Seed::Literal { bytes: SEED_WALLET_POLICY_ACCOUNT.to_vec() },
                Seed::AccountKey { index: 3 },
            ],
            false, // is_signer
            true,  // is_writable
        )?,
        ExtraAccountMeta::new_with_seeds(
            &[
                Seed::Literal { bytes: SEED_TOKEN_POLICY_ACCOUNT.to_vec() },
                Seed::AccountKey { index: 3 },
                Seed::AccountKey { index: 1 }
            ],
            false, // is_signer
            true,  // is_writable
        )?,
        ];
    
        // calculate account size
        let account_size = ExtraAccountMetaList::size_of(account_metas.len())? as u64;
        // calculate minimum required lamports
        let lamports = Rent::get()?.minimum_balance(account_size as usize);
    
        let mint_wrapped = self.mint_wrapped.key();
        let signer_seeds: &[&[&[u8]]] = &[&[
            b"extra-account-metas",
            mint_wrapped.as_ref(),
            &[bumps.extra_account_meta_list],
        ]];


        // create ExtraAccountMetaList account
        create_account(
            CpiContext::new(
                self.system_program.to_account_info(),
                CreateAccount {
                    from: self.payer.to_account_info(),
                    to: self.extra_account_meta_list.to_account_info(),
                },
            )
            .with_signer(signer_seeds),
            lamports,
            account_size,
            &crate::ID,
        )?;
    
        // initialize ExtraAccountMetaList account with extra accounts
        ExtraAccountMetaList::init::<ExecuteInstruction>(
            &mut self.extra_account_meta_list.try_borrow_mut_data()?,
            &account_metas,
        )?;


        Ok(())

    }

    pub fn update_wrapper(&mut self, symbol_string: String) -> Result<()> {

        self.wrapper.update(
            symbol_string,
        )
    }
}