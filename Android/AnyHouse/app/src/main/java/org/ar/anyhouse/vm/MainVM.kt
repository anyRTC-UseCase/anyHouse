package org.ar.anyhouse.vm

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.ar.anyhouse.sdk.RtmManager
import org.ar.anyhouse.service.ChannelListRep
import org.ar.anyhouse.service.CreateChannelInfo
import org.ar.anyhouse.service.JoinRoomRep
import org.ar.anyhouse.service.ServiceManager
import org.ar.anyhouse.utils.launch
import org.ar.anyhouse.utils.toast
import org.ar.anyhouse.view.ChannelActivity
import org.ar.anyhouse.view.MainActivity
import org.ar.rtm.ErrorInfo
import org.ar.rtm.ResultCallback
import rxhttp.tryAwait
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainVM : ViewModel() {

    val observerRoomList = MutableLiveData<ChannelListRep>()//房间列表变化通知
    val observerJoinChannel = MutableLiveData<JoinRoomRep>()//加入房间通知
    val observerError = MutableLiveData<Int>()//异常通知
    var curTopic = ""
    var curChannelType = 0
    var password = ""
    var haveNext = 0
    var pageNum = 1

    fun getRoomList(page:Int = pageNum){
        launch({
            val channelListRep  = ServiceManager.instance.getChannelList(1,15,it).await()
            haveNext = channelListRep.data.haveNext
            observerRoomList.value=channelListRep
        },{
            if (pageNum == 1){
                observerError.value = ErrorType.GET_ROOM_LIST
            }else{
                observerError.value = ErrorType.GET_MORE_ROOM_LIST
            }
        })
    }


    fun joinRoom(roomId:String,password:String=""){
        launch({
            val joinRep = ServiceManager.instance.joinChannel(roomId, "", it).await()
            if (joinRep.code == 0){
                ServiceManager.instance.setChannelInfo(Channel(joinRep.data.roomId, joinRep.data.roomName, joinRep.data.uid,joinRep.data.rtmToken,joinRep.data.rtcToken))
            }
            observerJoinChannel.value=joinRep
        },{
            observerError.value = ErrorType.JOIN_ROOM
        })
    }

    suspend fun loginRtm() = suspendCoroutine<Boolean>{
        RtmManager.instance.logOut()
        RtmManager.instance.login(ServiceManager.instance.getChannelInfo().rtmToken,
            ServiceManager.instance.getSelfInfo()?.userId.toString(),object :
                ResultCallback<Void> {
                override fun onSuccess(var1: Void?) {
                    it.resume(true)
                }
                override fun onFailure(var1: ErrorInfo?) {
                    it.resume(false)
                }
            })
    }



}