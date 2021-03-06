package org.ar.anyhouse.vm

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ar.anyhouse.sdk.Role
import org.ar.anyhouse.sdk.RtcListener
import org.ar.anyhouse.sdk.RtmListener
import org.ar.anyhouse.sdk.RtcManager
import org.ar.anyhouse.sdk.RtmManager
import org.ar.anyhouse.service.ServiceManager
import org.ar.anyhouse.utils.json
import org.ar.anyhouse.utils.launch
import org.ar.anyhouse.utils.ternary
import org.ar.rtc.Constants
import org.ar.rtc.IRtcEngineEventHandler
import org.ar.rtm.RtmChannelMember
import org.ar.rtm.RtmMessage
import org.json.JSONObject


class ChannelVM : ViewModel() {

    val channelInfo = MutableLiveData<Channel>()
    val observeConnectStatus = MutableLiveData<Int>()
    val observeHostStatus = MutableLiveData<Int>()
    val observeRtcStatus = MutableLiveData<Int>()
    val observeRaisedHandsList = MutableLiveData<MutableList<RaisedHandsMember>>()//举手列表变化通知
    val observerSpeakList = MutableLiveData<MutableList<Speaker>>()//发言者列表变化通知
    val observerListenerList = MutableLiveData<MutableList<Listener>>()//听众列表通知
    val observeReceiveInvite = MutableLiveData<Boolean>()//收到邀请通知
    private var isInvited = false //是不是处于被邀请状态 是的话 就不用弹窗了
    val observeReject = MutableLiveData<String>()//拒绝邀请通知
    val observeMyRoleChange = MutableLiveData<Int>() // 身份改变
    val observerCloseMic = MutableLiveData<Boolean>() //麦克风状态
    val observerSpeakerVolume = MutableLiveData<Array<out IRtcEngineEventHandler.AudioVolumeInfo>?>()//说话者音量大小
    val observerHosterLeave = MutableLiveData<Boolean>() //主持人离开
    val observerTokenPrivilegeWillExpire = MutableLiveData<Boolean>()//token过期
    val observerInviteStatus = MutableLiveData<String>()//听众拒绝或者接收后更新听众邀请状态

    private val speakerList = mutableListOf<Speaker>() //发言者列表
    private val listenerList = mutableListOf<Listener>()//听众列表
    private val raisedHandsList = mutableListOf<RaisedHandsMember>()//举手列表
    private val serviceManager = ServiceManager.instance // API 管理类


    //自己是否是频道所有者
    fun isMeHost(): Boolean {
        return serviceManager.getSelfInfo()?.userId.equals(getChannelInfo().hostId)
    }

    fun isMe(userId: String): Boolean {
        return getSelf()?.userId == userId
    }

    fun isHostOnline():Boolean {//主持人是否还在房间
       return speakerList.contains(Speaker.Factory.create(getChannelHostId()))
    }

    //获取自己的信息
    fun getSelf(): Self? {
        return serviceManager.getSelfInfo()
    }

    //初始化 SDK
    fun initSDK(context: Context) {
        RtmManager.instance.registerListener(RtmEvent())
        RtcManager.instance.init(context)
        RtcManager.instance.registerListener(RtcEvent())
    }

    //释放 SDK
    private fun releaseSDK() {
        RtcManager.instance.unRegisterListener()
        RtcManager.instance.release()
    }

    //加入RTC频道
    fun joinChannelSDK() {
        RtcManager.instance.joinChannel(
            getToken(), getChannelId(), getSelfId(), if (isMeHost()) {
                Role.HOST
            } else {
                Role.AUDIENCE
            }
        )
    }

    fun leaveChannelSDK() {
        launch({
            ServiceManager.instance.leaveChannel(
                if (isMeHost()) {
                    1
                } else {
                    0
                }, channelInfo.value?.channelId.toString()
            )
            if (isMeHost()) {
                RtmManager.instance.sendChannelMessage(json("action" to BroadcastCMD.HOSTER_LEAVE))
            }
            RtmManager.instance.unregisterListener()
            RtmManager.instance.unSubMember(getChannelHostId())
            RtmManager.instance.logOut()
            RtmManager.instance.leaveChannel()
            RtcManager.instance.leaveChannel()
        })

    }

    fun muteLocalAudio(mute: Boolean) {
        RtcManager.instance.muteLocalAudio(mute)
        updateLocalAudioState(mute)

    }

    fun changeRoleToSpeaker() {
        RtcManager.instance.changeRoleToSpeaker()
    }

    fun changeRoleToListener() {
        RtcManager.instance.changeRoleToListener()
    }


