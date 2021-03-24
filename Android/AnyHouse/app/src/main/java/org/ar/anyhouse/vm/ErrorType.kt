package org.ar.anyhouse.vm

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.SOURCE)
annotation  class ErrorType {
    companion object {

        const val GET_ROOM_LIST = 1 //刷新房间列表异常
        const val GET_MORE_ROOM_LIST = 2 //获取更多房间列表异常
        const val JOIN_ROOM = 3 //加入频道异常
        const val CREATE_ROOM = 4 //创建频道异常
        const val TOKEN_EXPIRE = 5 //token过期
        const val HOSTER_LEAVE = 6 //主持人离开
    }
}