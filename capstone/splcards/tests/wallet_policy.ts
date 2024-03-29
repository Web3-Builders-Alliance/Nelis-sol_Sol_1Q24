// import * as anchor from "@coral-xyz/anchor";
// import { Program } from "@coral-xyz/anchor";
// import { Splcards } from "../target/types/splcards";
// import { Keypair, LAMPORTS_PER_SOL, PublicKey, Commitment } from "@solana/web3.js";

// describe("User Policy tests", () => {
//   // Configure the client to use the local cluster.
//   anchor.setProvider(anchor.AnchorProvider.env());

//   const program = anchor.workspace.Splcards as Program<Splcards>;
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

//   // Log data from from the user policy PDA
//   // remove console.log() to shut down the logs
//   const log_wallet_policy = async () => {
//     const wallet_policy_log = await program.account.walletPolicyState.fetch(wallet_policy_pda);
//     console.log(wallet_policy_log);
//   }


//   // WalletPolicy constant
//   const wallet_policy_pda = PublicKey.findProgramAddressSync(
//     [
//       Buffer.from("wallet-policy"),
//       program.provider.publicKey.toBuffer(),
//     ],
//     program.programId,
//   )[0];


//   // CONSTANTS
//   const signer1 = Keypair.generate();
//   const signer2 = Keypair.generate();

//   const random_pubkey1 = Keypair.generate().publicKey;
//   const random_pubkey2 = Keypair.generate().publicKey;
//   const random_pubkey3 = Keypair.generate().publicKey;

//   const allow_list = [random_pubkey1, random_pubkey2, random_pubkey3];
//   const remove_from_allow_list = [random_pubkey2, random_pubkey3];

//   const block_list = [random_pubkey2, random_pubkey3];
//   const remove_from_block_list = [random_pubkey2];

//   // calculate timestamps for spending window
//   const midnight = new Date().setUTCHours(0, 0, 0, 0);

//   const start = new Date(midnight)
//   start.setUTCHours(8);
//   const start_ts = start.getTime() / 1000;

//   const end = new Date(midnight)
//   end.setUTCHours(8);
//   const end_ts = end.getTime() / 1000;
  
//   const spending_window = [new anchor.BN(start_ts), new anchor.BN(end_ts)]


//   // CREATE NEW FULLY DEFINED USER POLICY
//   it("Create new fully defined User Policy", async () => {

//     const tx = await program.methods.newFullWalletPolicy(
//         signer1.publicKey,
//         true,
//         signer2.publicKey,
//         false,
//         allow_list,
//         block_list,
//         spending_window

//     )
//     .accounts({
//       walletPolicy: wallet_policy_pda,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     .then(log_wallet_policy)
//     // .then(log_tx);

//   });
  
  
//   // DELETE USER POLICY
//   it("Delete User Policy", async () => {

//     const tx = await program.methods.deleteWalletPolicy()
//     .accounts({
//       walletPolicy: wallet_policy_pda,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_wallet_policy)
//     // .then(log_tx);

//   });

//   // CREATE NEW BASIC USER POLICY
//   it("Create new basic User Policy", async () => {

//     const tx = await program.methods.newWalletPolicy()
//     .accounts({
//       walletPolicy: wallet_policy_pda,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     .then(log_wallet_policy)
//     // .then(log_tx);

//   });


//   // ADD SIGNER1 TO USER POLICY with REQUIRE FALSE
//   it("Add signer1 to user policy require false", async () => {

//     const tx = await program.methods.addSigner1ToWalletPolicy(
//       signer1.publicKey,
//       false
//     )
//     .accounts({
//       walletPolicy: wallet_policy_pda,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     .then(log_wallet_policy)
//     // .then(log_tx);

//     });


//     // REMOVE SIGNER1 FROM USER POLICY
//     it("Remove signer1 from user policy", async () => {

//         const tx = await program.methods.removeSigner1FromWalletPolicy()
//         .accounts({
//         walletPolicy: wallet_policy_pda,
//         systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         .then(log_wallet_policy)
//         // .then(log_tx);

//     });


