use anchor_lang::prelude::*;

mod instructions;
use instructions::*;
mod state;
mod constants;
pub mod error;

declare_id!("2FUFG2i7pQt3DyU8oA2reL91oDyYfXkh2KfNbWhsbTf2");

#[program]
pub mod splcards {
    use super::*;

    /* 1. UserPolicy account instructions */ 
    
    // Create a User Policy account in one go
    pub fn new_full_user_policy(ctx: Context<UserPolicyInstructions>, 
        signer1: Option<Pubkey>,
        required_signer1: bool,
        signer2: Option<Pubkey>,
        required_signer2: bool,
        allow_list: Vec<Pubkey>,
        block_list: Vec<Pubkey>,
        spending_window: Option<[i64;2]>
    ) -> Result<()> {
        ctx.accounts.new_full_user_policy(
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
    pub fn new_user_policy(ctx: Context<UserPolicyInstructions>) -> Result<()> {
        ctx.accounts.new_user_policy(&ctx.bumps)
    }

    pub fn add_signer1_to_user_policy(ctx: Context<UserPolicyInstructions>, signer1: Pubkey, required: bool) -> Result<()> {
        ctx.accounts.add_signer1_to_user_policy(signer1, required)
    }

    pub fn remove_signer1_from_user_policy(ctx: Context<UserPolicyInstructions>) -> Result<()> {
        ctx.accounts.remove_signer1_from_user_policy()
    }

    pub fn add_signer2_to_user_policy(ctx: Context<UserPolicyInstructions>, signer2: Pubkey, required: bool) -> Result<()> {
        ctx.accounts.add_signer2_to_user_policy(signer2, required)
    }

    pub fn remove_signer2_from_user_policy(ctx: Context<UserPolicyInstructions>) -> Result<()> {
        ctx.accounts.remove_signer2_from_user_policy()
    }

    pub fn add_allowed_publickeys_to_user_policy(ctx: Context<UserPolicyInstructions>, allowed_pubkey_list: Vec<Pubkey>) -> Result<()> {
        ctx.accounts.add_allowed_publickeys_to_user_policy(allowed_pubkey_list)
    }

    pub fn remove_allowed_publickeys_from_user_policy(ctx: Context<UserPolicyInstructions>, remove_pubkey_list: Vec<Pubkey>) -> Result<()> {
        ctx.accounts.remove_allowed_publickeys_to_user_policy(remove_pubkey_list)
    }

    pub fn add_blocked_publickeys_to_user_policy(ctx: Context<UserPolicyInstructions>, blocked_pubkey_list: Vec<Pubkey>) -> Result<()> {
        ctx.accounts.add_blocked_publickeys_to_user_policy(blocked_pubkey_list)
    }

    pub fn remove_blocked_publickeys_from_user_policy(ctx: Context<UserPolicyInstructions>, remove_pubkey_list: Vec<Pubkey>) -> Result<()> {
        ctx.accounts.remove_blocked_publickeys_to_user_policy(remove_pubkey_list)
    }

    pub fn add_spending_window_to_user_policy(ctx: Context<UserPolicyInstructions>, spending_window: [i64; 2]) -> Result<()> {
        ctx.accounts.add_spending_window_to_user_policy(spending_window)
    }

    pub fn remove_spending_window_from_user_policy(ctx: Context<UserPolicyInstructions>) -> Result<()> {
        ctx.accounts.remove_spending_window_from_user_policy()
    }

    pub fn delete_user_policy(ctx: Context<DeleteUserPolicy>) -> Result<()> {
        ctx.accounts.delete_user_policy()
    }



    /* 2. Token Policy account instructions */ 

    // Create a Token Policy account in one go
    pub fn new_full_token_policy(ctx: Context<TokenPolicyInstructions>, spend_limit_amount: Option<u64>) -> Result<()> {
        ctx.accounts.new_full_token_policy(spend_limit_amount, &ctx.bumps)
    }

    // Create the most basic User Policy account and add rules/policies later on
    pub fn new_token_policy(ctx: Context<TokenPolicyInstructions>) -> Result<()> {
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

    pub fn new_wrapper(ctx: Context<WrapperInstructions>, symbol: String) -> Result<()> {
        ctx.accounts.new_wrapper(symbol, &ctx.bumps)
    }






    pub fn transact(ctx: Context<Transact>) -> Result<()> {
        ctx.accounts.process_transaction(&ctx.bumps)
    }

    pub fn wrap(ctx: Context<Wrap>, amount: u64) -> Result<()> {
        ctx.accounts.process_wrapping(amount, &ctx.bumps)
    }

}

