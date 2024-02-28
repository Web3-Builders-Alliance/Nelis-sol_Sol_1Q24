use anchor_lang::prelude::*;
use anchor_lang::system_program::{Transfer, transfer};

declare_id!("DwLmZCpPDvnSSYRojAPNXnWBcpbp5kgPpVqqmdDMo9Zk");

#[program]
pub mod vault_2024 {
    use super::*;

    pub fn deposit(ctx: Context<Deposit>, seed: u64, lamports: u64) -> Result<()> {
        ctx.accounts.vault.set_inner(Vault {
            maker: ctx.accounts.maker.key(),
            taker: ctx.accounts.taker.key(),
            seed,
            bump: ctx.bumps.vault,
            created_at: Clock::get()?.unix_timestamp,            
        });
        let accts = Transfer {
            from: ctx.accounts.maker.to_account_info(),
            to: ctx.accounts.vault.to_account_info(),
        };

        let cpi_ctx = CpiContext::new(
            ctx.accounts.system_program.to_account_info(),
            accts
        );

        transfer(cpi_ctx, lamports)
    }

    pub fn cancel(ctx: Context<Cancel>) -> Result<()> {
        let accts = Transfer {
            from: ctx.accounts.vault.to_account_info(),
            to: ctx.accounts.maker.to_account_info(),
        };

        let seed = ctx.accounts.vault.seed.to_le_bytes();
        let maker =  ctx.accounts.vault.maker.as_ref();
        let taker =  ctx.accounts.vault.taker.as_ref();

        let seeds = &[
            "vault".as_bytes(),
            seed.as_ref(),
            maker,
            taker,
            &[ctx.accounts.vault.bump],
        ];
        let signer_seeds = &[&seeds[..]];

        let cpi_ctx = CpiContext::new_with_signer(
            ctx.accounts.system_program.to_account_info(),
            accts,
            signer_seeds,
        );

        transfer(cpi_ctx, ctx.accounts.vault.get_lamports())

    }

    pub fn claim(ctx: Context<Claim>) -> Result<()> {
        Ok(())
    }

}

#[derive(Accounts)]
#[instruction(seed:u64)]
pub struct Deposit<'info> {
    #[account(mut)]
    pub maker: Signer<'info>,
    /// CHECK: this is ok.
    pub taker: UncheckedAccount<'info>,
    #[account(init, payer=maker, seeds = [b"vault", seed.to_le_bytes().as_ref(), maker.key().as_ref(), taker.key().as_ref()], bump, space=Vault::INIT_SPACE)]
    pub vault: Account<'info, Vault>,
    pub system_program: Program<'info, System>,
}

#[derive(Accounts)]
pub struct Cancel<'info> {
    #[account(mut)]
    pub maker: Signer<'info>,
    #[account(mut, has_one= maker)]
    pub vault: Account<'info, Vault>,
    pub system_program: Program<'info, System>,
}



#[derive(Accounts)]
pub struct Claim<'info> {
    #[account(mut)]
    pub taker: Signer<'info>,
    // close account and send lamports to the signer/taker
    #[account(mut, has_one= taker, close=taker)]
    pub vault: Account<'info, Vault>,
    pub system_program: Program<'info, System>,
}


// https://www.anchor-lang.com/docs/space
impl Space for Vault {
    const INIT_SPACE: usize = 8 + 32 + 32 + 8 + 1 + 8;
}

#[account]
pub struct Vault {
    pub maker: Pubkey, // 32 bytes
    pub taker: Pubkey, // 32 bytes
    pub seed: u64, // 8 bytes
    pub bump: u8, // 1 byte
    pub created_at: i64, // 8 bytes
}
