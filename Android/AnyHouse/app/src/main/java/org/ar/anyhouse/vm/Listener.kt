package org.ar.anyhouse.vm



/**
 * 每个观众的信息
 */
open class Listener(val userId:String){

    var isOpenAudio : Boolean = false //是否打开了音频
    var userName : String = ""
    var userIcon : Int = 1
    var isInvite : Boolean = false //是否已经发送邀请


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