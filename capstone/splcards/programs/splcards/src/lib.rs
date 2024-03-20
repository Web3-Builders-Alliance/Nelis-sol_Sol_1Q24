use anchor_lang::prelude::*;

mod state;
mod context;
use context::*;
mod constants;

declare_id!("2FUFG2i7pQt3DyU8oA2reL91oDyYfXkh2KfNbWhsbTf2");

#[program]
pub mod splcards {
    use super::*;

    pub fn transact(ctx: Context<Transact>) -> Result<()> {
        ctx.accounts.process_transaction(&ctx.bumps)
    }

    pub fn wrap(ctx: Context<Wrap>) -> Result<()> {
        ctx.accounts.process_wrapping(&ctx.bumps)
    }

}

