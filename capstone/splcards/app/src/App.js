import { useState } from "react";
import { useAnchorWallet } from "@solana/wallet-adapter-react";
import { Connection, PublicKey, SystemProgram, Keypair } from "@solana/web3.js";
import { Program, AnchorProvider } from "@project-serum/anchor";
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
const network = "http://127.0.0.1:8899"; // Adjust for your environment: local, devnet, or mainnet-beta
const opts = { preflightCommitment: "confirmed" };

const App = () => {

  const wallet = useAnchorWallet();

  const { connected } = useWallet();

  const [greetingAccountPublicKey, setGreetingAccountPublicKey] =
    useState(null);

  const [error, setError] = useState("");

  const getProvider = () => {
    if (!wallet) return null;
    const connection = new Connection(network, opts.preflightCommitment);
    return new AnchorProvider(connection, wallet, opts.preflightCommitment);
  };



  const createWrapper = async () => {
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

    try {

      const mintWrapped = Keypair.generate();
      const mintOriginal = new PublicKey("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");

      const wrapper = PublicKey.findProgramAddressSync(
        [
          Buffer.from("wrapper"),
          mintOriginal.toBuffer(),
        ],
        program.programId,
      )[0];

      const vault = PublicKey.findProgramAddressSync(
        [
          Buffer.from("vault"),
          mintOriginal.publicKey.toBuffer(),
        ],
        program.programId,
      )[0];

      const extra_account_meta_list = PublicKey.findProgramAddressSync(
        [
          Buffer.from("extra-account-metas"),
          mintWrapped.publicKey.toBuffer(),
        ],
        program.programId,
      )[0];


      await program.createWrapper(
        "USDC", 
        "USDC", 
        "https://www.example.com/wrapper.json",
        {
        accounts: {
          payer: wallet.payer.publicKey,
          mintWrapped: mintWrapped.publicKey,
          mintOriginal: mintOriginal,
          wrapper: wrapper,
          vault: vault,
          extraAccountMetaList: extra_account_meta_list,
          rent: SYSVAR_RENT_PUBKEY,
          systemProgram: anchor.web3.SystemProgram.programId,
          associatedTokenProgram: ASSOCIATED_TOKEN_PROGRAM_ID,
          tokenProgram: TOKEN_2022_PROGRAM_ID,
        },
        signers: [wallet.payer, mintWrapped],
        skipPreflight: true,
      });

      console.log("USDC wrapper created!");

      setGreetingAccountPublicKey(greetingAccount.publicKey.toString());




    } catch (err) {
      console.error("Error creating greeting account:", err);
      setError("Failed to create greeting account. Please try again.");
    }
  };



  const incrementGreeting = async () => {
    setError("");
    if (!connected) {
      setError("Wallet is not connected.");
      return;
    }
    if (!greetingAccountPublicKey) {
      setError("Greeting account not created or public key not set.");
      return;
    }
    const provider = getProvider();
    if (!provider) {
      setError("Provider is not available.");
      return;
    }
    const program = new Program(idl, programID, provider);
    try {
      await program.rpc.incrementGreeting({
        accounts: {
          greetingAccount: new PublicKey(greetingAccountPublicKey),
          user: provider.wallet.publicKey,
        },
        signers: [],
      });
      console.log("Greeting incremented!");
    } catch (err) {
      console.error("Error incrementing greeting:", err);
      setError("Failed to increment greeting. Please try again.");
    }
  };

  

  return (
    <div>
      <WalletMultiButton />
      <WalletDisconnectButton />
      <button onClick={createWrapper}>Create Greeting</button>
      {greetingAccountPublicKey && (
        <button onClick={incrementGreeting}>Increment Greeting</button>
      )}
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
};

export default App;