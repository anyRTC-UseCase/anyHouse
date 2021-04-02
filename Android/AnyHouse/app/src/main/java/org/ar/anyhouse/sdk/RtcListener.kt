package org.ar.anyhouse.sdk

import org.ar.rtc.IRtcEngineEventHandler
import org.ar.rtc.IRtcEngineEventHandler.LocalAudioStats

abstract class RtcListener  {

    abstract fun onJoinChannelSuccess(channel: String?, uid: String?, elapsed: Int)
    abstract fun onUserJoined(uid: String?, elapsed: Int)
    abstract fun onUserOffline(uid: String?, reason: Int)
    abstract fun onAudioVolumeIndication(speakers: Array<out IRtcEngineEventHandler.AudioVolumeInfo>?, totalVolume: Int)
    abstract fun onRemoteAudioStateChanged(uid: String?, state: Int, reason: Int, elapsed: Int)
    abstract fun onLocalAudioStateChanged(state: Int, reason: Int)
    abstract fun onClientRoleChanged(oldRole: Int, newRole: Int)
    abstract fun onTokenPrivilegeWillExpire()
    abstract fun onWarning(code:Int)
    abstract fun onConnectionLost()
}