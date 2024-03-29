import * as anchor from "@coral-xyz/anchor";
import { Program } from "@coral-xyz/anchor";
import { Splcards } from "../target/types/splcards";
import { Keypair, 
  LAMPORTS_PER_SOL, 
  PublicKey, 
  Commitment,
  SystemProgram,
  Transaction,
  sendAndConfirmTransaction, 
} from "@solana/web3.js";
import {
  ExtensionType,
  TOKEN_2022_PROGRAM_ID,
  getMintLen,
  createInitializeMintInstruction,
  createInitializeTransferHookInstruction,
  ASSOCIATED_TOKEN_PROGRAM_ID,
  createMintToInstruction,
  createTransferCheckedInstruction,
  getAssociatedTokenAddressSync,
  createAssociatedTokenAccountIdempotentInstruction,
} from "@solana/spl-token";

describe("Token Policy tests", () => {
  // Configure the client to use the local cluster.
  anchor.setProvider(anchor.AnchorProvider.env());

  const program = anchor.workspace.Splcards as Program<Splcards>;
  const provider = anchor.AnchorProvider.env();
  anchor.setProvider(provider);
  const wallet = provider.wallet as anchor.Wallet;
  const connection = anchor.getProvider().connection;


  const confirm = async (signature: string): Promise<string> => {
    const block = await connection.getLatestBlockhash();
    await connection.confirmTransaction({
      signature,
      ...block,
    });
    return signature;
  };

  const log_tx = async (signature: string): Promise<string> => {
    console.log(
      `Your tx: https://explorer.solana.com/transaction/${signature}?cluster=custom&customUrl=${connection.rpcEndpoint}`
    );
    return signature;
  };


  // CONSTANTS
  const signer1 = Keypair.generate();
  const signer2 = Keypair.generate();

  const mintAuth = program.provider.publicKey;
  const mint = Keypair.generate();
  const decimals = 0;

  const random_pubkey1 = Keypair.generate().publicKey;
  const random_pubkey2 = Keypair.generate().publicKey;
  const random_pubkey3 = Keypair.generate().publicKey;

  const allow_list = [random_pubkey1, random_pubkey2, random_pubkey3];
  const remove_from_allow_list = [random_pubkey2, random_pubkey3];

  const block_list = [random_pubkey2, random_pubkey3];
  const remove_from_block_list = [random_pubkey2];

  const spent_last_24 = [1711458410, 350];

  // calculate timestamps for spending window
  const midnight = new Date().setUTCHours(0, 0, 0, 0);

  const start = new Date(midnight)
  start.setUTCHours(8);
  const start_ts = start.getTime() / 1000;

  const end = new Date(midnight)
  end.setUTCHours(8);
  const end_ts = end.getTime() / 1000;
  
  const spending_window = [new anchor.BN(start_ts), new anchor.BN(end_ts)]


  // Log data from from the user policy PDA
  // remove console.log() to shut down the logs
  const log_token_policy = async () => {
    const token_policy_log = await program.account.tokenPolicyState.fetch(token_policy_pda);
    console.log(token_policy_log);
  }


  // UserPolicy constant
  const token_policy_pda = PublicKey.findProgramAddressSync(
    [
      Buffer.from("token-policy"),
      program.provider.publicKey.toBuffer(),
      mint.publicKey.toBuffer(),
    ],
    program.programId,
  )[0];



  it("Create Mint Account with Transfer Hook Extension", async () => {
    const extensions = [ExtensionType.TransferHook];
    const mintLen = getMintLen(extensions);
    const lamports = await provider.connection.getMinimumBalanceForRentExemption(mintLen);
  
    const transaction = new Transaction().add(
      SystemProgram.createAccount({
        fromPubkey: wallet.publicKey,
        newAccountPubkey: mint.publicKey,
        space: mintLen,
        lamports: lamports,
        programId: TOKEN_2022_PROGRAM_ID,
      }),
      createInitializeTransferHookInstruction(
        mint.publicKey,
        wallet.publicKey,
        program.programId, // Transfer Hook Program ID
        TOKEN_2022_PROGRAM_ID,
      ),
      createInitializeMintInstruction(
        mint.publicKey,
        decimals,
        mintAuth,
        null,
        TOKEN_2022_PROGRAM_ID,
      ),
    );
  
    const txSig = await sendAndConfirmTransaction(
      provider.connection,
      transaction,
      [wallet.payer, mint],
    );
    console.log(`Transaction Signature: ${txSig}`);
  });  



  // CREATE NEW FULLY DEFINED TOKEN POLICY
  it("Create new fully defined Token Policy", async () => {

    const tx = await program.methods.newFullTokenPolicy(
      new anchor.BN(1000)
    )
    .accounts({
      mint: mint.publicKey,
      tokenPolicy: token_policy_pda,
      systemProgram: anchor.web3.SystemProgram.programId,
    })
    .rpc()
    .then(confirm)
    .then(log_token_policy)
    // .then(log_tx);

  });


  // DELETE TOKEN POLICY
  it("Delete Token Policy", async () => {

    const tx = await program.methods.deleteTokenPolicy()
    .accounts({
      mint: mint.publicKey,
      tokenPolicy: token_policy_pda,
      systemProgram: anchor.web3.SystemProgram.programId,
    })
    .rpc()
    .then(confirm)
    // .then(log_token_policy)
    // .then(log_tx);

  });


  // CREATE NEW BASIC TOKEN POLICY
  it("Create new basic Token Policy", async () => {

    const tx = await program.methods.newTokenPolicy()
    .accounts({
      mint: mint.publicKey,
      tokenPolicy: token_policy_pda,
      systemProgram: anchor.web3.SystemProgram.programId,
    })
    .rpc()
    .then(confirm)
    .then(log_token_policy)
    // .then(log_tx);

  });


  // ADD SPEND LIMIT TO TOKEN POLICY
  it("Add spend limit to Token Policy", async () => {

    const tx = await program.methods.addSpendLimitToTokenPolicy(
      new anchor.BN(10000)
    )
    .accounts({
      mint: mint.publicKey,
      tokenPolicy: token_policy_pda,
      systemProgram: anchor.web3.SystemProgram.programId,
    })
    .rpc()
    .then(confirm)
    .then(log_token_policy)
    // .then(log_tx);

  });


  // REMOVE SPEND LIMIT FROM TOKEN POLICY
  it("Remove spend limit from Token Policy", async () => {

    const tx = await program.methods.removeSpendLimitFromTokenPolicy()
    .accounts({
      mint: mint.publicKey,
      tokenPolicy: token_policy_pda,
      systemProgram: anchor.web3.SystemProgram.programId,
    })
    .rpc()
    .then(confirm)
    .then(log_token_policy)
    // .then(log_tx);

  });
  
  
});
