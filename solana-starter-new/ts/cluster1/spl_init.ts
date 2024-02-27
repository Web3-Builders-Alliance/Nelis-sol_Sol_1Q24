import { Keypair, Connection, Commitment } from "@solana/web3.js";
import { createMint } from '@solana/spl-token';
import * as bs58 from 'bs58';
import wallet from "./wba-wallet.json";


const keypair = Keypair.fromSecretKey(new Uint8Array(wallet));
console.log(keypair.publicKey);


//Create a Solana devnet connection
const commitment: Commitment = "confirmed";
const connection = new Connection("https://api.devnet.solana.com", commitment);

(async () => {
    try {
        // Start here
        const mint = createMint(connection, keypair, keypair.publicKey, keypair.publicKey, 6);
    } catch(error) {
        console.log(`Oops, something went wrong: ${error}`)
    }
})()