    //举手
    fun raiseHand() {
        val params = json("action" to BroadcastCMD.RAISE_HANDS,"userName" to getSelf()?.userName,"avatar" to getSelf()?.userIcon)
        RtmManager.instance.sendPeerMessage(channelInfo.value?.hostId.toString(), params)
        updateUserStatusFromHttp(getSelfId(), 1)
    }

    //取消举手
    fun cancleRaiseHand() {
        RtmManager.instance.sendPeerMessage(channelInfo.value?.hostId.toString(), json("action" to BroadcastCMD.CANCLE_RAISE_HANDS))
        updateUserStatusFromHttp(getSelfId(), 0)

    }

    //邀请上台发言
    fun inviteLine(userId: String, needHttp: Boolean = false) {
        RtmManager.instance.sendPeerMessage(userId, json("action" to BroadcastCMD.INVITE_SPEAK))
        updateInviteStatus(userId)
        if (needHttp) {
            updateUserStatusFromHttp(userId, -1)
        }
    }

    //拒绝邀请
    fun rejectLine() {
        RtmManager.instance.sendPeerMessage(channelInfo.value?.hostId.toString(), json("action" to BroadcastCMD.REJECT_INVITE,"userName" to getSelf()?.userName))
        updateUserStatusFromHttp(getSelfId(), 0)
        isInvited = false
    }

    //同意邀请
    fun acceptLine() {
        RtmManager.instance.sendPeerMessage(channelInfo.value?.hostId.toString(), json("action" to BroadcastCMD.ACCEPT_INVITE))
        updateUserStatusFromHttp(getSelfId(), 2)
        isInvited = false
    }


    fun muteRemoteMic(userId: String) {
        RtmManager.instance.sendPeerMessage(userId, json("action" to BroadcastCMD.CLOSER_MIC))
    }


    fun changeRoleGuest(userId: String) {
        RtmManager.instance.sendPeerMessage(userId, json("action" to BroadcastCMD.ROLE_CHANGE_GUEST))
        updateUserStatusFromHttp(userId, 0)
    }

    //设置邀请状态 邀请过了就不再邀请了
    fun updateInviteStatus(userId: String) {
        raisedHandsList.find { it.userId == userId }?.let {
            it.isInvited = true
            observeRaisedHandsList.value = getAllRaiseHandsMember()
        }
        listenerList.find { it.userId == userId }?.let {
            it.isInvite = true
            observerListenerList.value = getAllListener()
        }
    }

    fun getChannelInfo(): Channel {
        channelInfo.value = serviceManager.getChannelInfo()
        return channelInfo.value!!
    }

    fun getChannelId(): String {
        return serviceManager.getChannelInfo().channelId
    }

    fun getChannelHostId(): String {
        return serviceManager.getChannelInfo().hostId
    }

    fun getChannelPassword():String{
        return ""
    }


    fun isOpenRoom():Boolean{
        return serviceManager.getChannelInfo().roomType ==0
    }

    fun getToken(): String {
        return serviceManager.getChannelInfo().rtcToken
    }

    fun getChannelName(): String {
        return serviceManager.getChannelInfo().channelName
    }

    fun sendSelfInfo(){
        launch({
            RtmManager.instance.sendChannelMessage(json(
                "avatar" to getSelf()?.userIcon,
                "userName" to getSelf()?.userName,
                "userId" to getSelf()?.userId,
                "action" to BroadcastCMD.USER_INFO
            ))
        })

    }

    fun getSelfId(): String {
        return getSelf()?.userId.toString()
    }

    fun updateUserStatusFromHttp(userId: String, state: Int) {
        launch({
            ServiceManager.instance.updateUserStatus(getChannelId(), userId, state, it)
        })
    }

    fun getMemberWhenEnterFromHttp() {
        launch({
            getListenerListFormHttp(true)
            if (isMeHost()) {
                getRaisedHandsListFormHttp()
            }
        })
    }


