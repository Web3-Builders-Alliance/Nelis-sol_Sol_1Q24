use anchor_lang::prelude::*;

mod instructions;
use instructions::*;
mod state;
mod constants;
pub mod error;

declare_id!("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew");

#[program]
pub mod splcards {
    use super::*;

    /* 1. WalletPolicy account instructions */ 

    pub fn new_full_wallet_policy(ctx: Context<CreateWalletPolicy>, 
        signer1: Option<Pubkey>,
        required_signer1: bool,
        signer2: Option<Pubkey>,
        required_signer2: bool,
        allow_list: Vec<Pubkey>,
        block_list: Vec<Pubkey>,
        spending_window: Option<[i64;2]>
    ) -> Result<()> {
        ctx.accounts.new_full_wallet_policy(
            signer1,
            required_signer1,
            signer2,
            required_signer2,
            allow_list,
            block_list,
            spending_window,
            &ctx.bumps)
    }


    // Create the most basic User Policy account and add rules/policies later on
    pub fn new_wallet_policy(ctx: Context<CreateWalletPolicy>) -> Result<()> {
        ctx.accounts.new_wallet_policy(&ctx.bumps)
    }

    pub fn add_signer1_to_wallet_policy(ctx: Context<WalletPolicyInstructions>, signer1: Pubkey, required: bool) -> Result<()> {
        ctx.accounts.add_signer1_to_wallet_policy(signer1, required)
    }

    pub fn remove_signer1_from_wallet_policy(ctx: Context<WalletPolicyInstructions>) -> Result<()> {
        ctx.accounts.remove_signer1_from_wallet_policy()
    }

    pub fn add_signer2_to_wallet_policy(ctx: Context<WalletPolicyInstructions>, signer2: Pubkey, required: bool) -> Result<()> {
        ctx.accounts.add_signer2_to_wallet_policy(signer2, required)
    }

    pub fn remove_signer2_from_wallet_policy(ctx: Context<WalletPolicyInstructions>) -> Result<()> {
        ctx.accounts.remove_signer2_from_wallet_policy()
    }

    pub fn add_allowed_publickeys_to_wallet_policy(ctx: Context<WalletPolicyInstructions>, allowed_pubkey_list: Vec<Pubkey>) -> Result<()> {
        ctx.accounts.add_allowed_publickeys_to_wallet_policy(allowed_pubkey_list)
    }

    pub fn remove_allowed_publickeys_from_wallet_policy(ctx: Context<WalletPolicyInstructions>, remove_pubkey_list: Vec<Pubkey>) -> Result<()> {
        ctx.accounts.remove_allowed_publickeys_to_wallet_policy(remove_pubkey_list)
    }

    pub fn add_blocked_publickeys_to_wallet_policy(ctx: Context<WalletPolicyInstructions>, blocked_pubkey_list: Vec<Pubkey>) -> Result<()> {
        ctx.accounts.add_blocked_publickeys_to_wallet_policy(blocked_pubkey_list)
    }

    pub fn remove_blocked_publickeys_from_wallet_policy(ctx: Context<WalletPolicyInstructions>, remove_pubkey_list: Vec<Pubkey>) -> Result<()> {
        ctx.accounts.remove_blocked_publickeys_to_wallet_policy(remove_pubkey_list)
    }

    pub fn add_spending_window_to_wallet_policy(ctx: Context<WalletPolicyInstructions>, spending_window: [i64; 2]) -> Result<()> {
        ctx.accounts.add_spending_window_to_wallet_policy(spending_window)
    }

    pub fn remove_spending_window_from_wallet_policy(ctx: Context<WalletPolicyInstructions>) -> Result<()> {
        ctx.accounts.remove_spending_window_from_wallet_policy()
    }

    pub fn delete_wallet_policy(ctx: Context<DeleteWalletPolicy>) -> Result<()> {
        ctx.accounts.delete_wallet_policy()
    }



    /* 2. Token Policy account instructions */ 

    pub fn new_full_token_policy(
        ctx: Context<CreateTokenPolicy>, 
        spend_limit_amount: Option<u64>
    ) -> Result<()> {
        ctx.accounts.new_full_token_policy(spend_limit_amount, &ctx.bumps)
    }

    pub fn new_token_policy(ctx: Context<CreateTokenPolicy>) -> Result<()> {
        ctx.accounts.new_token_policy(&ctx.bumps)
    }

    pub fn add_spend_limit_to_token_policy(ctx: Context<TokenPolicyInstructions>, amount: u64) -> Result<()> {
        ctx.accounts.add_spend_limit_to_token_policy(amount)
    }

    pub fn remove_spend_limit_from_token_policy(ctx: Context<TokenPolicyInstructions>) -> Result<()> {
        ctx.accounts.remove_spend_limit_from_token_policy()
    }

    pub fn delete_token_policy(ctx: Context<DeleteTokenPolicy>) -> Result<()> {
        ctx.accounts.delete_token_policy()
    }



    /* 3. Wrapper account instructions */ 

    pub fn new_wrapper(ctx: Context<CreateWrapper>, name: String, symbol: String, uri: String) -> Result<()> {
        ctx.accounts.new_wrapper(name, symbol, uri, &ctx.bumps)
    }

    pub fn update_wrapper(ctx: Context<WrapperInstructions>, symbol: String) -> Result<()> {
        ctx.accounts.update_wrapper(symbol)
    }

    pub fn wrapper_close(ctx: Context<WrapperClose>) -> Result<()> {
        ctx.accounts.wrapper_close()
    }


    /* 4. Wrapping instructions */ 

    pub fn wrap(ctx: Context<Wrap>, amount: u64) -> Result<()> {
        ctx.accounts.wrap(amount, &ctx.bumps)
    }

    pub fn unwrap(ctx: Context<Unwrap>, amount: u64) -> Result<()> {
        ctx.accounts.unwrap(amount, &ctx.bumps)
    }


    pub fn transfer_hook(ctx: Context<TransferHook>, amount: u64) -> Result<()> {
        ctx.accounts.transfer_hook(amount)
    }


    // fallback instruction handler as workaround to anchor instruction discriminator check
    pub fn fallback<'info>(
        program_id: &Pubkey,
        accounts: &'info [AccountInfo<'info>],
        data: &[u8],
    ) -> Result<()> {
        let instruction = TransferHookInstruction::unpack(data)?;

        // match instruction discriminator to transfer hook interface execute instruction
        // token2022 program CPIs this instruction on token transfer
        match instruction {
            TransferHookInstruction::Execute { amount } => {
                let amount_bytes = amount.to_le_bytes();

                // invoke custom transfer hook instruction on our program
                __private::__global::transfer_hook(program_id, accounts, &amount_bytes)
            }
            _ => return Err(ProgramError::InvalidInstructionData.into()),
        }
    }



}

