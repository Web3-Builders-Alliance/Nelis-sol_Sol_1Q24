import { Connection, Keypair, LAMPORTS_PER_SOL } from "@solana/web3.js"
import bs58 from 'bs58'
import wallet from "./dev-wallet.json"
const prompt = require('prompt-sync')()



// Generate a keypair
let kp = Keypair.generate()
console.log(`You've generated a new Solana wallet: ${kp.secretKey}`)


const connection = new Connection("https://api.devnet.solana.com");
const keypair = Keypair.fromSecretKey(new Uint8Array(wallet));

(async () => {
    try {
        // We're going to claim 2 devnet SOL tokens
        const txhash = await connection.requestAirdrop(keypair.publicKey, 2 * LAMPORTS_PER_SOL);
        console.log(`Success! Check out your TX here: https://explorer.solana.com/tx/${txhash}?cluster=devnet`);
    } catch(e) {
        console.error(`Oops, something went wrong: ${e}`)
    }
})();

