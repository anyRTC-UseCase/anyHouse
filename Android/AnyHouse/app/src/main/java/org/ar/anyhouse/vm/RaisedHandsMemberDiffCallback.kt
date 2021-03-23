package org.ar.anyhouse.vm

import androidx.recyclerview.widget.DiffUtil
import org.ar.anyhouse.utils.SpeakerPayload


class RaisedHandsMemberDiffCallback : DiffUtil.ItemCallback<RaisedHandsMember>(){

    override fun areItemsTheSame(oldItem: RaisedHandsMember, newItem: RaisedHandsMember): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: RaisedHandsMember, newItem: RaisedHandsMember): Boolean {
        return (oldItem.isInvited==newItem.isInvited)&&
                (oldItem.userId==newItem.userId)&&
                (oldItem.userName==newItem.userName)
    }

}