    fun getListenerListFormHttp(isFirst: Boolean){
        launch({
            val asyncListenerList = ServiceManager.instance.getListenerList(getChannelId(), it)
            var listenerListBean = asyncListenerList.await()
            if (listenerListBean.code == 0) {
                if (!listenerListBean.data.isNullOrEmpty()) {
                    if (isFirst){
                        listenerListBean.data.forEach {
                           val listener=Listener.Factory.create(it.uid)
                            listener.userName=it.userName
                            listener.userIcon=it.avatar
                            addListener(listener)
                        }
                    }else{
                        val memberListFromHttp = mutableListOf<String>()
                        val localListerList = mutableListOf<String>()

                        listenerListBean.data.forEach {
                            memberListFromHttp.add(it.uid)
                        }

                        listenerList.forEach {
                            localListerList.add(it.userId)
                        }

                        localListerList.forEach {
                            if (it !in memberListFromHttp){
                                listenerList.remove(Listener.Factory.create(it))
                               // removeListener(Listener.Factory.create(it))
                            }
                        }

                        memberListFromHttp.forEach {uid ->
                            if (uid !in localListerList){
                                listenerListBean.data.find { it.uid == uid }?.let {
                                    val listener= Listener.Factory.create(it.uid)
                                    listener.userName=it.userName
                                    listener.userIcon=it.avatar
                                    listenerList.add(listener)
                                    //addListener(listener)
                                }
                            }
                        }
                        observerListenerList.value=getAllListener()
                    }
                }else{
                    if(!isFirst) {
                        listenerList.clear()
                        observerListenerList.value = listenerList
                    }
                }
            }
        })
    }



    fun getRaisedHandsListFormHttp(){
        launch({
            var raisedRep =
                    ServiceManager.instance.getRaisedHandsList(getChannelId(), it).await()
            if (raisedRep.code == 0) {
                if (raisedRep.data.size > 0){
                    raisedRep.data.forEach {
                        addRaisedHandsMember(
                                RaisedHandsMember(
                                        it.uid,
                                        it.userName,
                                        it.avatar,
                                        it.state != 1
                                )
                        )
                    }
                }else {
                    raisedHandsList.clear()
                    observeRaisedHandsList.value = raisedHandsList
                }

            }
        })
    }

    inner class RtmEvent : RtmListener() {


        override fun onConnectionStateChanged(var1: Int, var2: Int) {
            super.onConnectionStateChanged(var1, var2)
            observeConnectStatus.value =var1
        }

        override fun onMemberJoined(var1: RtmChannelMember?) {
            super.onMemberJoined(var1)

        }

        override fun onMemberLeft(var1: RtmChannelMember?) {
            super.onMemberLeft(var1)
            removeRaisedHandsMember(RaisedHandsMember(var1?.userId.toString()))
            removeListener(Listener.Factory.create(var1?.userId.toString()))
            removeSpeaker(Speaker.Factory.create(var1?.userId.toString()))//上麦的听众异常离开的情况
        }

        override fun onMessageReceived(var1: RtmMessage?, var2: RtmChannelMember?) {
            super.onMessageReceived(var1, var2)
            val text = var1?.text
            val json = JSONObject(text)
            when (json.getInt("action")) {
                BroadcastCMD.HOSTER_LEAVE -> {
                    if (!isMeHost()) {
                        observerHosterLeave.value = true
                    }
                }
                BroadcastCMD.USER_INFO -> {
                    launch({
                        val uIcon = json.getInt("avatar")
                        val uName = json.getString("userName")
                        if (var2?.userId == getChannelHostId()){
                            addSpeaker(Speaker.Factory.create(var2.userId).apply {
                                userName = uName
                                isHoster = true
                                isOpenAudio = true
                                userIcon =uIcon
                            })
                        }else{
                            addListener(createListener(var2?.userId.toString(), uName, uIcon))
                        }

                    })
                }
            }

        }

        override fun onJoinChannelSuccess(channelId: String?) {
            if (!isMeHost()) {
                addListener(createListener())
            }
            //加入rtm频成功后 发送

            launch({
                sendSelfInfo()
                getMemberWhenEnterFromHttp()
            })


        }

        override fun onMessageReceived(var1: RtmMessage?, var2: String?) {
            super.onMessageReceived(var1, var2)
            val text = var1?.text
            val json = JSONObject(text)
            val action = json.getInt("action")
            when (action) {
                BroadcastCMD.RAISE_HANDS -> {
                    val userName = json.getString("userName")
                    val userIcon = json.getInt("avatar")
                    addRaisedHandsMember(
                        RaisedHandsMember(
                            var2.toString(),
                            userName,
                            userIcon,
                            false
                        )
                    )
                }
                BroadcastCMD.CANCLE_RAISE_HANDS -> {
                    removeRaisedHandsMember(RaisedHandsMember(var2.toString()))
                }
                BroadcastCMD.INVITE_SPEAK -> {
                    if (!isInvited) {
                        observeReceiveInvite.value = true
                        isInvited = true
                    }
                }
                BroadcastCMD.REJECT_INVITE -> {
                    observerInviteStatus.value=var2.toString()
                    val userName = json.getString("userName")
                    removeRaisedHandsMember(RaisedHandsMember(var2.toString()))
                    observeReject.value = userName

                }
                BroadcastCMD.ACCEPT_INVITE -> {
                    observerInviteStatus.value=var2.toString()
                    removeRaisedHandsMember(RaisedHandsMember(var2.toString()))
                }

                BroadcastCMD.ROLE_CHANGE_GUEST -> {
                    changeRoleToListener()
                }
                BroadcastCMD.CLOSER_MIC -> {
                    observerCloseMic.value = true
                    muteLocalAudio(true)
                }

            }

        }

        override fun onPeersOnlineStatusChanged(var1: MutableMap<String, Int>?) {
            super.onPeersOnlineStatusChanged(var1)
            var1?.let {
                if (it.containsKey(getChannelHostId())) {
                    observeHostStatus.value = it[getChannelHostId()]
                    if (it[getChannelHostId()]!=0){
                        removeSpeaker(Speaker.Factory.create(getChannelHostId()))
                    }
                }
            }
        }

    }

