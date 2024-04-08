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
  SYSVAR_RENT_PUBKEY,
} from "@solana/web3.js";
import {
  ExtensionType,
  TOKEN_PROGRAM_ID,
  TOKEN_2022_PROGRAM_ID,
  getMintLen,
  createInitializeMintInstruction,
  createInitializeTransferHookInstruction,
  ASSOCIATED_TOKEN_PROGRAM_ID,
  createMintToInstruction,
  createTransferCheckedInstruction,
  getAssociatedTokenAddressSync,
  createAssociatedTokenAccountIdempotentInstruction,
  getOrCreateAssociatedTokenAccount,
  createMint,
  MINT_SIZE,
  createTransferCheckedWithTransferHookInstruction,
} from "@solana/spl-token";
import { Buffer } from 'buffer';


describe("SPL Cards tests", () => {
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
  const mintOriginal = Keypair.generate();
  const mintWrapped = Keypair.generate();
  const decimals = 9;


const payerAtaOriginal = getAssociatedTokenAddressSync(
  mintOriginal.publicKey, 
  wallet.publicKey, 
  false, 
  TOKEN_2022_PROGRAM_ID
);


const payerAtaWrapped = getAssociatedTokenAddressSync(
    mintWrapped.publicKey,
    program.provider.publicKey,
    false, 
    TOKEN_2022_PROGRAM_ID
);


  const random_pubkey1 = Keypair.generate().publicKey;
  const random_pubkey2 = Keypair.generate().publicKey;
  const random_pubkey3 = Keypair.generate().publicKey;

  const allow_list = [random_pubkey1, random_pubkey2, signer2.publicKey];
  const remove_from_allow_list = [random_pubkey1, random_pubkey2];

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


  // Log data from from the Wrapper
  // remove console.log() to shut down the logs
  // const log_wrapper = async () => {
  //   const token_policy_log = await program.account.wrapperState.fetch(wrapper);
  //   console.log(token_policy_log);
  // }

    // Log data from from the wallet policy PDA
  // remove console.log() to shut down the logs
  const log_token_policy = async () => {
    const token_policy_log = await program.account.tokenPolicyState.fetch(tokenPolicyPDA);
    console.log(token_policy_log);
  }

    // Log data from from the wallet policy PDA
  // remove console.log() to shut down the logs
  const log_wallet_policy = async () => {
    const wallet_policy_log = await program.account.walletPolicyState.fetch(walletPolicyPDA);
    console.log(wallet_policy_log);
  }



  // UserPolicy constant
  const tokenPolicyPDA = PublicKey.findProgramAddressSync(
    [
      Buffer.from("token-policy"),
      program.provider.publicKey.toBuffer(),
      mintWrapped.publicKey.toBuffer(),
    ],
    program.programId,
  )[0];


  // WalletPolicy constant
  const walletPolicyPDA = PublicKey.findProgramAddressSync(
    [
      Buffer.from("wallet-policy"),
      program.provider.publicKey.toBuffer()
    ],
    program.programId,
  )[0];









  it("Create Mint Account", async () => {


    const lamports = await provider.connection.getMinimumBalanceForRentExemption(MINT_SIZE);
  
    const transaction = new Transaction().add(
      SystemProgram.createAccount({
        fromPubkey: wallet.publicKey,
        newAccountPubkey: mintOriginal.publicKey,
        space: MINT_SIZE,
        lamports: lamports,
        programId: TOKEN_2022_PROGRAM_ID,
      }),
      createInitializeMintInstruction(
        mintOriginal.publicKey,
        decimals,
        mintAuth,
        null,
        TOKEN_2022_PROGRAM_ID,
      ),
      createAssociatedTokenAccountIdempotentInstruction(
        wallet.publicKey,
        payerAtaOriginal,
        wallet.publicKey,
        mintOriginal.publicKey,
        TOKEN_2022_PROGRAM_ID,
      ),
      createMintToInstruction(
        mintOriginal.publicKey,
        payerAtaOriginal,
        wallet.publicKey,
        1000000000000,
        [],
        TOKEN_2022_PROGRAM_ID,
      ),
    );
  
    const txSig = await sendAndConfirmTransaction(
      provider.connection,
      transaction,
      [wallet.payer, mintOriginal],
    );
    console.log(`Transaction Signature: ${txSig}`);
  });  



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


//   const extra_account_meta_list = PublicKey.findProgramAddressSync(
//     [
//       Buffer.from("extra-account-metas"),
//       mintWrapped.publicKey.toBuffer(),
//     ],
//     program.programId,
//   )[0];


//   console.log(`mintWrapped public key: ${mintWrapped.publicKey.toString()}`)
  
//   // CREATE NEW WRAPPER
//   it("Create a new wrapper", async () => {

//     const tx = await program.methods.newWrapper(
//       "USDC",
//       "USDC",
//       "https://www.example.com/wrapper.json"
//     )
//     .accounts({
//       payer: wallet.payer.publicKey,
//       mintWrapped: mintWrapped.publicKey,
//       mintOriginal: mintOriginal.publicKey,
//       wrapper: wrapper,
//       vault: vault,
//       extraAccountMetaList: extra_account_meta_list,
//       rent: SYSVAR_RENT_PUBKEY,
//       associatedTokenProgram: ASSOCIATED_TOKEN_PROGRAM_ID,
//       tokenProgram: TOKEN_2022_PROGRAM_ID,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .signers([wallet.payer, mintWrapped])
//     .rpc({skipPreflight: true})
//     .then(confirm)
//     // .then(log_wrapper);
//     .then(log_tx);

//   });




  




//   // CREATE NEW FULLY DEFINED TOKEN POLICY
//   it("Create new fully defined Token Policy", async () => {

//     const tx = await program.methods.newFullTokenPolicy(
//       new anchor.BN(1000)
//     )
//     .accounts({
//       mintWrapped: mintWrapped.publicKey,
//       tokenPolicy: tokenPolicyPDA,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm);
//     // .then(log_token_policy);
//     // .then(log_tx);

//   });


//   // DELETE TOKEN POLICY
//   it("Delete Token Policy", async () => {

//     const tx = await program.methods.deleteTokenPolicy()
//     .accounts({
//       mintWrapped: mintWrapped.publicKey,
//       tokenPolicy: tokenPolicyPDA,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_token_policy)
//     .then(log_tx);

//   });


//   // CREATE NEW BASIC TOKEN POLICY
//   it("Create new basic Token Policy", async () => {

//     const tx = await program.methods.newTokenPolicy()
//     .accounts({
//       mintWrapped: mintWrapped.publicKey,
//       tokenPolicy: tokenPolicyPDA,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_token_policy)
//     .then(log_tx);

//   });


//   // ADD SPEND LIMIT TO TOKEN POLICY
//   it("Add spend limit to Token Policy", async () => {

//     const tx = await program.methods.addSpendLimitToTokenPolicy(
//       new anchor.BN(1)
//     )
//     .accounts({
//       mintWrapped: mintWrapped.publicKey,
//       tokenPolicy: tokenPolicyPDA,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_token_policy)
//     .then(log_tx);

//   });


//   // // REMOVE SPEND LIMIT FROM TOKEN POLICY
//   // it("Remove spend limit from Token Policy", async () => {

//   //   const tx = await program.methods.removeSpendLimitFromTokenPolicy()
//   //   .accounts({
//   //     mintWrapped: mintWrapped.publicKey,
//   //     tokenPolicy: tokenPolicyPDA,
//   //     systemProgram: anchor.web3.SystemProgram.programId,
//   //   })
//   //   .rpc()
//   //   .then(confirm)
//   //   // .then(log_token_policy)
//   //   .then(log_tx);

//   // });




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
//       walletPolicy: walletPolicyPDA,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_wallet_policy)
//     .then(log_tx);

//   });
  
  
//   // DELETE USER POLICY
//   it("Delete Wallet Policy", async () => {

//     const tx = await program.methods.deleteWalletPolicy()
//     .accounts({
//       walletPolicy: walletPolicyPDA,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_wallet_policy)
//     .then(log_tx);

//   });

//   // CREATE NEW BASIC USER POLICY
//   it("Create new basic Wallet Policy", async () => {

//     const tx = await program.methods.newWalletPolicy()
//     .accounts({
//       walletPolicy: walletPolicyPDA,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_wallet_policy)
//     .then(log_tx);

//   });


//   // ADD SIGNER1 TO USER POLICY with REQUIRE FALSE
//   it("Add signer1 to wallet policy require false", async () => {

//     const tx = await program.methods.addSigner1ToWalletPolicy(
//       signer1.publicKey,
//       false
//     )
//     .accounts({
//       walletPolicy: walletPolicyPDA,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_wallet_policy)
//     .then(log_tx);

//     });


//     // REMOVE SIGNER1 FROM USER POLICY
//     it("Remove signer1 from wallet policy", async () => {

//         const tx = await program.methods.removeSigner1FromWalletPolicy()
//         .accounts({
//         walletPolicy: walletPolicyPDA,
//         systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         // .then(log_wallet_policy)
//         .then(log_tx);

//     });


//   // ADD SIGNER1 TO USER POLICY with REQUIRE TRUE
//   it("Add signer1 to wallet policy require true", async () => {

//     const tx = await program.methods.addSigner1ToWalletPolicy(
//       signer1.publicKey,
//       true
//     )
//     .accounts({
//       walletPolicy: walletPolicyPDA,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_wallet_policy)
//     .then(log_tx);

//   });



//   // ADD SIGNER2 TO USER POLICY with REQUIRE FALSE
//   it("Add signer2 to wallet policy require false", async () => {

//     const tx = await program.methods.addSigner2ToWalletPolicy(
//       signer2.publicKey,
//       false
//     )
//     .accounts({
//       walletPolicy: walletPolicyPDA,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_wallet_policy)
//     .then(log_tx);

//     });


//     // REMOVE SIGNER2 FROM USER POLICY
//     it("Remove signer2 from wallet policy", async () => {

//         const tx = await program.methods.removeSigner2FromWalletPolicy()
//         .accounts({
//         walletPolicy: walletPolicyPDA,
//         systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         // .then(log_wallet_policy)
//         .then(log_tx);

//     });


//   // ADD SIGNER2 TO USER POLICY with REQUIRE TRUE
//   it("Add signer2 to wallet policy require true", async () => {

//     const tx = await program.methods.addSigner2ToWalletPolicy(
//       signer1.publicKey,
//       true
//     )
//     .accounts({
//       walletPolicy: walletPolicyPDA,
//       systemProgram: anchor.web3.SystemProgram.programId,
//     })
//     .rpc()
//     .then(confirm)
//     // .then(log_wallet_policy)
//     .then(log_tx);

//   });  


//     // ADD PUBLIC KEY TO ALLOW LIST
//     it("Add a list of public keys to allow list", async () => {

//         const tx = await program.methods.addAllowedPublickeysToWalletPolicy(
//           allow_list,
//         )
//         .accounts({
//           walletPolicy: walletPolicyPDA,
//           systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         // .then(log_wallet_policy)
//         .then(log_tx);
    
//       });  


//     // REMOVE PUBLIC KEY FROM ALLOW LIST
//     it("Remove a list of public keys from allow list", async () => {

//         const tx = await program.methods.removeAllowedPublickeysFromWalletPolicy(
//           remove_from_allow_list,
//         )
//         .accounts({
//           walletPolicy: walletPolicyPDA,
//           systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         // .then(log_wallet_policy)
//         .then(log_tx);
    
//       });  

//     // ADD PUBLIC KEY TO BLOCK LIST
//     it("Add a list of public keys to block list", async () => {

//         const tx = await program.methods.addBlockedPublickeysToWalletPolicy(
//           block_list,
//         )
//         .accounts({
//           walletPolicy: walletPolicyPDA,
//           systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         // .then(log_wallet_policy)
//         .then(log_tx);
    
//       });  


//     // REMOVE PUBLIC KEY FROM BLOCK LIST
//     it("Remove a list of public keys from block list", async () => {

//         const tx = await program.methods.removeBlockedPublickeysFromWalletPolicy(
//           remove_from_block_list,
//         )
//         .accounts({
//           walletPolicy: walletPolicyPDA,
//           systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         // .then(log_wallet_policy)
//         .then(log_tx);

//       });  

    
//     // ADD SPENDING WINDOW
//     it("Add spending window to wallet policy", async () => {

//         const tx = await program.methods.addSpendingWindowToWalletPolicy(
//           spending_window,
//         )
//         .accounts({
//           walletPolicy: walletPolicyPDA,
//           systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         // .then(log_wallet_policy)
//         .then(log_tx);
    
//       });  


//     // REMOVE SPENDING WINDOW
//     it("Remove spending window from wallet policy", async () => {

//         const tx = await program.methods.removeSpendingWindowFromWalletPolicy()
//         .accounts({
//           walletPolicy: walletPolicyPDA,
//           systemProgram: anchor.web3.SystemProgram.programId,
//         })
//         .rpc()
//         .then(confirm)
//         // .then(log_wallet_policy)
//         .then(log_tx);
    
//       });  







//   // UPDATE WRAPPER SYMBOL
//   it("Update wrapper symbol", async () => {

//     const tx = await program.methods.updateWrapper(
//       "BONK"
//     )
//     .accounts({
//       payer: wallet.payer.publicKey,
//       mintWrapped: mintWrapped.publicKey,
//       mintOriginal: mintOriginal.publicKey,
//       wrapper: wrapper,
//       systemProgram: anchor.web3.SystemProgram.programId,
//       associatedTokenProgram: ASSOCIATED_TOKEN_PROGRAM_ID,
//       tokenProgram: TOKEN_2022_PROGRAM_ID,
//     })
//     .signers([wallet.payer, mintWrapped])
//     .rpc()
//     .then(confirm)
//     // .then(log_wrapper)
//     .then(log_tx);

//   });


// // console.log(payerAtaOriginal.toString());

//  // WRAP TOKEN
// it("Wrap token", async () => {

//   const tx = await program.methods.wrap(
//       new anchor.BN(100)
//   )
//   .accounts({
//     payer: wallet.payer.publicKey,
//     payerAtaOriginal: payerAtaOriginal,
//     payerAtaWrapped: payerAtaWrapped,
//     mintOriginal: mintOriginal.publicKey,
//     mintWrapped: mintWrapped.publicKey,
//     wrapper: wrapper,
//     vault: vault,
//     associatedTokenProgram: ASSOCIATED_TOKEN_PROGRAM_ID,
//     // REMEMBER TO SWITCH THIS UP if you use a Natvie Mint as original Token
//     tokenProgram: TOKEN_2022_PROGRAM_ID,
//     tokenExtensionsProgram: TOKEN_2022_PROGRAM_ID,
//     systemProgram: anchor.web3.SystemProgram.programId,
//   })
//   .signers([wallet.payer])
//   .rpc({skipPreflight: true})
//   .then(confirm)
//   .then(log_tx);
// });




//   // // DELETE TOKEN POLICY
//   // it("Delete Token Policy", async () => {

//   //   const tx = await program.methods.deleteTokenPolicy()
//   //   .accounts({
//   //     mintWrapped: mintWrapped.publicKey,
//   //     tokenPolicy: tokenPolicyPDA,
//   //     systemProgram: anchor.web3.SystemProgram.programId,
//   //   })
//   //   .rpc()
//   //   .then(confirm)
//   //   // .then(log_token_policy)
//   //   .then(log_tx);

//   // });
  



//   it("Transfer Hook with Extra Account Meta", async () => {

//     let signer2TokenAccount = await getOrCreateAssociatedTokenAccount(
//       connection, 
//       wallet.payer, 
//       mintWrapped.publicKey, 
//       signer2.publicKey,
//       true,
//       undefined,
//       undefined,
//       TOKEN_2022_PROGRAM_ID,
//       ASSOCIATED_TOKEN_PROGRAM_ID
//     )

//     let amount = BigInt(1);

//     let transferInstructionWithHelper = await createTransferCheckedWithTransferHookInstruction( 
//       connection,
//       payerAtaWrapped,
//       mintWrapped.publicKey,
//       signer2TokenAccount.address,
//       wallet.publicKey,
//       amount,
//       decimals,
//       [],
//       "confirmed",
//       TOKEN_2022_PROGRAM_ID,
//     );

//     let tx = new Transaction().add(transferInstructionWithHelper);

//     const txSig = await sendAndConfirmTransaction(
//       connection,
//       tx,
//       [wallet.payer],
//       { skipPreflight: true, commitment: "confirmed"}
//     );
//     console.log("This is the token transfer:", txSig);

//   });

//   it("Transfer Hook with Extra Account Meta", async () => {

//     let signer2TokenAccount = await getOrCreateAssociatedTokenAccount(
//       connection, 
//       wallet.payer, 
//       mintWrapped.publicKey, 
//       signer2.publicKey,
//       true,
//       undefined,
//       undefined,
//       TOKEN_2022_PROGRAM_ID,
//       ASSOCIATED_TOKEN_PROGRAM_ID
//     )

//     let amount = BigInt(1);

//     let transferInstructionWithHelper = await createTransferCheckedWithTransferHookInstruction( 
//       connection,
//       payerAtaWrapped,
//       mintWrapped.publicKey,
//       signer2TokenAccount.address,
//       wallet.publicKey,
//       amount,
//       decimals,
//       [],
//       "confirmed",
//       TOKEN_2022_PROGRAM_ID,
//     );

//     let tx = new Transaction().add(transferInstructionWithHelper);

//     const txSig = await sendAndConfirmTransaction(
//       connection,
//       tx,
//       [wallet.payer],
//       { skipPreflight: true, commitment: "confirmed"}
//     );
//     console.log("This is the token transfer:", txSig);

//   });


//     //       DELETE USER POLICY
//     it("Delete Wallet Policy", async () => {

//       const tx = await program.methods.deleteWalletPolicy()
//       .accounts({
//         walletPolicy: walletPolicyPDA,
//         systemProgram: anchor.web3.SystemProgram.programId,
//       })
//       .rpc()
//       .then(confirm)
//       // .then(log_wallet_policy)
//       .then(log_tx);

//     });
  
});
