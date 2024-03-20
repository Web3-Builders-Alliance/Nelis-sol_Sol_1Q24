use anchor_lang::prelude::*;


#[derive(Accounts)]
pub struct Wrap<'info> {
    #[account(mut)]
    pub payer: Signer<'info>,
    pub system_program: Program<'info, System>,
}

impl<'info> Wrap<'info> {
    pub fn process_wrapping(&mut self, _bumps: &WrapBumps) -> Result<()> {
        Ok(())
    }
}