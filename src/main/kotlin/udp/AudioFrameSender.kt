@file:Suppress("ArrayInDataClass")

package dev.kord.voice.udp

import dev.kord.voice.AudioProvider
import dev.kord.voice.FrameInterceptorConfiguration
import dev.kord.voice.encryption.strategies.NonceStrategy
import io.ktor.network.sockets.*
import io.ktor.util.network.*

public data class AudioFrameSenderConfiguration(
    val server: SocketAddress,
    val ssrc: UInt,
    val key: ByteArray,
    val interceptorConfiguration: FrameInterceptorConfiguration,
    val nonceStrategy: NonceStrategy,
    val audioProvider: AudioProvider
)

public interface AudioFrameSender {
    /**
     * This should start polling frames from [the audio provider][AudioFrameSenderConfiguration.provider] and
     * send them to Discord.
     */
    public suspend fun start(configuration: AudioFrameSenderConfiguration)
}
