package spl.cards.program

import com.solana.core.PublicKey
import com.solana.networking.serialization.serializers.solana.PublicKeyAs32ByteSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor

object PublicKeyListSerializer : KSerializer<List<PublicKey>> {

    private val delegateSerializer = ListSerializer(PublicKeyAs32ByteSerializer)

    override val descriptor: SerialDescriptor get() = delegateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: List<PublicKey>) {
        encoder.encodeSerializableValue(delegateSerializer, value)
    }

    override fun deserialize(decoder: Decoder): List<PublicKey> {
        return decoder.decodeSerializableValue(delegateSerializer)
    }
}