package dev.kord.voice.entity.optional

import dev.kord.voice.entity.Snowflake
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represents a value that encapsulate a [Snowflake]'s
 * [optional and value state in the Discord API](https://discord.com/developers/docs/reference#nullable-and-optional-resource-fields).
 *
 * Specifically:
 *
 * * [Missing] - a [Snowflake] field that was not present in the serialized entity.
 * * [Value] - a [Snowflake] field that was assigned a non-null value in the serialized entity.
 *
 * > Note that there is no nullable variant present. Use `Snowflake?` or `OptionalSnowflake?` for this case instead.
 *
 * The base class is (de)serializable with kotlinx.serialization.
 *
 * Note that kotlinx.serialization does **not** call serializers for values that are not
 * present in the serialized format. `Optional` fields should have a default value of `OptionalSnowflake.Missing`:
 *
 * ```kotlin
 * @Serializable
 * class DiscordUser(
 *     val id: Long,
 *     val username: String,
 *     val bot: OptionalSnowflake = OptionalSnowflake.Missing
 * )
 * ```
 */
@Serializable(with = OptionalSnowflake.Serializer::class)
public sealed class OptionalSnowflake {

    public open val value: Snowflake?
        get() = when (this) {
            Missing -> null
            is Value -> value
        }

    public object Missing : OptionalSnowflake() {
        override fun toString(): String = "OptionalSnowflake.Missing"
    }

    public class Value(private val uLongValue: ULong) : OptionalSnowflake() {

        public constructor(value: Snowflake) : this(value.value)

        override val value: Snowflake get() = Snowflake(uLongValue)

        public operator fun component1(): Snowflake = value

        override fun toString(): String = "OptionalSnowflake.Value(snowflake=$value)"

        override fun equals(other: Any?): Boolean {
            val value = other as? Value ?: return false
            return value.value == this.value
        }

        override fun hashCode(): Int = value.hashCode()
    }

    @OptIn(ExperimentalSerializationApi::class)
    internal object Serializer : KSerializer<OptionalSnowflake> {
        override val descriptor: SerialDescriptor =
            @OptIn(ExperimentalUnsignedTypes::class) ULong.serializer().descriptor

        override fun deserialize(decoder: Decoder): OptionalSnowflake =
            Value(decoder.decodeInline(descriptor).decodeLong().toULong())

        override fun serialize(encoder: Encoder, value: OptionalSnowflake) = when (value) {
            Missing -> Unit // ignore value
            is Value -> encoder.encodeInline(descriptor).encodeLong(value.value.value.toLong())
        }
    }
}

public val OptionalSnowflake?.value: Snowflake?
    get() = when (this) {
        is OptionalSnowflake.Value -> value
        OptionalSnowflake.Missing, null -> null
    }
