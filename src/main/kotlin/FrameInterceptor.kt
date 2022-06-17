package dev.kord.voice

import dev.kord.voice.gateway.VoiceGateway
import kotlinx.coroutines.flow.Flow

public data class FrameInterceptorConfiguration(
    val gateway: GatewayBridge,
    val voiceGateway: VoiceGateway,
    val ssrc: UInt
)

/**
 * An interceptor for audio frames before they are sent as packets.
 *
 * @see DefaultFrameInterceptor
 */
public fun interface FrameInterceptor {
    public fun Flow<AudioFrame?>.intercept(configuration: FrameInterceptorConfiguration): Flow<AudioFrame?>
}
