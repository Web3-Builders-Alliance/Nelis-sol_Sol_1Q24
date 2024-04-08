//
// Accounts
// Metaplex
//
// This code was generated locally by Funkatronics on 2024-04-07
//
@file:UseSerializers(PublicKeyAs32ByteSerializer::class)

package spl.cards.program

import com.metaplex.lib.serialization.serializers.solana.PublicKeyAs32ByteSerializer
import com.solana.core.PublicKey
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlin.UByte
import kotlin.ULong
import kotlin.collections.List
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class TokenPolicyState(
    val authority: PublicKey,
    val mint: PublicKey,
    val spentLast24: List<Long>,
    val spendLimitAmount: ULong?,
    val bump: UByte
)

@Serializable
class WalletPolicyState(
    val authority: PublicKey,
    val signer1: PublicKey?,
    val requiredSigner1: Boolean,
    val signer2: PublicKey?,
    val requiredSigner2: Boolean,
    val allowList: List<PublicKey>,
    val blockList: List<PublicKey>,
    val spendingWindow: List<Long>?,
    val bump: UByte
)

@Serializable
class WrapperState(
    val symbol: String,
    val mintOriginal: PublicKey,
    val mintWrapped: PublicKey,
    val vault: PublicKey,
    val bump: UByte
)
