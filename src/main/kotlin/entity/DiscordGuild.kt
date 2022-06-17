package dev.kord.voice.entity

import dev.kord.voice.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public interface DiscordVoicePacketData

@Serializable
public data class DiscordVoiceServerUpdateData(
    val token: String,
    @SerialName("guild_id") val guildId: Snowflake,
    val endpoint: String?,
) : DiscordVoicePacketData

@Serializable
public data class DiscordVoiceState(
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("channel_id")
    val channelId: Snowflake?,
    @SerialName("user_id")
    val userId: Snowflake,
    @SerialName("session_id")
    val sessionId: String,
) : DiscordVoicePacketData