//   // ADD SIGNER1 TO USER POLICY with REQUIRE TRUE
//   it("Add signer1 to user policy require true", async () => {

//     const tx = await program.methods.addSigner1ToWalletPolicy(
//       signer1.publicKey,
//       true
//     )
//     .accounts({
//       walletPolicy: wallet_policy_pda,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     .then(log_wallet_policy)
//     // .then(log_tx);

//   });



//   // ADD SIGNER2 TO USER POLICY with REQUIRE FALSE
//   it("Add signer2 to user policy require false", async () => {

//     const tx = await program.methods.addSigner2ToWalletPolicy(
//       signer2.publicKey,
//       false
//     )
//     .accounts({
//       walletPolicy: wallet_policy_pda,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     .then(log_wallet_policy)
//     // .then(log_tx);

//     });


//     // REMOVE SIGNER2 FROM USER POLICY
//     it("Remove signer2 from user policy", async () => {

//         const tx = await program.methods.removeSigner2FromWalletPolicy()
//         .accounts({
//         walletPolicy: wallet_policy_pda,
//         systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         .then(log_wallet_policy)
//         // .then(log_tx);

//     });


//   // ADD SIGNER2 TO USER POLICY with REQUIRE TRUE
//   it("Add signer2 to user policy require true", async () => {

//     const tx = await program.methods.addSigner2ToWalletPolicy(
//       signer1.publicKey,
//       true
//     )
//     .accounts({
//       walletPolicy: wallet_policy_pda,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     .then(log_wallet_policy)
//     // .then(log_tx);

//   });  


//     // ADD PUBLIC KEY TO ALLOW LIST
//     it("Add a list of public keys to allow list", async () => {

//         const tx = await program.methods.addAllowedPublickeysToWalletPolicy(
//           allow_list,
//         )
//         .accounts({
//           walletPolicy: wallet_policy_pda,
//           systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         .then(log_wallet_policy)
//         // .then(log_tx);
    
//       });  


//     // REMOVE PUBLIC KEY FROM ALLOW LIST
//     it("Remove a list of public keys from allow list", async () => {

//         const tx = await program.methods.removeAllowedPublickeysFromWalletPolicy(
//           remove_from_allow_list,
//         )
//         .accounts({
//           walletPolicy: wallet_policy_pda,
//           systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         .then(log_wallet_policy)
//         // .then(log_tx);
    
//       });  

//     // ADD PUBLIC KEY TO BLOCK LIST
//     it("Add a list of public keys to block list", async () => {

//         const tx = await program.methods.addBlockedPublickeysToWalletPolicy(
//           block_list,
//         )
//         .accounts({
//           walletPolicy: wallet_policy_pda,
//           systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         .then(log_wallet_policy)
//         // .then(log_tx);
    
//       });  


//     // REMOVE PUBLIC KEY FROM BLOCK LIST
//     it("Remove a list of public keys from block list", async () => {

//         const tx = await program.methods.removeBlockedPublickeysFromWalletPolicy(
//           remove_from_block_list,
//         )
//         .accounts({
//           walletPolicy: wallet_policy_pda,
//           systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         .then(log_wallet_policy)
//         // .then(log_tx);

//       });  

    
//     // ADD SPENDING WINDOW
//     it("Add spending window to user policy", async () => {

//         const tx = await program.methods.addSpendingWindowToWalletPolicy(
//           spending_window,
//         )
//         .accounts({
//           walletPolicy: wallet_policy_pda,
//           systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         .then(log_wallet_policy)
//         // .then(log_tx);
    
//       });  


//     // REMOVE SPENDING WINDOW
//     it("Remove spending window from user policy", async () => {

//         const tx = await program.methods.removeSpendingWindowFromWalletPolicy()
//         .accounts({
//           walletPolicy: wallet_policy_pda,
//           systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         .then(log_wallet_policy)
//         // .then(log_tx);
    
//       });  

//         // DELETE USER POLICY
//   it("Delete User Policy", async () => {

//     const tx = await program.methods.deleteWalletPolicy()
//     .accounts({
//       walletPolicy: wallet_policy_pda,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_wallet_policy)
//     // .then(log_tx);

//   });

// });
