import { useState } from "react";
import { useAnchorWallet } from "@solana/wallet-adapter-react";
import { 
  Connection, 
  PublicKey, 
  SystemProgram, 
  Keypair, 
  SYSVAR_RENT_PUBKEY, 
  ComputeBudgetProgram,
  Transaction,
  LAMPORTS_PER_SOL,
} from "@solana/web3.js";
import { Program, AnchorProvider, Wallet } from "@project-serum/anchor";
import { useWallet } from "@solana/wallet-adapter-react";
import {
  WalletMultiButton,
  WalletDisconnectButton,
} from "@solana/wallet-adapter-react-ui";
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
import idl from "./splcards.json"; // The path to your JSON IDL file

const programID = new PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew");
const network = "https://mainnet.helius-rpc.com/?api-key=2d6c544c-8fc7-4bac-9352-a60a7bb2a391"; // Adjust for your environment: local, devnet, or mainnet-beta
const opts = { preflightCommitment: "finalized" };

const App = () => {

  const wallet = useAnchorWallet();

  const { connected } = useWallet();



  // State handlers

  const [mintOriginalValue, setMintOriginalValue] = useState('');
  const handleChangeMintOriginal = (event) => {
    setMintOriginalValue(event.target.value);
    console.log(`mint original: ${event.target.value}`);
  };

  const [symbolValue, setSymbolValue] = useState('');
  const handleChangeSymbol = (event) => {
    setSymbolValue(event.target.value);
    console.log(`symbol: ${event.target.value}`);
  };

  const [uriValue, setUriValue] = useState('');
  const handleChangeUri = (event) => {
    setUriValue(event.target.value);
    console.log(`uri: ${event.target.value}`);
  };


  // const [mintWrappedValue, setMintWrappedValue] = useState('');

  // const handleChangeMintWrapped = (event) => {
  //   setMintWrappedValue(event.target.value);
  //   console.log(`mint wrapped: ${event.target.value}`);
  // };


  let wrapper;
  let vault;
  let extra_account_meta_list;


  function getMintOriginalPublicKey(mintOriginalValue, programId) {
    let mintPublicKey = new PublicKey(mintOriginalValue);
    return mintPublicKey
  }

  function getWrapperPDA(mintOriginal, programId) {
    wrapper = PublicKey.findProgramAddressSync(
      [Buffer.from("wrapper"), mintOriginal.toBuffer(),],
      programId,
    )[0];
    return wrapper
  }

  function getVaultPDA(mintOriginal, programId) {
    vault = PublicKey.findProgramAddressSync(
      [Buffer.from("vault"), mintOriginal.toBuffer(),],
      programId,
    )[0];
    return vault
  }

  function getExtraAccountMetaListPDA(mintWrapped, programId) {
    extra_account_meta_list = PublicKey.findProgramAddressSync(
      [Buffer.from("extra-account-metas"), mintWrapped.publicKey.toBuffer(),],
      programId,
    )[0];
    return extra_account_meta_list
  }

  // mintWrapped temporary 
  const mintWrapped = Keypair.generate();




  const [error, setError] = useState("");

  const getProvider = () => {
    if (!wallet) return null;
    const connection = new Connection(network, opts.preflightCommitment);
    return new AnchorProvider(connection, wallet, opts.preflightCommitment);
  };



  function initialiseProgram() {

    setError("");
    if (!connected) {
      setError("Wallet is not connected.");
      return;
    }
    const provider = getProvider();
    if (!provider) {
      setError("Provider is not available.");
      return;
    }

    const program = new Program(idl, programID, provider);

    return program
  }




  // CREATE WRAPPER
  const createWrapper = async () => {

    let program = initialiseProgram();

    // try to prepare accounts
    try {

      let wrapperPDA = getWrapperPDA(mintOriginalValue, program.programId)
      let vaultPDA = getVaultPDA(mintOriginalValue, program.programId)
      let ExtraAccountMetaListPDA = getExtraAccountMetaListPDA(mintWrapped, program.programId)
      let mintOriginalKey = getMintOriginalPublicKey(mintOriginalValue);

      // try to send transaction
      try {
        let tx = await program.methods.newWrapper(
          "PONK", 
          "PONK", 
          "https://shdw-drive.genesysgo.net/6NtZ6mGHc7WKirqTSN692Vui9Cf1XHumGJjg5arNXD9k/PONK.json",
        )
        .accounts({
            payer: wallet.publicKey,
            mintWrapped: mintWrapped.publicKey,
            mintOriginal: mintOriginalKey,
            wrapper: wrapperPDA,
            vault: vaultPDA,
            extraAccountMetaList: ExtraAccountMetaListPDA,
            rent: SYSVAR_RENT_PUBKEY,
            associatedTokenProgram: ASSOCIATED_TOKEN_PROGRAM_ID,
            tokenProgram: TOKEN_2022_PROGRAM_ID,
            systemProgram: SystemProgram.programId,
        })
        .signers([mintWrapped])
        .rpc({
          commitment: 'finalized', 
          skipPreflight: true, 
        });

        console.log(tx);
  
      } catch(e) {
        console.log(e)
      }
    } catch (err) {
      console.error("Error creating wrapper accounts:", err);
      setError("Failed to create greeting account. Please try again.");
    }
  };





    // CREATE WRAPPER
    const updateWrapper = async () => {

      let program = initialiseProgram();
  
      // try to prepare accounts
      try {
        let wrapperPDA = getWrapperPDA(mintOriginalValue, program.programId)
  
        // try to send transaction
        try {
          let tx = await program.methods.updateWrapper(
            "BONK"
          )
          .accounts({
            payer: wallet.payer.publicKey,
            mintWrapped: mintWrapped.publicKey,
            mintOriginal: mintOriginalValue,
            wrapper: wrapperPDA,
            systemProgram: SystemProgram.programId,
            associatedTokenProgram: ASSOCIATED_TOKEN_PROGRAM_ID,
            tokenProgram: TOKEN_2022_PROGRAM_ID,
          })
          .signers([mintWrapped])
          .rpc({
            commitment: 'finalized', 
            skipPreflight: true, 
          });
  
          console.log(tx);
    
        } catch(e) {
          console.log(e)
        }
      } catch (err) {
        console.error("Error creating wrapper accounts:", err);
        setError("Failed to create greeting account. Please try again.");
      }
    };



  

  return (
    <div>
      <WalletMultiButton />
      <WalletDisconnectButton />

      {error && <p style={{ color: "red" }}>{error}</p>}


      <h1>Create new Wrapper</h1>

      <label>Name</label>
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />

      <label>Symbol</label>
      <input
        type="text"
        id="symbol"
        value={symbolValue}
        onChange={handleChangeSymbol}
      />
      <br />

      <label>URI</label>
      <input
        type="text"
        id="uri"
        value={uriValue}
        onChange={handleChangeUri}
      />
      <br />

      <br /><br />
      <button onClick={createWrapper}>Create wrapper</button>
  

      <h1>Update Wrapper Symbol</h1>

      <label>Symbol</label>
      <input
        type="text"
        id="symbol"
        value={symbolValue}
        onChange={handleChangeSymbol}
      />
      <br />

      <br /><br />
      <button onClick={updateWrapper}>Update wrapper symbol</button>



      <h1>Create Complete Token Policy</h1>

      <label>Daily spending limit</label>
      <input
        type="number"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />

      <br /><br />
      <button onClick={createWrapper}>Create Token Policy</button>




      <h1>Delete Token Policy</h1>
      <button onClick={createWrapper}>Delete Token Policy</button>




      <h1>Create Basic Token Policy</h1>
      <button onClick={createWrapper}>Create Basic Token Policy</button>



      <h1>Add Spend Limit to Token Policy</h1>

      <label>Daily spending limit</label>
      <input
        type="number"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />

      <br /><br />
      <button onClick={createWrapper}>Add Spend Limit to Token Policy</button>



      <h1>Remove daily spending limit from Token Policy</h1>
      <button onClick={createWrapper}>Remove spending limit from Token Policy</button>




      <h1>Create Complete Wallet Policy</h1>

      <label>Extra signer (1)</label>
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />

      <label>Extra signer (2)</label>
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br /><br />

      <label>List of allowed destination addresses</label>
      <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
            <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
            <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br /><br />

      <label>List of blocked destination addresses</label>
      <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
            <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
            <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br /><br />

      <label>Spending window start</label>
      <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <label>Spending window end</label>
      <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />    

      <br /><br />
      <button onClick={createWrapper}>Create Complete Wallet Policy</button>  


      <h1>Delete Wallet Policy</h1>
      <button onClick={createWrapper}>Delete Wallet Policy</button>


      <h1>Create Basic Wallet Policy</h1>
      <button onClick={createWrapper}>Create Basic Wallet Policy</button>


      <h1>Add signer (1) to Wallet Policy</h1>
      <label>Signer public key</label>
      <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <button onClick={createWrapper}>Add signer to Wallet Policy</button>


      <h1>Remove signer 1 from Wallet Policy</h1>
      <button onClick={createWrapper}>Remove signer1 from Wallet Policy</button>


      <h1>Add signer (2) to Wallet Policy</h1>
      <label>Signer public key</label>
      <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <button onClick={createWrapper}>Add signer to Wallet Policy</button>


      <h1>Remove signer 2 from Wallet Policy</h1>
      <button onClick={createWrapper}>Remove signer1 from Wallet Policy</button>      
  

      <h1>Add list of allowed destination addresses to Wallet Policy</h1>
      <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
            <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
            <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />
      <button onClick={createWrapper}>Add allow list to Wallet Policy</button>



      <h1>Remove one or more destination addresses from allowed list</h1>
      <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
            <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
            <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />
      <button onClick={createWrapper}>Remove from allow list</button>



      <h1>Add list of blocked destination addresses to Wallet Policy</h1>
      <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
            <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
            <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />
      <button onClick={createWrapper}>Add block list to Wallet Policy</button>



      <h1>Remove one or more destination addresses from block list</h1>
      <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
            <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
            <br />
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />
      <button onClick={createWrapper}>Remove from block list</button>




      <h1>Add spending window to Wallet Policy</h1>

      <label>Spending window start</label>
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />
      <label>Spending window end</label>
      <input
        type="text"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />    

      <br /><br />
      <button onClick={createWrapper}>Add spending window to Wallet Policy</button>   

      <h1>Remove spending window from Wallet Policy</h1>
      <button onClick={createWrapper}>Remove spending window from Wallet Policy</button>   




      <h1>Delete Wallet Policy</h1>
      <button onClick={createWrapper}>Wrap tokens</button>




      <h1>Wrap token</h1>

      <label>Amount</label>
      <input
        type="number"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />

      <br /><br />
      <button onClick={createWrapper}>Wrap tokens</button>



      <h1>Unwrap tokens</h1>

      <label>Amount</label>
      <input
        type="number"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />

      <br /><br />
      <button onClick={createWrapper}>Unwrap tokens</button>



      <h1>Wrap tokenl</h1>

      <label>Amount</label>
      <input
        type="number"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />
      <label>Destination</label>
      <input
        type="number"
        id="mintOriginalAddress"
        value={mintOriginalValue}
        onChange={handleChangeMintOriginal}
      />
      <br />

      <br /><br />
      <button onClick={createWrapper}>Transfer tokens</button>



    <br /><br /><br />

    </div>
  );
};

export default App;