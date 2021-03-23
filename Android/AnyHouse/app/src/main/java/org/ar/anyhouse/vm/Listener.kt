package org.ar.anyhouse.vm

import org.ar.anyhouse.R


/**
 * 每个观众的信息
 */
class Listener private constructor(val userId:String){

    var isOpenAudio : Boolean = false //是否打开了音频
    var userName : String = ""
    var userIcon : Int = 1


    object Factory{

        fun create(userId: String): Listener {
            return create(userId, userName = "guest", userIcon = 1, isOpenAudio = false)
        }

        private fun create(userId: String,userName :String,userIcon:Int,isOpenAudio:Boolean): Listener {
            val member = Listener(userId)
            member.isOpenAudio = isOpenAudio
            member.userIcon = userIcon
            member.userName = userName
            return member
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is Listener){
            if (userId == other.userId){
                return true
            }
        }
        return false
    }
}