package spl.cards.program

import android.util.Log
import com.solana.core.AccountMeta
import com.solana.core.PublicKey
import com.solana.core.TransactionInstruction
import com.solana.programs.AssociatedTokenProgram
import com.solana.programs.Program
import com.solana.programs.TokenProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.ArrayList

class SplCardProgram {
    companion object {
        const val PROGRAM_ADDRESS = "6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"
    }
}

//region PDAs
fun SplCardProgram.Companion.programWrapperPda(mintAddress: PublicKey) =
    PublicKey.findProgramAddress(
        listOf(
            "wrapper".toByteArray(Charsets.UTF_8),
            mintAddress.toByteArray()
        ), PublicKey(PROGRAM_ADDRESS)
    )

fun SplCardProgram.Companion.programVaultPda(mintAddress: PublicKey) =
    PublicKey.findProgramAddress(
        listOf(
            "vault".toByteArray(Charsets.UTF_8),
            mintAddress.toByteArray()
        ), PublicKey(PROGRAM_ADDRESS)
    )

fun SplCardProgram.Companion.programExtraAccountMetaPda(mintWrappedAddress: PublicKey) =
    PublicKey.findProgramAddress(
        listOf(
            "extra-account-metas".toByteArray(Charsets.UTF_8),
            mintWrappedAddress.toByteArray()
        ), PublicKey(PROGRAM_ADDRESS)
    )

fun SplCardProgram.Companion.programWalletPolicyPda(sender: PublicKey) =
    PublicKey.findProgramAddress(
        listOf(
            "wallet-policy".toByteArray(Charsets.UTF_8),
            sender.toByteArray()
        ), PublicKey(PROGRAM_ADDRESS)
    )

fun SplCardProgram.Companion.programTokenPolicyPda(sender: PublicKey, mintWrapped: PublicKey) =
    PublicKey.findProgramAddress(
        listOf(
            "token-policy".toByteArray(Charsets.UTF_8),
            sender.toByteArray(),
            mintWrapped.toByteArray(),
        ), PublicKey(PROGRAM_ADDRESS)
    )

fun SplCardProgram.Companion.programPayerAtAPda(sender: PublicKey, mint: PublicKey, tokenProgram: PublicKey) =
    PublicKey.findProgramAddress(
        listOf(
            sender.toByteArray(),
            tokenProgram.toByteArray(),
            mint.toByteArray(),
        ), AssociatedTokenProgram.SPL_ASSOCIATED_TOKEN_ACCOUNT_PROGRAM_ID
    )

fun SplCardProgram.Companion.programMintWrappedAPda(mint: PublicKey) =
    PublicKey.findProgramAddress(
        listOf(
            "mint-wrapped".toByteArray(Charsets.UTF_8),
            mint.toByteArray()
        ), PublicKey(PROGRAM_ADDRESS)
    )

fun SplCardProgram.Companion.findSPLTokenDestinationAddress(sender: PublicKey, mint: PublicKey, tokenProgram: PublicKey) =
    PublicKey.findProgramAddress(
        listOf(
            sender.toByteArray(),
            tokenProgram.toByteArray(),
            mint.toByteArray(),
        ), AssociatedTokenProgram.SPL_ASSOCIATED_TOKEN_ACCOUNT_PROGRAM_ID
    )


// Copied helper functions to specify tokenProgram
fun transferChecked(
    source: PublicKey,
    destination: PublicKey,
    amount: Long,
    decimals: Byte,
    owner: PublicKey,
    tokenMint: PublicKey,
    tokenProgram: PublicKey,
): TransactionInstruction {
    Log.d("HAHA", "transferChecked - called!")
    Log.d("HAHA", "source: $source")
    Log.d("HAHA", "destination: $destination")
    Log.d("HAHA", "amount: $amount")
    Log.d("HAHA", "decimals: $decimals")
    Log.d("HAHA", "owner: $owner")
    Log.d("HAHA", "tokenMint: $tokenMint")
    Log.d("HAHA", "tokenProgram: $tokenProgram")

    val keys: MutableList<AccountMeta> = ArrayList()
    keys.add(AccountMeta(source, false, true))
    // index 1 = token mint (https://docs.rs/spl-token/3.1.0/spl_token/instruction/enum.TokenInstruction.html#variant.TransferChecked)
    keys.add(AccountMeta(tokenMint, false, false))
    keys.add(AccountMeta(destination, false, true))
    keys.add(AccountMeta(owner, true, false))
    val transactionData = encodeTransferCheckedTokenInstructionData(
        amount,
        decimals
    )
    return Program.createTransactionInstruction(
        tokenProgram,
        keys,
        transactionData
    )
}

fun createTransferCheckedInstruction(
    source: PublicKey,
    mint: PublicKey,
    destination: PublicKey,
    owner: PublicKey,
    amount: Long,
    decimals: Byte,
    tokenProgram: PublicKey,
): TransactionInstruction {
    val keys: MutableList<AccountMeta> = ArrayList()
    keys.add(AccountMeta(source, false, true))
    keys.add(AccountMeta(mint, false, false))
    keys.add(AccountMeta(destination, false, true))
    keys.add(AccountMeta(owner, true, false))

    val transactionData = encodeTransferCheckedTokenInstructionData(
        amount,
        decimals
    )
    return Program.createTransactionInstruction(
        tokenProgram,
        keys,
        transactionData
    )
}

fun transfer(
    source: PublicKey,
    destination: PublicKey,
    amount: Long,
    owner: PublicKey,
    tokenProgram: PublicKey
): TransactionInstruction {
    val keys: MutableList<AccountMeta> = ArrayList()
    keys.add(AccountMeta(source, false, true))
    keys.add(AccountMeta(destination, false, true))
    keys.add(AccountMeta(owner, true, false))
    val transactionData = encodeTransferTokenInstructionData(
        amount
    )
    return Program.createTransactionInstruction(
        tokenProgram,
        keys,
        transactionData
    )
}

private const val TRANSFER_METHOD_ID = 3
private fun encodeTransferTokenInstructionData(amount: Long): ByteArray {
    val result = ByteBuffer.allocate(9)
    result.order(ByteOrder.LITTLE_ENDIAN)
    result.put(TRANSFER_METHOD_ID.toByte())
    result.putLong(amount)
    return result.array()
}

private const val TRANSFER_CHECKED_METHOD_ID = 12
private fun encodeTransferCheckedTokenInstructionData(amount: Long, decimals: Byte): ByteArray {
    val result = ByteBuffer.allocate(10)
    result.order(ByteOrder.LITTLE_ENDIAN)
    result.put(TRANSFER_CHECKED_METHOD_ID.toByte())
    result.putLong(amount)
    result.put(decimals)
    return result.array()
}