import * as anchor from "@coral-xyz/anchor";
import { Program } from "@coral-xyz/anchor";
import {
  Keypair,
  PublicKey,
  LAMPORTS_PER_SOL,
  SystemProgram,
} from "@solana/web3.js";
import { Vault2024 } from "../target/types/vault_2024";

const program = anchor.workspace.Vault2024 as Program<Vault2024>;

const connection = anchor.getProvider().connection;

const maker = Keypair.generate();
const taker = Keypair.generate();
const seed = new anchor.BN(1);

const vault = PublicKey.findProgramAddressSync(
  [
    Buffer.from("vault"),
    seed.toBuffer("le", 8),
    maker.publicKey.toBuffer(),
    taker.publicKey.toBuffer(),
  ],
  program.programId
)[0];

const confirm = async (signature: string): Promise<string> => {
  const block = await connection.getLatestBlockhash();
  await connection.confirmTransaction({
    signature,
    ...block,
  });
  return signature;
};

const log = async (signature: string): Promise<string> => {
  console.log(
    `Your transaction signature: https://explorer.solana.com/transaction/${signature}?cluster=custom&customUrl=${connection.rpcEndpoint}`
  );
  return signature;
};

it("Airdrop", async () => {
  await connection
    .requestAirdrop(maker.publicKey, LAMPORTS_PER_SOL * 10)
    .then(confirm)
    .then(log);
  await connection
    .requestAirdrop(taker.publicKey, LAMPORTS_PER_SOL * 10)
    .then(confirm)
    .then(log);
});

describe("vault-2024", () => {
  // Configure the client to use the local cluster.
  anchor.setProvider(anchor.AnchorProvider.env());

  const program = anchor.workspace.Vault2024 as Program<Vault2024>;

  it("Deposit SOL into Vault!", async () => {

    let balance_before_deposit = await program.provider.connection.getBalance(maker.publicKey);
    console.log(`Maker balance before deposit: ${balance_before_deposit}`);

    // Add your test here.
    const tx = await program.methods
      .deposit(seed, new anchor.BN(1 * LAMPORTS_PER_SOL))
      .accounts({
        maker: maker.publicKey,
        taker: taker.publicKey,
        vault,
        systemProgram: SystemProgram.programId,
      })
      .signers([maker])
      .rpc()
      .then(confirm)
      .then(log);

      const vault_log = await program.account.vault.fetch(vault);
      console.log(`Vault seed: ${vault_log.seed}, created at: ${vault_log.createdAt}`);

      let balance_after_deposit = await program.provider.connection.getBalance(maker.publicKey);
      console.log(`Maker balance after deposit: ${balance_after_deposit}`);

  });

  it("Cancel test", async () => {

    const tx = await program.methods
      .cancel()
      .accounts({
        maker: maker.publicKey,
        vault,
        systemProgram: SystemProgram.programId,
      })
      .signers([maker])
      .rpc()
      .then(confirm)
      .then(log);

      let balance_after_cancel = await program.provider.connection.getBalance(maker.publicKey);
      console.log(`Maker balance after cancel: ${balance_after_cancel}`);

  });

  it("Deposit SOL into Vault!", async () => {

    let balance_before_deposit = await program.provider.connection.getBalance(taker.publicKey);
    console.log(`Taker balance before deposit: ${balance_before_deposit}`);


    // Add your test here.
    const tx = await program.methods
      .deposit(seed, new anchor.BN(1 * LAMPORTS_PER_SOL))
      .accounts({
        maker: maker.publicKey,
        taker: taker.publicKey,
        vault,
        systemProgram: SystemProgram.programId,
      })
      .signers([maker])
      .rpc()
      .then(confirm)
      .then(log);

      const vault_log = await program.account.vault.fetch(vault);
      console.log(`Vault seed: ${vault_log.seed}, created at: ${vault_log.createdAt}`);

      let balance_after_deposit = await program.provider.connection.getBalance(taker.publicKey);
      console.log(`Taker balance after deposit: ${balance_after_deposit}`);

  });

  it("Claim test", async () => {

    const tx = await program.methods
      .claim()
      .accounts({
        taker: taker.publicKey,
        vault,
        systemProgram: SystemProgram.programId,
      })
      .signers([taker])
      .rpc()
      .then(confirm)
      .then(log);

      let balance_after_claim = await program.provider.connection.getBalance(taker.publicKey);
      console.log(`Taker balance after cancel: ${balance_after_claim}`);

  });

});



