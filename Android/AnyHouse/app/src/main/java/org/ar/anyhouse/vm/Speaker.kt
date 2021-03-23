package org.ar.anyhouse.vm

import org.ar.anyhouse.R


/**
 * 说话者的信息
 */
class Speaker private constructor(val userId:String){

    var isOpenAudio : Boolean = false //是否打开了音频
    var isHoster : Boolean = false //是否是主持人
    var userName : String = ""
    var userIcon : Int = 1


    object Factory{

        fun create(userId: String): Speaker {
            return create(userId, userName = "guest", userIcon = 1, isOpenAudio = false, isHoster = false)
        }

        private fun create(userId: String,userName :String,userIcon:Int,isOpenAudio:Boolean,isHoster:Boolean): Speaker {
            val member = Speaker(userId)
            member.isHoster = isHoster
            member.isOpenAudio = isOpenAudio
            member.userIcon = userIcon
            member.userName = userName
            return member
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is Speaker){
            if (userId == other.userId){
                return true
            }
        }
        return false
    }

}