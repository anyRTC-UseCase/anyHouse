package org.ar.anyhouse.service

import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import org.ar.anyhouse.BuildConfig
import org.ar.anyhouse.utils.Constans
import org.ar.anyhouse.utils.SpUtil
import org.ar.anyhouse.vm.Channel
import org.ar.anyhouse.vm.Self
import rxhttp.async
import rxhttp.toClass
import rxhttp.toStr
import rxhttp.wrapper.param.RxHttp
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ServiceManager private constructor(){

    //服务管理类 用于请求相关接口 并保存相应数据

    private var channelInfo : Channel? = null

    private var selfInfo : Self? =null


    suspend fun login(nickName:String,scope: CoroutineScope): Deferred<SignInRep> {
        return RxHttp.postJson(Api.LOGIN)
                .add("cType",1)
                .add("pkg",BuildConfig.APPLICATION_ID)
                .add("userName",nickName).toClass<SignInRep>().async(scope)
    }


    suspend fun getRoomStatus()= suspendCoroutine<Int>{
         RxHttp.postJson(Api.GET_ROOM_STATUS)
                .add("roomId",getSelfInfo()?.userId)
                 .add("pkg",BuildConfig.APPLICATION_ID)
                 .add("cType",1)
                 .asClass(GetRoomStatusInfo::class.java).observeOn(AndroidSchedulers.mainThread())
                 .subscribe { result ->
                    if (result.code == 0){
                        if (result.data.state ==1){//有未结束的
                            setChannelInfo(Channel(result.data.roomId,result.data.roomName,result.data.ownerId,result.data.rtmToken,result.data.rtcToken))
                        }
                        it.resume(result.data.state)
                    }else if (result.code==1054){
                        it.resume(2)
                    }else{
                        it.resume(-1)
                    }
                 }
    }


    suspend fun createChannel(isPrivate:Int,topic:String,password:String,scope: CoroutineScope) : Deferred<CreateChannelInfo> {
        return RxHttp.postJson(Api.CREATE_ROOM)
                .add("cType",1)
                .add("pkg",BuildConfig.APPLICATION_ID)
                .add("isPrivate",isPrivate)
                .add("rType",4)
                .add("roomName",if (topic.isNullOrEmpty()){getSelfInfo()?.userName+"的房间"}else{topic})
                .add("roomPwd",password).toClass<CreateChannelInfo>().async(scope)
    }

    suspend fun getChannelList(page:Int,pageSize:Int,scope: CoroutineScope) : Deferred<ChannelListRep> {
        return RxHttp.postJson(Api.GET_ROOM_LIST)
                .add("pageNum",page)
                .add("pageSize",pageSize)
                .toClass<ChannelListRep>().async(scope)
    }

    suspend fun joinChannel(roomId: String,roomPwd:String,scope: CoroutineScope) : Deferred<JoinRoomRep> {
        return RxHttp.postJson(Api.JOIN_ROOM)
                .add("cType",1)
                .add("pkg",BuildConfig.APPLICATION_ID)
                .add("roomId",roomId)
                .add("roomPwd",roomPwd)
                .toClass<JoinRoomRep>().async(scope)
    }



    fun leaveChannel(isOwner:Int,roomId:String){
         RxHttp.postJson(Api.LEAVE_ROOM)
                .add("isOwner",isOwner)
                .add("roomId",roomId)
                .asString()
                .observeOn(AndroidSchedulers.mainThread())
                 .subscribe {
                 }
    }


    suspend fun getSpeakList(roomId: String,scope: CoroutineScope): Deferred<MemberInfo>{
        return RxHttp.postJson(Api.GET_SPEAKER_LIST)
                .add("roomId",roomId).toClass<MemberInfo>().async(scope)
    }

    suspend fun getListenerList(roomId: String,scope: CoroutineScope): Deferred<MemberInfo>{
        return RxHttp.postJson(Api.GET_LISTENER_LIST)
                .add("roomId",roomId).toClass<MemberInfo>().async(scope)
    }

    suspend fun getRaisedHandsList(roomId: String,scope: CoroutineScope): Deferred<RaisedhandsRep>{
        return RxHttp.postJson(Api.GET_RAISED_HANDS_LIST)
                .add("roomId",roomId).toClass<RaisedhandsRep>().async(scope)
    }
    suspend fun updateUserName(name:String,scope: CoroutineScope) : Deferred<UpdateNameRep> {
        return RxHttp.postJson(Api.UPDATE_USERNAME)
                .add("userName",name).toClass<UpdateNameRep>().async(scope)
    }

    suspend fun updateUserStatus(roomId: String,userId:String,status:Int,scope: CoroutineScope) : Deferred<String> {
        return RxHttp.postJson(Api.UPDATE_USER_STATUS)
                .add("roomId",roomId)
                .add("uid",userId)
                .add("status",status).toStr().async(scope)
    }

    suspend fun getUserInfo(userId:String,scope: CoroutineScope) : Deferred<UserRep> {
        return RxHttp.postJson(Api.GET_USER_INFO)
                .add("uid",userId)
                .toClass<UserRep>().async(scope)
    }




    fun getSelfInfo() : Self? {
        if (selfInfo == null ){
            val userId = SpUtil.get().getString(Constans.USER_ID,"")
            val userIcon = SpUtil.get().getInt(Constans.USER_ICON,-1)
            val username = SpUtil.get().getString(Constans.USER_NAME,"")
            selfInfo = Self(username.toString(),userId.toString(),userIcon)
        }
        return selfInfo
    }



    //获取频道信息
    fun getChannelInfo(): Channel {
        return channelInfo as Channel
    }

    //设置频道信息
    fun setChannelInfo(channel: Channel){
        this.channelInfo = channel
    }


    companion object{
        val instance : ServiceManager by lazy {
            ServiceManager()
        }
    }

}