    inner class RtcEvent : RtcListener() {
        override fun onJoinChannelSuccess(channel: String?, uid: String?, elapsed: Int) {
            if (isMeHost()) {
                addSpeaker(Speaker.Factory.create(uid.toString()).apply {
                    isHoster = true
                    isOpenAudio = true
                    userName = getSelf()?.userName.toString()
                    userIcon = getSelf()?.userIcon!!
                })
            }else{
                subHostOnlineStatus()
            }
            RtmManager.instance.joinChannel(getChannelId())
        }

        override fun onConnectionLost() {//彻底断开rtc
        }

        override fun onUserJoined(uid: String?, elapsed: Int) {
            //rtc的用户加入 listenerList
            launch({
                val userRep = ServiceManager.instance.getUserInfo(uid.toString(), it).await()
                    addSpeaker(Speaker.Factory.create(uid.toString()).apply {
                        isHoster = uid == getChannelHostId()
                        userIcon = (userRep.code == 0).ternary(userRep.data.avatar,1)
                        userName = (userRep.code == 0).ternary(userRep.data.userName,"未知")
                        isOpenAudio = true
                    })
            })

        }

        override fun onUserOffline(uid: String?, reason: Int) {
            if (reason ==2){//对方身份改变 从主播变成听众
                speakerList.find { it.userId == uid }?.let {
                    //找到speakerList里的这个人 将个人信息赋值到听众
                    val listener = Listener.Factory.create(uid.toString())
                    listener.isInvite = false
                    listener.userIcon=it.userIcon
                    listener.userName=it.userName
                    //再从speakeList删除
                    removeSpeaker(Speaker.Factory.create(uid.toString()))
                    //listenenrList 添加
                    addListener(listener)
                }
            }else{
                removeSpeaker(Speaker.Factory.create(uid.toString()))
            }
        }

        override fun onAudioVolumeIndication(
            speakers: Array<out IRtcEngineEventHandler.AudioVolumeInfo>?,
            totalVolume: Int
        ) {
            observerSpeakerVolume.value = speakers
        }

        override fun onRemoteAudioStateChanged(
            uid: String?,
            state: Int,
            reason: Int,
            elapsed: Int
        ) {

            updateSpeakerAudioState(uid.toString(), reason)
        }

        override fun onLocalAudioStateChanged(state: Int, reason: Int) {

        }

        override fun onClientRoleChanged(oldRole: Int, newRole: Int) {
            observeMyRoleChange.value = newRole
            if (newRole == Constants.CLIENT_ROLE_BROADCASTER) {
                removeListener(Listener.Factory.create(getSelf()?.userId.toString()))
                addSpeaker(Speaker.Factory.create(getSelf()?.userId.toString()).apply {
                    isHoster = isMeHost()
                    isOpenAudio = true
                    userIcon = getSelf()?.userIcon!!
                    userName = getSelf()?.userName.toString()
                })
            } else {
                removeSpeaker(Speaker.Factory.create(getSelf()?.userId.toString()))
                addListener(createListener())
                observeMyRoleChange.value = Constants.CLIENT_ROLE_AUDIENCE //从主持人变游客
            }


        }

        override fun onTokenPrivilegeWillExpire() {
            observerTokenPrivilegeWillExpire.value = true
        }


        override fun onWarning(code: Int) {
            observeRtcStatus.value = code
        }
    }

