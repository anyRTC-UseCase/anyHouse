package org.ar.anyhouse.service

import rxhttp.wrapper.annotation.DefaultDomain

object  Api {

    @DefaultDomain
    @JvmField
    val BASE_API = "http://arlive.agrtc.cn:12680/arapi/arlive/v1/anyhouse/"

    //登录
    const val LOGIN = "signIn"

    //创建房间
    const val CREATE_ROOM = "addRoom"

    //获取房间状态
    const val GET_ROOM_STATUS  = "getRoomState"

    const val GET_ROOM_LIST  = "getRoomList"

    //加入房间
    const val JOIN_ROOM = "joinRoom"

    //离开房间
    const val LEAVE_ROOM = "leaveRoom"

    //获取说话者列表
    const val GET_SPEAKER_LIST  = "getSpeakerList"

    //获取听众列表
    const val GET_LISTENER_LIST = "getListenerList"

    //获取举手列表
    const val GET_RAISED_HANDS_LIST = "getHandsUpList"

    //获取用户信息
    const val GET_USER_INFO = "getUserInfo"

    //麦克风？不需要
    const val UPDATE_USER_MIC_STATUS = "updateMicStatus"

    //更新用户名
    const val UPDATE_USERNAME = "updateUserName"

    //更新用户状态
    const val UPDATE_USER_STATUS = "updateUserStatus"


}