package dev.kord.voice


/**
 * A frame of 20ms Opus-encoded 48k stereo audio data.
 */
@JvmInline
public value class AudioFrame(public val data: ByteArray) {
    public companion object {
        public val SILENCE: AudioFrame = AudioFrame(byteArrayOf(0xFC.toByte(), 0xFF.toByte(), 0xFE.toByte()))

        public fun fromData(data: ByteArray?): AudioFrame? = data?.let(::AudioFrame)
    }
}
