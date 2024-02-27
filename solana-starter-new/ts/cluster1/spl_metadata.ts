import wallet from "./wba-wallet.json"
import { createUmi } from "@metaplex-foundation/umi-bundle-defaults"
import { 
    createMetadataAccountV3, 
    CreateMetadataAccountV3InstructionAccounts, 
    CreateMetadataAccountV3InstructionArgs,
    DataV2Args,
    MPL_TOKEN_METADATA_PROGRAM_ID
} from "@metaplex-foundation/mpl-token-metadata";
import { createSignerFromKeypair, signerIdentity, publicKey, publicKeyBytes } from "@metaplex-foundation/umi";

// Define our Mint address
const mint = publicKey("5znEazS1JF5F5CiGbPQS7JLLqjUSVHcQGaUkoYkCjVrU");

// Create a UMI connection
const umi = createUmi('https://api.devnet.solana.com');
const keypair = umi.eddsa.createKeypairFromSecretKey(new Uint8Array(wallet));
const signer = createSignerFromKeypair(umi, keypair);
umi.use(signerIdentity(createSignerFromKeypair(umi, keypair)));


const metadata = umi.eddsa.findPda(MPL_TOKEN_METADATA_PROGRAM_ID, [
    Buffer.from("metadata"),
    publicKeyBytes(MPL_TOKEN_METADATA_PROGRAM_ID),
    publicKeyBytes(mint),
]);


(async () => {
    try {
        // Start here

        let accounts: CreateMetadataAccountV3InstructionAccounts = {
            mint,
            metadata,
            mintAuthority: signer,
            payer: signer,
            updateAuthority: signer,

        }

        let data: DataV2Args = {
            name: "WBA Token",
            symbol: "WBA",
            uri: "", // mostly used for NFT's, for example to display an image
            sellerFeeBasisPoints: 300,
            creators: null,
            collection: null,
            uses: null, // for programmable nft's
        }

        let args: CreateMetadataAccountV3InstructionArgs = {
            data,
            isMutable: true,
            collectionDetails: null,
        }

        let tx = createMetadataAccountV3(
            umi,
            {
                ...accounts,
                ...args
            }
        )

        let result = await tx.sendAndConfirm(umi).then(r => r.signature.toString());
        console.log(result);

        // result: 90,181,34,237,216,170,209,34,31,241,71,133,224,202,232,189,162,251,27,96,239,134,203,179,173,40,152,165,14,158,144,8,138,101,77,219,239,28,222,157,56,49,91,161,245,202,220,61,121,55,54,20,120,4,206,0,46,30,99,26,88,60,37,13
    } catch(e) {
        console.error(`Oops, something went wrong: ${e}`)
    }
})();