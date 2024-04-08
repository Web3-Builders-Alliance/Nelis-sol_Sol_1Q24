pub use anchor_lang::{
    solana_program::{
        sysvar::rent::ID as RENT_ID,
        program::{invoke, invoke_signed}
    },
    prelude::*,
    system_program::{ create_account, CreateAccount, Transfer, transfer }
};

use anchor_spl::{
    associated_token::AssociatedToken,
    token_interface::{Mint, TokenAccount, TokenInterface, Token2022},
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
pub struct CreateWrapper<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    #[account(mut)]
    // (non-existing) Mint of the wrapped tokens
    pub mint_wrapped: Signer<'info>,
    #[account(mut)]
    // (existing) Mint of the original tokens
    pub mint_original: Box<InterfaceAccount<'info, Mint>>,
    #[account(
        init,
        payer = payer,
        space = WrapperState::INIT_SPACE,
        // Wrapper is unique for a given mint
        seeds = [SEED_WRAPPER_ACCOUNT, mint_original.key().as_ref()],
        bump
    )]
    pub wrapper: Box<Account<'info, WrapperState>>,
    #[account(
        init, 
        payer = payer,
        seeds = [SEED_VAULT_ACCOUNT, mint_original.key().as_ref()],
        bump,
        // vault is owned by the wrapper PDA
        token::authority = wrapper,
        // vault stores the original tokens
        token::mint = mint_original
      )]
    pub vault: Box<InterfaceAccount<'info, TokenAccount>>,
    #[account(
        mut,
        seeds = [b"extra-account-metas", mint_wrapped.key().as_ref()],
        bump
    )]
    /// CHECK: ExtraAccountMetaList Account
    pub extra_account_meta_list: AccountInfo<'info>,
    #[account(address = RENT_ID)]
    /// CHECK: This is OK since we are hardcode the rent sysvar in the constraint.
    pub rent: UncheckedAccount<'info>,
    pub associated_token_program: Program<'info, AssociatedToken>,
    pub token_program: Interface<'info, TokenInterface>,
    pub token_extensions_program: Program<'info, Token2022>,
    pub system_program: Program<'info, System>,
}


impl<'info> CreateWrapper<'info> {

