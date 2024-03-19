use anchor_lang::prelude::*;

declare_id!("2FUFG2i7pQt3DyU8oA2reL91oDyYfXkh2KfNbWhsbTf2");

#[program]
pub mod splcards {
    use super::*;

    pub fn initialize(ctx: Context<Initialize>) -> Result<()> {
        Ok(())
    }
}

#[derive(Accounts)]
pub struct Initialize {}
