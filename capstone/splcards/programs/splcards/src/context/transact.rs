use anchor_lang::prelude::*;


#[derive(Accounts)]
pub struct Transact<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    pub system_program: Program<'info, System>,
}

impl<'info> Transact<'info> {
    pub fn process_transaction(&mut self, _bumps: &TransactBumps) -> Result<()> {
        Ok(())
    }
}

