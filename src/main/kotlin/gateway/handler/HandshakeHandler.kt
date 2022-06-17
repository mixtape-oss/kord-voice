package dev.kord.voice.gateway.handler

import dev.kord.voice.gateway.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

internal class HandshakeHandler(
    flow: Flow<VoiceEvent>,
    private val data: DefaultVoiceGatewayData,
    private val send: suspend (Command) -> Unit
) : GatewayEventHandler(flow, "HandshakeHandler") {
    lateinit var configuration: VoiceGatewayConfiguration
    private var resumable: Boolean = false

    private val identify
        get() = Identify(
            data.guildId,
            data.selfId,
            data.sessionId,
            configuration.token
        )
    private val resume
        get() = Resume(
            serverId = data.guildId,
            sessionId = data.sessionId,
            token = configuration.token
        )

    override suspend fun start() = coroutineScope {
        on<Hello> {
            send(if (resumable) resume else identify)
        }


        on<Ready> {
            resumable = true
        }

        on<Resumed> {
            data.reconnectRetry.reset()
        }
    }
}
