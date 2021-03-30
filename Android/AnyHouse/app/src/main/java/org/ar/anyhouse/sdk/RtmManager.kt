package org.ar.anyhouse.sdk

import android.content.Context
import androidx.annotation.Nullable
import org.ar.anyhouse.BuildConfig
import org.ar.anyhouse.utils.Constans
import org.ar.anyhouse.utils.SpUtil
import org.ar.anyhouse.utils.launch
import org.ar.rtm.*
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class RtmManager private constructor(){

    private var rtmClient : RtmClient? =null

    private var rtmCallBack : RtmListener? =null

    private var rtmChannel: RtmChannel? =null


    fun init(context: Context){
        if (rtmClient == null) {
            val appId = SpUtil.get().getString(Constans.APP_ID, "")
            rtmClient = RtmClient.createInstance(context, appId.toString(), RtmEvent())
            rtmClient?.setParameters(JSONObject().apply {
                put("Cmd", "ConfPriCloudAddr")
                put("ServerAdd", "pro.rtmgw.agrtc.cn")
                put("Port", 7080)
            }.toString())
        }
    }



    fun registerListener(rtmCallBack: RtmListener){
        this.rtmCallBack = rtmCallBack
    }

    fun unregisterListener(){
        this.rtmCallBack = null
    }

    fun sendPeerMessage(userId:String,json:String){
        rtmClient?.let {
            it.sendMessageToPeer(userId,it.createMessage(json),SendMessageOptions(),null)
        }
    }

    fun login(token:String,userId:String,@Nullable callback: ResultCallback<Void>){
        rtmClient?.login(token,userId,object :ResultCallback<Void> {
            override fun onSuccess(var1: Void?) {
                callback.onSuccess(var1)
            }

            override fun onFailure(var1: ErrorInfo?) {
                callback.onFailure(var1)
            }

        })
    }

    fun logOut(){
        rtmClient?.let {
            it.logout(null)
        }
    }

    fun joinChannel(channelId:String){
        rtmChannel = rtmClient?.createChannel(channelId,ChannelEvent())
        rtmChannel?.join(object :ResultCallback<Void>{
            override fun onSuccess(var1: Void?) {
                launch({
                    rtmCallBack?.let {
                        it.onJoinChannelSuccess(channelId)
                    }
                })
            }

            override fun onFailure(var1: ErrorInfo?) {
            }

        })
    }

    fun getChannelMember(members: (List<RtmChannelMember>) -> Unit){
        rtmChannel?.getMembers(object :ResultCallback<MutableList<RtmChannelMember>>{
            override fun onSuccess(var1: MutableList<RtmChannelMember>?) {
                launch({
                    var1?.let { members(it) }
                })

            }

            override fun onFailure(var1: ErrorInfo?) {
            }

        })
    }

    suspend fun sendChannelMessage(json:String)= suspendCoroutine<Boolean> {
        rtmChannel?.let {channel ->
            channel.sendMessage(rtmClient?.createMessage(json),object :ResultCallback<Void>{
                override fun onSuccess(var1: Void?) {
                    it.resume(true)
                }

                override fun onFailure(var1: ErrorInfo?) {
                    it.resume(false)
                }

            })
        }
    }

    fun subMemberOnline(userId:String){
        val setArray = mutableSetOf(userId)
        rtmClient?.subscribePeersOnlineStatus(setArray,null)
    }

    suspend fun queryMemberOnline(userId:String)=suspendCoroutine<Boolean> {
        val setArray = mutableSetOf(userId)
        rtmClient?.queryPeersOnlineStatus(setArray,object :ResultCallback<Map<String,Boolean>>{
            override fun onSuccess(var1: Map<String, Boolean>?) {
               it.resume(var1?.get(userId) as Boolean)
            }

            override fun onFailure(var1: ErrorInfo?) {
                it.resume(false)
            }

        })
    }

    fun unSubMember(userId:String){
        val setArray = mutableSetOf(userId)
        rtmClient?.unsubscribePeersOnlineStatus(setArray,null)
    }
    fun leaveChannel(){
        rtmChannel?.let {
            it.leave(null)
            it.release()
        }
    }

    fun release(){
        rtmClient?.let {
            it.release()
        }
        rtmClient = null
    }

    private inner class RtmEvent: RtmClientListener {
        
        override fun onConnectionStateChanged(state: Int, reason: Int) {
            launch({
                rtmCallBack?.let {
                    it.onConnectionStateChanged(state, reason)
                }
            })

        }
        override fun onMessageReceived(var1: RtmMessage?, var2: String?) {
            launch({
                rtmCallBack?.let {
                    it.onMessageReceived(var1, var2)
                }
            })

        }
        override fun onTokenExpired() {
            
        }
        override fun onPeersOnlineStatusChanged(var1: MutableMap<String, Int>?) {
            launch({
                rtmCallBack?.let {
                    it.onPeersOnlineStatusChanged(var1)
                }
            })

        }

    }

    private inner class ChannelEvent : RtmChannelListener {
        override fun onMemberCountUpdated(var1: Int) {
            launch({
                rtmCallBack?.let {
                    it.onMemberCountUpdated(var1)
                }
            })

        }

        override fun onAttributesUpdated(var1: MutableList<RtmChannelAttribute>?) {
        }

        override fun onMessageReceived(var1: RtmMessage?, var2: RtmChannelMember?) {
            launch({
                rtmCallBack?.let {
                    it.onMessageReceived(var1, var2)
                }
            })

        }

        override fun onMemberJoined(var1: RtmChannelMember?) {
            launch({
                rtmCallBack?.let {
                    it.onMemberJoined(var1)
                }
            })

        }

        override fun onMemberLeft(var1: RtmChannelMember?) {
            launch({
                rtmCallBack?.let {
                    it.onMemberLeft(var1)
                }
            })

        }
    }



    companion object{
        val instance : RtmManager by lazy() {
            RtmManager()
        }
    }

}