    private fun subHostOnlineStatus() {//订阅主播在线状态
        if (!isMeHost()) {
            RtmManager.instance.subMemberOnline(getChannelHostId())
            launch({
                var isOnline = RtmManager.instance.queryMemberOnline(getChannelHostId())
                if (!isOnline){
                    observeHostStatus.value = 1
                }
            })
        }
    }


    private fun getAllSpeaker(): MutableList<Speaker> {
        val newList = mutableListOf<Speaker>()
        speakerList.forEach {//必须深拷贝 不然无法使用DiffUtil
            val speaker = Speaker.Factory.create(it.userId)
            speaker.userName = it.userName
            speaker.isHoster = it.isHoster
            speaker.isOpenAudio = it.isOpenAudio
            speaker.userIcon = it.userIcon
            newList.add(speaker)
        }
        return newList
    }

    private fun getAllListener(): MutableList<Listener> {
        val newList = mutableListOf<Listener>()
        listenerList.forEach {//必须深拷贝 不然无法使用DiffUtil
            val listener = Listener.Factory.create(it.userId)
            listener.userName = it.userName
            listener.isOpenAudio = it.isOpenAudio
            listener.userIcon = it.userIcon
            listener.isInvite = it.isInvite
            newList.add(listener)
        }
        return newList
    }

    private fun getAllRaiseHandsMember(): MutableList<RaisedHandsMember> {
        val newList = mutableListOf<RaisedHandsMember>()
        raisedHandsList.forEach {//必须深拷贝 不然无法使用DiffUtil
            val member = RaisedHandsMember(it.userId, it.userName, it.userIcon, it.isInvited)
            newList.add(member)
        }
        return newList
    }

    private fun addSpeaker(speaker: Speaker) {
        if (speakerList.contains(speaker)) {
            var sp = speakerList.find { it.userId == speaker.userId }
            sp?.let {
                speakerList.remove(it)
            }
            if (speaker.isHoster) {
                speakerList.add(0, speaker)
            } else {
                speakerList.add(speaker)
            }
        } else {
            if (speaker.isHoster) {
                speakerList.add(0, speaker)
            } else {
                speakerList.add(speaker)
            }
        }
        observerSpeakList.value = getAllSpeaker()
        removeListener(Listener.Factory.create(speaker.userId))//从听众到说话者 听众列表里有的话需要删除
    }

    private fun addListener(listener: Listener) {//得先检查说话得里面有没有这个人 没有才添加
        var speaker = speakerList.find { it.userId == listener.userId }
        if (speaker == null) {
            listenerList.find { it.userId == listener.userId }?.let {
                listenerList.remove(it)
            }
            listenerList.add(listener)
            observerListenerList.value = getAllListener()
        }
    }

    private fun removeListener(listener: Listener) {

        listenerList.find { it.userId ==listener.userId }?.let {
            listenerList.remove(it)
        }
        observerListenerList.value = getAllListener()
    }

    private fun removeSpeaker(speaker: Speaker) {
        if (speakerList.contains(speaker)) {
            speakerList.remove(speaker)
        }
        observerSpeakList.value = getAllSpeaker()
    }

    private fun updateSpeakerAudioState(uid: String, reason: Int) {
        speakerList.find { it.userId == uid }?.let {
            if (reason == 5) {
                it.isOpenAudio = false
            } else if (reason == 6) {
                it.isOpenAudio = true
            }
            observerSpeakList.value = getAllSpeaker()
        }
    }

    private fun createListener(
        userId: String = getSelfId(),
        userName: String = getSelf()?.userName!!,
        userIcon: Int = getSelf()?.userIcon!!
    ): Listener {
        val member = Listener.Factory.create(userId)
        member.userName = userName
        member.userIcon = userIcon
        return member
    }

    private fun addRaisedHandsMember(raisedHandsMember: RaisedHandsMember) {
        raisedHandsList.find { it.userId == raisedHandsMember.userId }?.let {
            raisedHandsList.remove(it)
        }
        raisedHandsList.add(raisedHandsMember)
        observeRaisedHandsList.value = getAllRaiseHandsMember()
    }

    private fun removeRaisedHandsMember(raisedHandsMember: RaisedHandsMember) {
        raisedHandsList.find { it.userId == raisedHandsMember.userId }?.let {
            raisedHandsList.remove(it)
            observeRaisedHandsList.value = getAllRaiseHandsMember()
        }
    }

    private fun updateLocalAudioState(mute: Boolean) {
        speakerList.find { it.userId == getSelf()?.userId.toString() }?.let {
            it.isOpenAudio = !mute
            observerSpeakList.value = getAllSpeaker()
        }
    }


    override fun onCleared() {
        super.onCleared()
        releaseSDK()
    }
}

