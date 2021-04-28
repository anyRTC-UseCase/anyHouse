package org.ar.anyhouse.sdk

import android.content.Context
import android.util.Log
import org.ar.anyhouse.BuildConfig
import org.ar.anyhouse.utils.Constans
import org.ar.anyhouse.utils.SpUtil
import org.ar.anyhouse.utils.launch
import org.ar.rtc.Constants
import org.ar.rtc.IRtcEngineEventHandler
import org.ar.rtc.RtcEngine
import org.json.JSONObject

class RtcManager private constructor(){

    private var rtcEngine : RtcEngine? =null
    private var rtcListener : RtcListener? =null

    //初始化SDK
    fun init(context: Context){
        val appId = SpUtil.get().getString(Constans.APP_ID,"")
        rtcEngine = RtcEngine.create(context, appId.toString(),RtcEvent())
        rtcEngine?.let {
            it.enableAudioVolumeIndication(1000, 3, true)
            it.setParameters(JSONObject().apply {
                put("Cmd","SetAudioAiNoise")
                put("Enable",1)//开启智能降噪
            }.toString())
        }
    }
    //加入房间
    fun joinChannel(channelToken:String,channelId:String,userId:String,role: Int){
        rtcEngine?.let {
            it.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            if (role == Role.HOST){
                it.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
            }else{
                it.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
            }
            it.setAudioProfile(Constants.AUDIO_PROFILE_SPEECH_STANDARD , Constants.AUDIO_SCENARIO_GAME_STREAMING);
            it.joinChannel(channelToken,channelId,"",userId)
        }
    }
    //设置为说话嘉宾
    fun changeRoleToSpeaker(){
        rtcEngine?.let {
            it.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
        }
    }
    //设置为听众
    fun changeRoleToListener(){
        rtcEngine?.let {
            it.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
        }
    }
    //离开频道
    fun leaveChannel(){
        rtcEngine?.let {
            it.leaveChannel()
        }
    }
    //本地麦克风静音
    fun muteLocalAudio(mute:Boolean){
        rtcEngine?.let {
            it.muteLocalAudioStream(mute)
        }
    }
    //销毁SDK
    fun release(){
        RtcEngine.destroy()
    }

    companion object {
        val instance: RtcManager by lazy() {
            RtcManager()
        }
    }


    fun registerListener(rtcListener: RtcListener){
        this.rtcListener=rtcListener
    }

    fun unRegisterListener(){
        this.rtcListener = null
    }

    private inner class RtcEvent: IRtcEngineEventHandler() {

        override fun onJoinChannelSuccess(channel: String?, uid: String?, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            launch({
                rtcListener?.let {
                    it.onJoinChannelSuccess(channel, uid, elapsed)
                }
            })

        }

        override fun onConnectionLost() {
            super.onConnectionLost()
            launch({
                rtcListener?.let {
                    it.onConnectionLost()
                }
            })
        }

        override fun onUserJoined(uid: String?, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            launch({
                rtcListener?.let {
                    it.onUserJoined( uid, elapsed)
                }
            })

        }

        override fun onUserOffline(uid: String?, reason: Int) {
            super.onUserOffline(uid, reason)
            launch({
                rtcListener?.let {
                    it.onUserOffline(uid,reason)
                }
            })

        }

        override fun onAudioVolumeIndication(
            speakers: Array<out AudioVolumeInfo>?,
            totalVolume: Int
        ) {
            launch({
                rtcListener?.let {
                    it.onAudioVolumeIndication(speakers, totalVolume)
                }
            })

        }

        override fun onRemoteAudioStateChanged(uid: String?, state: Int, reason: Int, elapsed: Int) {
            super.onRemoteAudioStateChanged(uid, state, reason, elapsed)
            launch({
                rtcListener?.let {
                    it.onRemoteAudioStateChanged(uid, state, reason, elapsed)
                }
            })

        }

        override fun onLocalAudioStateChanged(state: Int, error: Int) {
            super.onLocalAudioStateChanged(state, error)
            launch({
                rtcListener?.let {
                    it.onLocalAudioStateChanged(state,error)
                }
            })
        }

        override fun onClientRoleChanged(oldRole: Int, newRole: Int) {
            super.onClientRoleChanged(oldRole, newRole)
            launch({
                rtcListener?.let {
                    it.onClientRoleChanged(oldRole,newRole)
                }
            })
        }

        override fun onWarning(warn: Int) {
            super.onWarning(warn)
            launch({
                rtcListener?.let {
                    it.onWarning(warn)
                }
            })
        }


        override fun onTokenPrivilegeWillExpire(token: String?) {
            super.onTokenPrivilegeWillExpire(token)
            launch({
                rtcListener?.let {
                    it.onTokenPrivilegeWillExpire()
                }
            })
        }

    }


}