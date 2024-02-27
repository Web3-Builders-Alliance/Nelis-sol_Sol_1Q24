import { createUmi } from "@metaplex-foundation/umi-bundle-defaults"
import { createSignerFromKeypair, signerIdentity, generateSigner, percentAmount } from "@metaplex-foundation/umi"
import { createNft, mplTokenMetadata } from "@metaplex-foundation/mpl-token-metadata";

import wallet from "./wba-wallet.json"
import base58 from "bs58";

const RPC_ENDPOINT = "https://api.devnet.solana.com";
const umi = createUmi(RPC_ENDPOINT);

let keypair = umi.eddsa.createKeypairFromSecretKey(new Uint8Array(wallet));
const myKeypairSigner = createSignerFromKeypair(umi, keypair);
umi.use(signerIdentity(myKeypairSigner));
umi.use(mplTokenMetadata())

const mint = generateSigner(umi);

(async () => {
    const image = "https://arweave.net/Lvf_0qmSXrokNAGOmwTwbGKtLSlrKO79DwaMWqqcut4";
    const uri = "https://yer4pc2u6i4uscvwray4ihzhwuykbhqxgy3yf4qjxbdmut2ftbna.arweave.net/wSPHi1TyOUkKtogxxB8ntTCgnhc2N4LyCbhGyk9FmFo";

    let tx = createNft(umi, {
        mint,
        name: "Rug me",
        symbol: "RUG",
        uri,
        sellerFeeBasisPoints: percentAmount(5),
    })
    let result = await tx.sendAndConfirm(umi);
    const signature = base58.encode(result.signature);
    
    console.log(`Succesfully Minted! Check out your TX here:\nhttps://explorer.solana.com/tx/${signature}?cluster=devnet`)

    console.log("Mint Address: ", mint.publicKey);
})();