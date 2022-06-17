package dev.kord.voice

import dev.kord.voice.entity.DiscordVoicePacketData
import dev.kord.voice.entity.Snowflake
import kotlinx.coroutines.flow.Flow

/**
 * A gateway bridge, mainly used for handling voice related stuff.
 */
public interface GatewayBridge {
    /**
     */
    public val events: Flow<DiscordVoicePacketData>

    /**
     */
    public suspend fun join(guildId: Snowflake, channelId: Snowflake, selfMute: Boolean, selfDeaf: Boolean)

    /**
     */
    public suspend fun leave(guildId: Snowflake)
}
