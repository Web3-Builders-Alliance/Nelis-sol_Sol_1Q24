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

    /// Initializes a new complete wallet policy. 
    /// 
    /// A wallet policy is unique for a given owner.
    /// 
    /// Wallet policies contain settings of a user for any token where the user is the owner.
    /// Possible settings include a spending window (e.g. only allow transactions during office hours) or specifying a 2nd signer.
    /// 
    /// This this instruction creates a complete wallet policy which can be modified using instructions like .remove_signer1().
    /// 
    /// 
    /// # Arguments
    ///
    /// * `ctx` - The context for the initialization (automatically added).
    /// * `signer1` - Extra signer for transactions that exceed the spending limit, next to the owner's signature. 
    /// * `required_signer1` -  Indicator if signer1 is mandatory.
    /// * `signer2` - Extra signer for transactions that exceed the spending limit, next to the owner's signature and signer1. 
    /// * `required_signer2` - Indicator if signer2 is mandatory.
    /// * `allow_list` - List of approved destination addresses. If set, transactions to addresses not in this list will be blocked.
    /// * `block_list` - List of blocked destination addresses. Transactions to these addresses are blocked.
    /// * `spending_window` - Begin and end time outside of which transactions are blocked. For example: transactions at night or outside of office hours.
    /// 
    /// # Example: Typescript client call
    ///  ```typescript
    /// const walletPolicyPDA = PublicKey.findProgramAddressSync(
    ///         [ Buffer.from("wallet-policy"), signer.publicKey.toBuffer() ],
    ///         program.programId,
    /// )[0];
    ///
    /// await program.methods.newFullWalletPolicy(
    ///         signer1.publicKey,
    ///         true,
    ///         signer2.publicKey,
    ///         false,
    ///         allow_list,
    ///         block_list,
    ///         spending_window
    ///     )
    ///     .accounts({
    ///         mintWrapped: mintWrapped.publicKey,
    ///         walletPolicy: walletPolicyPDA,
    ///         systemProgram: SystemProgram.programId,
    ///     })
    ///     .rpc()
    /// ```
    /// 
    /// # Output: PDA created
    /// ```
    /// struct WalletPolicyState {
    ///     authority: signer.publicKey,                // 32 bytes
    ///     signer1: signer1.publicKey,                 // 1 + 32 = 33 bytes
    ///     required_signer1: true,                     // 1 byte
    ///     signer2: signer2.publicKey,                 // 1 + 32 = 33 bytes
    ///     required_signer2: false,                    // 1 byte
    ///     allow_list: [allowAddress1.publicKey, allowedAddress2.publicKey],   // 4 + 32 * n = 68 bytes
    ///     block_list: [],                             // 4 + 32 * n = 4 bytes
    ///     spending_window: [1712050648, 1712068648],  // 1 + 8 + 8 = 17 bytes
    ///     bump: bump,                                 // 1 byte
    /// }
    /// ```
    ///
    /// # Errors
    ///
    /// Returns an error if the initialization fails.
    pub fn new_full_wallet_policy(ctx: Context<WalletPolicyInstructions>, 
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
    pub fn new_wallet_policy(ctx: Context<WalletPolicyInstructions>) -> Result<()> {
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

    /// Initializes a new complete token policy. 
    /// 
    /// A token policy is unique for a given owner and mint.
    /// 
    /// Token policies contain settings of a user for tokens of a specific mint. 
    /// These policies are on top of the Wallet Policies set by a user, which are valid for every transaction.
    /// Token Policies are only valid for a specific mint.
    /// 
    /// Possible settings include a daily spend limit.
    /// 
    /// This this instruction creates a complete token policy which can be modified using instructions like .remove_spend_limit_from_token_policy().
    /// 
    /// 
    ///
    /// # Arguments
    ///
    /// * `ctx` - The context for the initialization (automatically added).
    /// * `spend_limit` - The number of tokens that can be spend within 24 hours.
    /// 
    /// # Example: Typescript client call
    ///  ```typescript
    /// await program.methods.newFullTokenPolicy(
    ///     new anchor.BN(1000)
    ///     )
    ///     .accounts({
    ///         mintWrapped: mintWrapped.publicKey,
    ///         tokenPolicy: tokenPolicyPDA,
    ///         systemProgram: SystemProgram.programId,
    ///     })
    ///     .rpc()
    /// ```
    /// 
    /// # Output: PDA created
    /// ```
    /// struct TokenPolicyState {
    ///     authority: signer.publicKey,    // 32 bytes
    ///     mint: mintWrapped.publicKey,    // 32 bytes
    ///     spent_last_24: [0, 0],          // 8 + 8 = 16 bytes
    ///     spend_limit_amount: 1000,       // 1 + 8 = 9 bytes
    ///     bump: bump,                     // 1 byte
    /// }
    /// ```
    ///
    /// # Errors
    ///
    /// Returns an error if the initialization fails.
    pub fn new_full_token_policy(
        ctx: Context<TokenPolicyInstructions>, 
        spend_limit_amount: Option<u64>
    ) -> Result<()> {
        ctx.accounts.new_full_token_policy(spend_limit_amount, &ctx.bumps)
    }

    /// Initializes a new basic token policy. 
    /// 
    /// A token policy is unique for a given owner and mint.
    /// 
    /// Token policies contain settings of a user for tokens of a specific mint.
    /// Possible settings include a daily spend limit.
    /// 
    /// This this instruction creates a basic token policy which can be build out using instructions like .add_spend_limit_to_token_policy().
    /// 
    /// 
    ///
    /// # Arguments
    ///
    /// * `ctx` - The context for the initialization (automatically added).
    /// 
    /// # Example: Typescript client call
    ///  ```typescript
    /// await program.methods.newTokenPolicy()
    ///     .accounts({
    ///         mintWrapped: mintWrapped.publicKey,
    ///         tokenPolicy: tokenPolicyPDA,
    ///         systemProgram: SystemProgram.programId,
    ///     })
    ///     .rpc()
    /// ```
    /// 
    /// # Output: PDA created
    /// ```
    /// struct TokenPolicyState {
    ///     authority: signer.publicKey,    // 32 bytes
    ///     mint: mintWrapped.publicKey,    // 32 bytes
    ///     spent_last_24: [0, 0],          // 8 + 8 = 16 bytes
    ///     spend_limit_amount: None,       // 1 + 8 = 9 bytes
    ///     bump: bump,                     // 1 byte
    /// }
    /// ```
    ///
    /// # Errors
    ///
    /// Returns an error if the initialization fails.
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

    pub fn new_wrapper(ctx: Context<WrapperInstructions>, name: String, symbol: String, uri: String) -> Result<()> {
        ctx.accounts.new_wrapper(name, symbol, uri, &ctx.bumps)
    }

    pub fn update_wrapper(ctx: Context<WrapperInstructions>, symbol: String) -> Result<()> {
        ctx.accounts.update_wrapper(symbol)
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

