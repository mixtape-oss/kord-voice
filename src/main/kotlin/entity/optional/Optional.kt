package dev.kord.voice.entity.optional

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Optional.Serializer::class)
public sealed class Optional<out T> {
    public open val value: T?
        get() = throw UnsupportedOperationException("This is implemented in implementation classes")

    public class Missing<out T> private constructor() : Optional<T>() {
        override val value: T?
            get() = null

        override fun toString(): String = "Optional.Missing"

        override fun equals(other: Any?): Boolean {
            return other is Missing<*>
        }

        override fun hashCode(): Int = 0

        public companion object {
            private val constantNull = Missing<Nothing>()

            public operator fun <T : Any> invoke(): Missing<T> = constantNull
        }
    }

    public class Null<out T> private constructor() : Optional<T?>() {
        override val value: T?
            get() = null

        override fun toString(): String = "Optional.Null"

        override fun equals(other: Any?): Boolean {
            return other is Null<*>
        }

        override fun hashCode(): Int = 0

        public companion object {
            private val constantNull = Null<Nothing>()

            public operator fun <T : Any> invoke(): Null<T> = constantNull
        }
    }

    public class Value<T : Any>(override val value: T) : Optional<T>() {
        override fun toString(): String = "Optional.Something(content=$value)"

        public operator fun component1(): T = value

        override fun equals(other: Any?): Boolean {
            val value = other as? Value<*> ?: return false
            return value.value == this.value
        }

        override fun hashCode(): Int = value.hashCode()
    }

    public companion object {

        public fun <T, C : Collection<T>> missingOnEmpty(value: C): Optional<C> =
            if (value.isEmpty()) Missing()
            else Value(value)

        public operator fun <T : Any> invoke(): Missing<T> = Missing()

        public operator fun <T : Any> invoke(value: T): Value<T> = Value(value)

        @JvmName("invokeNullable")
        public operator fun <T : Any> invoke(value: T?): Optional<T?> = when (value) {
            null -> Null()
            else -> Value(value)
        }
    }

    internal class Serializer<T>(private val contentSerializer: KSerializer<T>) : KSerializer<Optional<T>> {
        override val descriptor: SerialDescriptor = contentSerializer.descriptor

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): Optional<T> {
            if (!descriptor.isNullable && !decoder.decodeNotNullMark()) {
                throw SerializationException("descriptor for ${descriptor.serialName} was not nullable but null mark was encountered")
            }

            val optional: Optional<T?> = when {
                !decoder.decodeNotNullMark() -> {
                    decoder.decodeNull()
                    Null<Nothing>()
                }

                else -> Optional(decoder.decodeSerializableValue(contentSerializer))
            }

            @Suppress("UNCHECKED_CAST")
            return optional as Optional<T>
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: Optional<T>) = when (value) {
            is Missing<*> -> throw SerializationException("missing values cannot be serialized")
            is Null<*> -> encoder.encodeNull()
            is Value -> encoder.encodeSerializableValue(contentSerializer, value.value)
        }
    }
}
