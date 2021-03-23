package org.ar.anyhouse.vm

import org.ar.anyhouse.R


//举手的人的信息
class RaisedHandsMember constructor(userId:String, userName:String="", userIcon :Int= 1, isInvited:Boolean=false) {
    var userId = userId
    var userName = userName
    var isInvited = isInvited
    var userIcon = userIcon



    override fun equals(other: Any?): Boolean {//重写equals userId相同既视为同一对象
       if (other is RaisedHandsMember){
           if (userId == other.userId){
               return true
           }
       }
        return false

    }
}