    // Create wrapper PDA with default settings
    pub fn new_wrapper(
        &mut self, 
        name: String, 
        symbol: String, 
        uri: String, 
        bumps: &CreateWrapperBumps
    ) -> Result<()> {
        
        // use wrapper method to create new
        self.wrapper.new(
            symbol.clone(),
            self.mint_original.key(),
            self.mint_wrapped.key(),
            self.vault.key(),
            bumps.wrapper,
        )?;


        /*    Step 1: Initialize Account    */

        // calculate needed size/space for adding token extensions to the mint of the wrapped tokens
        let size = ExtensionType::try_calculate_account_len::<spl_token_2022::state::Mint>(
            &[
                // transfer hook extension
                ExtensionType::TransferHook,
                // custom metadata account
                ExtensionType::MetadataPointer,
            ],
        ).unwrap();

        // define the mint's metadata based on the input sent by from the client-side
        let metadata = TokenMetadata {
            // The wrapper PDA is the update authority
            update_authority: spl_pod::optional_keys::OptionalNonZeroPubkey::try_from(Some(self.wrapper.key())).unwrap(),
            mint: self.mint_wrapped.key(),
            name: name.clone(),
            symbol: symbol.clone(),
            uri: uri.clone(),
            // We do not add additional metadata for now
            additional_metadata: vec![]
        };


        // calculate extra needed space to accomodate the metadata
        let extension_extra_space = metadata.tlv_size_of().unwrap();

        // get rent account so we can calculate the required rent dynamically
        let rent = &Rent::from_account_info(&self.rent.to_account_info())?;

        // retrieve the amount of rent needed to cover the mint account (incl. metadata and token extensions)
        let lamports = rent.minimum_balance(size + extension_extra_space);


        msg!("44");

        // create a new account for mint_wrapped, which we will initialize as mint later on
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


        msg!("55");

        /*    Step 2: Initialize Extension needed    */

        // Initialize transfer hook
        invoke(
            &intialize_transfer_hook(
                &self.token_extensions_program.key(),
                &self.mint_wrapped.key(),
                // The wrapper PDA is the authority for the transfer hook
                Some(self.wrapper.key()),
                // The transfer hook calls the current program on every transfer
                Some(crate::ID),
            )?,
            &vec![
                self.mint_wrapped.to_account_info(),
            ],
        )?;

        msg!("66");

        // Initialize metadata pointer
        invoke(
            &initialize_metadata_pointer(
                &self.token_extensions_program.key(),
                &self.mint_wrapped.key(),
                // The wrapper PDA is the authority for the metadata pointer
                Some(self.wrapper.key()),
                // The metadata is stored at the mint_wrapped address
                Some(self.mint_wrapped.key()),
            )?,
            &vec![
                self.mint_wrapped.to_account_info(),
            ],
        )?;
        

        /*    Step 3: Initialize Mint & Metadata Account    */

        // clone the mint_original account for use in deriving the wrapper account
        let mint_original = self.mint_original.key().clone();

        // create signer seeds for the wrapper PDA
        let seeds: &[&[u8]; 3] = &[
            SEED_WRAPPER_ACCOUNT,
            mint_original.as_ref(),
            &[bumps.wrapper],
        ];
        let signer_seeds = [&seeds[..]];
        
        msg!("AA");
        // initialize mint_wrapped and sign with the wrapper PDA
        invoke_signed(
            &initialize_mint2(
                &self.token_extensions_program.key(),
                &self.mint_wrapped.key(),
                // the wrapper PDA is the mint authority
                &self.wrapper.key(),
                // the wrapper PDA is the freeze authority
                Some(&self.wrapper.key()),
                // the mint_wrapped has the same amount of decimals as the original
                self.mint_original.decimals,
            )?,
            &vec![
                self.wrapper.to_account_info(),
                self.mint_wrapped.to_account_info(),
            ],
            &signer_seeds
        )?;

        msg!("BB");


        // initialize mint_wrapped and sign with the wrapper PDA
        invoke_signed(
            &initialize_metadata_account(
                &self.token_extensions_program.key(),
                // metadata is stored at the mint_wrapped address
                &self.mint_wrapped.key(),
                // the wrapper PDA is the update authority for the metadata account
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

        msg!("CC");

        /*    Step 4: Initialize ExtraAccountMetaList account    */

        let account_metas = vec![
            // seeds for wallet_policy
            ExtraAccountMeta::new_with_seeds(
                &[
                    Seed::Literal { bytes: SEED_WALLET_POLICY_ACCOUNT.to_vec() },
                    // the owner of the sent token is at index 3
                    Seed::AccountKey { index: 3 },
                ],
                // wallet_policy is not a signer in the transaction
                false,
                // wallet_policy is mutable in case we want to make changes to the PDA
                true,
            )?,
            // seeds for token_policy
            ExtraAccountMeta::new_with_seeds(
                &[
                    Seed::Literal { bytes: SEED_TOKEN_POLICY_ACCOUNT.to_vec() },
                    // the owner of the sent token is at index 3
                    Seed::AccountKey { index: 3 },
                    // the mint of the sent token is at index 1
                    Seed::AccountKey { index: 1 }
                ],
                // token_policy is not a signer in the transaction
                false,
                // token_policy is mutable mutable because we update the spending per 24 hours
                true,
            )?,
            ExtraAccountMeta::new_with_pubkey(&self.system_program.key(), false, false)?,
        ];
    
        // calculate account size
        let account_size = ExtraAccountMetaList::size_of(account_metas.len())? as u64;
        // calculate minimum required lamports to cover this account size
        let lamports = Rent::get()?.minimum_balance(account_size as usize);
    
        // clone the mint_original account for use in deriving the wrapper account
        let mint_wrapped = self.mint_wrapped.key().clone();

        // create signer seeds for the wrapper PDA
        let signer_seeds: &[&[&[u8]]] = &[&[
            b"extra-account-metas",
            mint_wrapped.as_ref(),
            &[bumps.extra_account_meta_list],
        ]];


        // create a new account for extra_account_meta_list, which we will initialize directly after
        create_account(
            CpiContext::new(
                self.system_program.to_account_info(),
                CreateAccount {
                    // signer of the transaction pays for the account creation
                    from: self.payer.to_account_info(),
                    to: self.extra_account_meta_list.to_account_info(),
                },
            )
            .with_signer(signer_seeds),
            lamports,
            account_size,
            &crate::ID,
        )?;
    
        // initialize ExtraAccountMetaList account with the extra accounts
        ExtraAccountMetaList::init::<ExecuteInstruction>(
            &mut self.extra_account_meta_list.try_borrow_mut_data()?,
            &account_metas,
        )?;


        Ok(())

    }
}