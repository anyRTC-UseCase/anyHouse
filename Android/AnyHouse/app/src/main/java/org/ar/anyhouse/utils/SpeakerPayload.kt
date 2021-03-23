package org.ar.anyhouse.utils

enum class SpeakerPayload {
    AUDIO,
    VOLUME;

    var data: Any? = null

    operator fun invoke(data: Any?): SpeakerPayload {
        this.data = data
        return this
    }

}