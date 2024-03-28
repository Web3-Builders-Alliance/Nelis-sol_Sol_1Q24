// import * as anchor from "@coral-xyz/anchor";
// import { Program } from "@coral-xyz/anchor";
// import { Splcards } from "../target/types/splcards";
// import { Keypair, 
//   LAMPORTS_PER_SOL, 
//   PublicKey, 
//   Commitment,
//   SystemProgram,
//   Transaction,
//   sendAndConfirmTransaction, 
// } from "@solana/web3.js";
// import {
//   ExtensionType,
//   TOKEN_PROGRAM_ID,
//   TOKEN_2022_PROGRAM_ID,
//   getMintLen,
//   createInitializeMintInstruction,
//   createInitializeTransferHookInstruction,
//   ASSOCIATED_TOKEN_PROGRAM_ID,
//   createMintToInstruction,
//   createTransferCheckedInstruction,
//   getAssociatedTokenAddressSync,
//   getOrCreateAssociatedTokenAccount,
//   createAssociatedTokenAccountIdempotentInstruction,
//   AccountLayout,
//   createAssociatedTokenAccountInstruction,
//   getAssociatedTokenAddress,
//   createMint,
// } from "@solana/spl-token";
// import { ASSOCIATED_PROGRAM_ID } from "@coral-xyz/anchor/dist/cjs/utils/token";

// describe("Token Policy tests", () => {
//   // Configure the client to use the local cluster.
//   anchor.setProvider(anchor.AnchorProvider.env());

//   const program = anchor.workspace.Splcards as Program<Splcards>;
//   const provider = anchor.AnchorProvider.env();
//   anchor.setProvider(provider);
//   const wallet = provider.wallet as anchor.Wallet;
//   const connection = anchor.getProvider().connection;


//   const confirm = async (signature: string): Promise<string> => {
//     const block = await connection.getLatestBlockhash();
//     await connection.confirmTransaction({
//       signature,
//       ...block,
//     });
//     return signature;
//   };

//   const log_tx = async (signature: string): Promise<string> => {
//     console.log(
//       `Your tx: https://explorer.solana.com/transaction/${signature}?cluster=custom&customUrl=${connection.rpcEndpoint}`
//     );
//     return signature;
//   };


//   // CONSTANTS
//   const signer1 = Keypair.generate();
//   const signer2 = Keypair.generate();

//   const mintAuth = program.provider.publicKey;
//   const mintOriginal = Keypair.generate();
//   const mintWrapped = Keypair.generate();
//   const decimals = 9;



//   const random_pubkey1 = Keypair.generate().publicKey;
//   const random_pubkey2 = Keypair.generate().publicKey;
//   const random_pubkey3 = Keypair.generate().publicKey;

//   const allow_list = [random_pubkey1, random_pubkey2, random_pubkey3];
//   const remove_from_allow_list = [random_pubkey2, random_pubkey3];

//   const block_list = [random_pubkey2, random_pubkey3];
//   const remove_from_block_list = [random_pubkey2];

//   const spent_last_24 = [1711458410, 350];

//   // calculate timestamps for spending window
//   const midnight = new Date().setUTCHours(0, 0, 0, 0);

//   const start = new Date(midnight)
//   start.setUTCHours(8);
//   const start_ts = start.getTime() / 1000;

//   const end = new Date(midnight)
//   end.setUTCHours(8);
//   const end_ts = end.getTime() / 1000;
  
//   const spending_window = [new anchor.BN(start_ts), new anchor.BN(end_ts)]

//   const payerAtaOriginal = getAssociatedTokenAddressSync(
//     mintOriginal.publicKey, 
//     wallet.publicKey, 
//     false, 
//     TOKEN_2022_PROGRAM_ID
// );


//     const payerAtaWrapped = getAssociatedTokenAddressSync(
//         mintWrapped.publicKey,
//         program.provider.publicKey,
//         false, 
//         TOKEN_2022_PROGRAM_ID
//     );


//   // UserPolicy constant
//   const token_policy_pda = PublicKey.findProgramAddressSync(
//     [
//       Buffer.from("token-policy"),
//       program.provider.publicKey.toBuffer(),
//       mintOriginal.publicKey.toBuffer(),
//     ],
//     program.programId,
//   )[0];


//   const wrapper = PublicKey.findProgramAddressSync(
//     [
//       Buffer.from("wrapper"),
//       mintOriginal.publicKey.toBuffer(),
//     ],
//     program.programId,
//   )[0];


//   const vault = PublicKey.findProgramAddressSync(
//     [
//       Buffer.from("vault"),
//       mintOriginal.publicKey.toBuffer(),
//     ],
//     program.programId,
//   )[0];





//     it("Create Mint Account with Transfer Hook Extension", async () => {
//     const extensions = [ExtensionType.TransferHook];
//     const mintLen = getMintLen(extensions);
//     const lamports = await provider.connection.getMinimumBalanceForRentExemption(mintLen);
  
//     const transaction = new Transaction().add(
//       SystemProgram.createAccount({
//         fromPubkey: wallet.publicKey,
//         newAccountPubkey: mintOriginal.publicKey,
//         space: mintLen,
//         lamports: lamports,
//         programId: TOKEN_2022_PROGRAM_ID,
//       }),
//       createInitializeTransferHookInstruction(
//         mintOriginal.publicKey,
//         wallet.publicKey,
//         program.programId, // Transfer Hook Program ID
//         TOKEN_2022_PROGRAM_ID,
//       ),
//       createInitializeMintInstruction(
//         mintOriginal.publicKey,
//         decimals,
//         mintAuth,
//         null,
//         TOKEN_2022_PROGRAM_ID,
//       ),
//     );
  
//     const txSig = await sendAndConfirmTransaction(
//       provider.connection,
//       transaction,
//       [wallet.payer, mintOriginal],
//     );
//     console.log(`Transaction Signature: ${txSig}`);
//   });  



//     // CREATE NEW WRAPPER
//   it("Create a new wrapper", async () => {

//     const tx = await program.methods.newWrapper(
//       "USDC",
//       "USDC",
//       "https://www.shdwstorage.com/wrapper.json"
//     )
//     .accounts({
//       mintOriginal: mintOriginal.publicKey,
//       mintWrapped: mintOriginal.publicKey,
//       wrapper: wrapper,
//       vault: vault,
//       systemProgram: anchor.web3.SystemProgram.programId,
//       associatedTokenProgram: ASSOCIATED_TOKEN_PROGRAM_ID,
//       tokenProgram: TOKEN_2022_PROGRAM_ID,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_tx);

//   });

// // WRAP TOKEN
// it("Wrap token", async () => {
    

//     console.log("a");

//       const mintWrappedAddress = await createMint(
//         connection,
//         // this is the mint authority
//         wallet.payer,
//         mintAuth,
//         mintAuth,
//         9,
//         mintWrapped
//       );
//       console.log("c");

//     const tx = await program.methods.wrap(
//         new anchor.BN(100)
//     )
//     .accounts({
//         payerAtaOriginal: payerAtaOriginal,
//         payerAtaWrapped: payerAtaWrapped,
//         mintOriginal: mintOriginalAddress,
//         mintWrapped: mintWrappedAddress,
//         wrapper: wrapper,
//         vault: vault,
//         associatedTokenProgram: ASSOCIATED_TOKEN_PROGRAM_ID,
//         tokenProgram: TOKEN_PROGRAM_ID,
//         tokenExtensionsProgram: TOKEN_2022_PROGRAM_ID,
//         systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_tx);

//   });







// });