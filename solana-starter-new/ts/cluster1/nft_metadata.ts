import wallet from "./wba-wallet.json"
import { createUmi } from "@metaplex-foundation/umi-bundle-defaults"
import { createGenericFile, createSignerFromKeypair, signerIdentity } from "@metaplex-foundation/umi"
import { createBundlrUploader } from "@metaplex-foundation/umi-uploader-bundlr"

// Create a devnet connection
const umi = createUmi('https://api.devnet.solana.com');
const bundlrUploader = createBundlrUploader(umi);

let keypair = umi.eddsa.createKeypairFromSecretKey(new Uint8Array(wallet));
const signer = createSignerFromKeypair(umi, keypair);

umi.use(signerIdentity(signer));

(async () => {
    try {
        // Follow this JSON structure
        // https://docs.metaplex.com/programs/token-metadata/changelog/v1.0#json-structure

        const image = "https://arweave.net/Lvf_0qmSXrokNAGOmwTwbGKtLSlrKO79DwaMWqqcut4";
        const metadata = {
            name: "Rug me",
            symbol: "RUG",
            description: "A rug that keeps on rugging.",
            image,
            attributes: [
                {trait_type: 'Wen stop rugging', value: 'never'}
            ],
            properties: {
                files: [
                    {
                        type: "image/png",
                        uri: "https://arweave.net/Lvf_0qmSXrokNAGOmwTwbGKtLSlrKO79DwaMWqqcut4"
                    },
                ]
            },
            creators: [{address: keypair.publicKey, share: 100}]
        };
        const myUri = await bundlrUploader.uploadJson([metadata])
        console.log("Your image URI: ", myUri);
    }
    catch(error) {
        console.log("Oops.. Something went wrong", error);
    }
})();