package org.ar.anyhouse.utils

enum class RaisedHandsPayload {
    INVITE;

    var data: Any? = null

    operator fun invoke(data: Any?): RaisedHandsPayload {
        this.data = data
        return this
    }
}