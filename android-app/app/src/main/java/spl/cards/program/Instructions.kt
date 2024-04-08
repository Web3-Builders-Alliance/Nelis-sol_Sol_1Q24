//
// Instructions
// Metaplex
//
// This code was generated locally by Funkatronics on 2024-04-07
//
@file:UseSerializers(PublicKeyAs32ByteSerializer::class)

package spl.cards.program

import android.util.Log
import com.metaplex.kborsh.Borsh
import com.metaplex.lib.serialization.serializers.solana.AnchorInstructionSerializer
import com.metaplex.lib.serialization.serializers.solana.PublicKeyAs32ByteSerializer
import com.solana.core.AccountMeta
import com.solana.core.PublicKey
import com.solana.core.TransactionInstruction
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlin.ULong
import kotlin.collections.List
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import spl.cards.app.repository.SolanaRepository
import spl.cards.app.util.Constants

object SplCardProgramInstructions {
    fun newFullWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey,
        signer1: PublicKey?,
        requiredSigner1: Boolean,
        signer2: PublicKey?,
        requiredSigner2: Boolean,
        allowList: List<PublicKey>,
        blockList: List<PublicKey>,
        spendingWindow: List<Long>?
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys, Borsh.encodeToByteArray(
                AnchorInstructionSerializer("new_full_wallet_policy"),
                Args_newFullWalletPolicy(
                    signer1, requiredSigner1, signer2, requiredSigner2,
                    allowList, blockList, spendingWindow
                )
            )
        )
    }

    fun newWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys, Borsh.encodeToByteArray(
                AnchorInstructionSerializer("new_wallet_policy"),
                Args_newWalletPolicy()
            )
        )
    }

    fun addSigner1ToWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey,
        signer1: PublicKey,
        required: Boolean
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys,
            Borsh.encodeToByteArray(
                AnchorInstructionSerializer("add_signer1to_wallet_policy"),
                Args_addSigner1ToWalletPolicy(signer1, required)
            )
        )
    }

    fun removeSigner1FromWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys,
            Borsh.encodeToByteArray(
                AnchorInstructionSerializer("remove_signer1from_wallet_policy"),
                Args_removeSigner1FromWalletPolicy()
            )
        )
    }

    fun addSigner2ToWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey,
        signer2: PublicKey,
        required: Boolean
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys,
            Borsh.encodeToByteArray(
                AnchorInstructionSerializer("add_signer2to_wallet_policy"),
                Args_addSigner2ToWalletPolicy(signer2, required)
            )
        )
    }

    fun removeSigner2FromWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys,
            Borsh.encodeToByteArray(
                AnchorInstructionSerializer("remove_signer2from_wallet_policy"),
                Args_removeSigner2FromWalletPolicy()
            )
        )
    }

    fun addAllowedPublickeysToWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey,
        allowedPubkeyList: List<PublicKey>
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys,
            Borsh.encodeToByteArray(
                AnchorInstructionSerializer("add_allowed_publickeys_to_wallet_policy"),
                Args_addAllowedPublickeysToWalletPolicy(allowedPubkeyList)
            )
        )
    }

    fun removeAllowedPublickeysFromWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey,
        removePubkeyList: List<PublicKey>
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys,
            Borsh.encodeToByteArray(
                AnchorInstructionSerializer("remove_allowed_publickeys_from_wallet_policy"),
                Args_removeAllowedPublickeysFromWalletPolicy(removePubkeyList)
            )
        )
    }

    fun addBlockedPublickeysToWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey,
        blockedPubkeyList: List<PublicKey>
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys,
            Borsh.encodeToByteArray(
                AnchorInstructionSerializer("add_blocked_publickeys_to_wallet_policy"),
                Args_addBlockedPublickeysToWalletPolicy(blockedPubkeyList)
            )
        )
    }

    fun removeBlockedPublickeysFromWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey,
        removePubkeyList: List<PublicKey>
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys,
            Borsh.encodeToByteArray(
                AnchorInstructionSerializer("remove_blocked_publickeys_from_wallet_policy"),
                Args_removeBlockedPublickeysFromWalletPolicy(removePubkeyList)
            )
        )
    }

    fun addSpendingWindowToWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey,
        spendingWindow: List<Long>
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys,
            Borsh.encodeToByteArray(
                AnchorInstructionSerializer("add_spending_window_to_wallet_policy"),
                Args_addSpendingWindowToWalletPolicy(spendingWindow)
            )
        )
    }

    fun removeSpendingWindowFromWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys,
            Borsh.encodeToByteArray(
                AnchorInstructionSerializer("remove_spending_window_from_wallet_policy"),
                Args_removeSpendingWindowFromWalletPolicy()
            )
        )
    }

    fun deleteWalletPolicy(
        payer: PublicKey,
        walletPolicy: PublicKey,
        systemProgram: PublicKey
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys, Borsh.encodeToByteArray(
                AnchorInstructionSerializer("delete_wallet_policy"),
                Args_deleteWalletPolicy()
            )
        )
    }

    fun newFullTokenPolicy(
        payer: PublicKey,
        mintWrapped: PublicKey,
        tokenPolicy: PublicKey,
        systemProgram: PublicKey,
        spendLimitAmount: ULong?
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(mintWrapped, false, false))
        keys.add(AccountMeta(tokenPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        Log.d("HAHA", "payer: $payer")
        Log.d("HAHA", "mintWrapped: $mintWrapped")
        Log.d("HAHA", "tokenPolicy: $tokenPolicy")
        Log.d("HAHA", "systemProgram: $systemProgram")
        Log.d("HAHA", "spendLimitAmount: $spendLimitAmount")

        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys, Borsh.encodeToByteArray(
                AnchorInstructionSerializer("new_full_token_policy"),
                Args_newFullTokenPolicy(spendLimitAmount)
            )
        )
    }

    fun newTokenPolicy(
        payer: PublicKey,
        mintWrapped: PublicKey,
        tokenPolicy: PublicKey,
        systemProgram: PublicKey
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(mintWrapped, false, false))
        keys.add(AccountMeta(tokenPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys, Borsh.encodeToByteArray(
                AnchorInstructionSerializer("new_token_policy"),
                Args_newTokenPolicy()
            )
        )
    }

    fun addSpendLimitToTokenPolicy(
        payer: PublicKey,
        mintWrapped: PublicKey,
        tokenPolicy: PublicKey,
        systemProgram: PublicKey,
        amount: ULong
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(mintWrapped, false, false))
        keys.add(AccountMeta(tokenPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys,
            Borsh.encodeToByteArray(
                AnchorInstructionSerializer("add_spend_limit_to_token_policy"),
                Args_addSpendLimitToTokenPolicy(amount)
            )
        )
    }

    fun removeSpendLimitFromTokenPolicy(
        payer: PublicKey,
        mintWrapped: PublicKey,
        tokenPolicy: PublicKey,
        systemProgram: PublicKey
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(mintWrapped, false, false))
        keys.add(AccountMeta(tokenPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys,
            Borsh.encodeToByteArray(
                AnchorInstructionSerializer("remove_spend_limit_from_token_policy"),
                Args_removeSpendLimitFromTokenPolicy()
            )
        )
    }

    fun deleteTokenPolicy(
        payer: PublicKey,
        mintWrapped: PublicKey,
        tokenPolicy: PublicKey,
        systemProgram: PublicKey
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(mintWrapped, false, false))
        keys.add(AccountMeta(tokenPolicy, false, true))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys, Borsh.encodeToByteArray(
                AnchorInstructionSerializer("delete_token_policy"),
                Args_deleteTokenPolicy()
            )
        )
    }

    fun newWrapper(
        payer: PublicKey,
        mintWrapped: PublicKey,
        mintOriginal: PublicKey,
        wrapper: PublicKey,
        vault: PublicKey,
        extraAccountMetaList: PublicKey,
        rent: PublicKey,
        associatedTokenProgram: PublicKey,
        tokenProgram: PublicKey,
        tokenExtensionsProgram: PublicKey,
        systemProgram: PublicKey,
        name: String,
        symbol: String,
        uri: String
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(mintWrapped, true, true))
        keys.add(AccountMeta(mintOriginal, false, true))
        keys.add(AccountMeta(wrapper, false, true))
        keys.add(AccountMeta(vault, false, true))
        keys.add(AccountMeta(extraAccountMetaList, false, true))
        keys.add(AccountMeta(rent, false, false))
        keys.add(AccountMeta(associatedTokenProgram, false, false))
        keys.add(AccountMeta(tokenProgram, false, false))
        keys.add(AccountMeta(tokenExtensionsProgram, false, false))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys, Borsh.encodeToByteArray(
                AnchorInstructionSerializer("new_wrapper"),
                Args_newWrapper(name, symbol, uri)
            )
        )
    }

    fun updateWrapper(
        payer: PublicKey,
        mintWrapped: PublicKey,
        mintOriginal: PublicKey,
        wrapper: PublicKey,
        associatedTokenProgram: PublicKey,
        tokenProgram: PublicKey,
        systemProgram: PublicKey,
        symbol: String
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(mintWrapped, true, true))
        keys.add(AccountMeta(mintOriginal, false, true))
        keys.add(AccountMeta(wrapper, false, true))
        keys.add(AccountMeta(associatedTokenProgram, false, false))
        keys.add(AccountMeta(tokenProgram, false, false))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys, Borsh.encodeToByteArray(
                AnchorInstructionSerializer("update_wrapper"),
                Args_updateWrapper(symbol)
            )
        )
    }

    fun wrapperClose(
        payer: PublicKey,
        mintOriginal: PublicKey,
        wrapper: PublicKey,
        associatedTokenProgram: PublicKey,
        tokenProgram: PublicKey,
        systemProgram: PublicKey
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(mintOriginal, false, true))
        keys.add(AccountMeta(wrapper, false, true))
        keys.add(AccountMeta(associatedTokenProgram, false, false))
        keys.add(AccountMeta(tokenProgram, false, false))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys, Borsh.encodeToByteArray(
                AnchorInstructionSerializer("wrapper_close"),
                Args_wrapperClose()
            )
        )
    }

    fun wrap(
        payer: PublicKey,
        payerAtaOriginal: PublicKey,
        payerAtaWrapped: PublicKey,
        mintOriginal: PublicKey,
        mintWrapped: PublicKey,
        wrapper: PublicKey,
        vault: PublicKey,
        associatedTokenProgram: PublicKey,
        tokenProgram: PublicKey,
        tokenExtensionsProgram: PublicKey,
        systemProgram: PublicKey,
        amount: ULong
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(payerAtaOriginal, false, true))
        keys.add(AccountMeta(payerAtaWrapped, false, true))
        keys.add(AccountMeta(mintOriginal, false, false))
        keys.add(AccountMeta(mintWrapped, false, true))
        keys.add(AccountMeta(wrapper, false, false))
        keys.add(AccountMeta(vault, false, true))
        keys.add(AccountMeta(associatedTokenProgram, false, false))
        keys.add(AccountMeta(tokenProgram, false, false))
        keys.add(AccountMeta(tokenExtensionsProgram, false, false))
        keys.add(AccountMeta(systemProgram, false, false))
        Log.d("HAHA", "payer: $payer")
        Log.d("HAHA", "payerAtaOriginal: $payerAtaOriginal")
        Log.d("HAHA", "payerAtaWrapped: $payerAtaWrapped")
        Log.d("HAHA", "mintOriginal: $mintOriginal")
        Log.d("HAHA", "mintWrapped: $mintWrapped")
        Log.d("HAHA", "wrapper: $wrapper")
        Log.d("HAHA", "vault: $vault")
        Log.d("HAHA", "associatedTokenProgram: $associatedTokenProgram")
        Log.d("HAHA", "tokenProgram: $tokenProgram")
        Log.d("HAHA", "tokenExtensionsProgram: $tokenExtensionsProgram")
        Log.d("HAHA", "systemProgram: $systemProgram")
        Log.d("HAHA", "amount: $amount")
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys, Borsh.encodeToByteArray(
                AnchorInstructionSerializer("wrap"),
                Args_wrap(amount)
            )
        )
    }

    fun unwrap(
        payer: PublicKey,
        payerAtaOriginal: PublicKey,
        payerAtaWrapped: PublicKey,
        mintOriginal: PublicKey,
        mintWrapped: PublicKey,
        wrapper: PublicKey,
        vault: PublicKey,
        associatedTokenProgram: PublicKey,
        tokenProgram: PublicKey,
        tokenExtensionsProgram: PublicKey,
        systemProgram: PublicKey,
        amount: ULong
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(payer, true, true))
        keys.add(AccountMeta(payerAtaOriginal, false, true))
        keys.add(AccountMeta(payerAtaWrapped, false, true))
        keys.add(AccountMeta(mintOriginal, false, false))
        keys.add(AccountMeta(mintWrapped, false, false))
        keys.add(AccountMeta(wrapper, false, false))
        keys.add(AccountMeta(vault, false, true))
        keys.add(AccountMeta(associatedTokenProgram, false, false))
        keys.add(AccountMeta(tokenProgram, false, false))
        keys.add(AccountMeta(tokenExtensionsProgram, false, false))
        keys.add(AccountMeta(systemProgram, false, false))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys, Borsh.encodeToByteArray(
                AnchorInstructionSerializer("unwrap"),
                Args_unwrap(amount)
            )
        )
    }

    fun transferHook(
        sourceToken: PublicKey,
        mint: PublicKey,
        destinationToken: PublicKey,
        owner: PublicKey,
        extraAccountMetaList: PublicKey,
        walletPolicy: PublicKey,
        tokenPolicy: PublicKey,
        amount: ULong
    ): TransactionInstruction {
        val keys = mutableListOf<AccountMeta>()
        keys.add(AccountMeta(sourceToken, false, false))
        keys.add(AccountMeta(mint, false, false))
        keys.add(AccountMeta(destinationToken, false, false))
        keys.add(AccountMeta(owner, false, false))
        keys.add(AccountMeta(extraAccountMetaList, false, false))
        keys.add(AccountMeta(walletPolicy, false, true))
        keys.add(AccountMeta(tokenPolicy, false, true))
        return TransactionInstruction(
            PublicKey("6jPXVk78mLJq3MAz24gasxmYmV2f3bYDd5Rp5zK92tew"),
            keys, Borsh.encodeToByteArray(
                AnchorInstructionSerializer("transfer_hook"),
                Args_transferHook(amount)
            )
        )
    }

    @Serializable
    class Args_newFullWalletPolicy(
        @Serializable(with = PublicKeyAs32ByteSerializer::class) val signer1: PublicKey?,
        val requiredSigner1: Boolean,
        @Serializable(with = PublicKeyAs32ByteSerializer::class) val signer2: PublicKey?,
        val requiredSigner2: Boolean,
        @Serializable(with = PublicKeyListSerializer::class) val allowList: List<PublicKey>,
        @Serializable(with = PublicKeyListSerializer::class) val blockList: List<PublicKey>,
        val spendingWindow: List<Long>?
    )

    @Serializable
    class Args_newWalletPolicy()

    @Serializable
    class Args_addSigner1ToWalletPolicy(
        @Serializable(with = PublicKeyAs32ByteSerializer::class) val
        signer1: PublicKey, val required: Boolean
    )

    @Serializable
    class Args_removeSigner1FromWalletPolicy()

    @Serializable
    class Args_addSigner2ToWalletPolicy(
        @Serializable(with = PublicKeyAs32ByteSerializer::class) val
        signer2: PublicKey, val required: Boolean
    )

    @Serializable
    class Args_removeSigner2FromWalletPolicy()

    @Serializable
    class Args_addAllowedPublickeysToWalletPolicy(
        @Serializable(
            with =
            PublicKeyListSerializer::class
        ) val allowedPubkeyList: List<PublicKey>
    )

    @Serializable
    class Args_removeAllowedPublickeysFromWalletPolicy(
        @Serializable(
            with =
            PublicKeyListSerializer::class
        ) val removePubkeyList: List<PublicKey>
    )

    @Serializable
    class Args_addBlockedPublickeysToWalletPolicy(
        @Serializable(
            with =
            PublicKeyListSerializer::class
        ) val blockedPubkeyList: List<PublicKey>
    )

    @Serializable
    class Args_removeBlockedPublickeysFromWalletPolicy(
        @Serializable(
            with =
            PublicKeyListSerializer::class
        ) val removePubkeyList: List<PublicKey>
    )

    @Serializable
    class Args_addSpendingWindowToWalletPolicy(val spendingWindow: List<Long>)

    @Serializable
    class Args_removeSpendingWindowFromWalletPolicy()

    @Serializable
    class Args_deleteWalletPolicy()

    @Serializable
    class Args_newFullTokenPolicy(val spendLimitAmount: ULong?)

    @Serializable
    class Args_newTokenPolicy()

    @Serializable
    class Args_addSpendLimitToTokenPolicy(val amount: ULong)

    @Serializable
    class Args_removeSpendLimitFromTokenPolicy()

    @Serializable
    class Args_deleteTokenPolicy()

    @Serializable
    class Args_newWrapper(
        val name: String,
        val symbol: String,
        val uri: String
    )

    @Serializable
    class Args_updateWrapper(val symbol: String)

    @Serializable
    class Args_wrapperClose()

    @Serializable
    class Args_wrap(val amount: ULong)

    @Serializable
    class Args_unwrap(val amount: ULong)

    @Serializable
    class Args_transferHook(val amount: ULong)
}
