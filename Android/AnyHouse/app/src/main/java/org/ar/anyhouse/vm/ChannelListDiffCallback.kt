package org.ar.anyhouse.vm

import androidx.recyclerview.widget.DiffUtil
import org.ar.anyhouse.service.ChannelListRep


class ChannelListDiffCallback : DiffUtil.ItemCallback<ChannelListRep.DataBean.ListBean>(){

    override fun areItemsTheSame(oldItem: ChannelListRep.DataBean.ListBean, newItem: ChannelListRep.DataBean.ListBean): Boolean {
        return oldItem.roomId == newItem.roomId
    }

    override fun areContentsTheSame(oldItem: ChannelListRep.DataBean.ListBean, newItem: ChannelListRep.DataBean.ListBean): Boolean {
        return(oldItem.roomId==newItem.roomId)
                &&(oldItem.isPrivate == newItem.isPrivate)
                &&(oldItem.avatars == newItem.avatars)
                &&(oldItem.ownerUid == newItem.ownerUid)
                &&(oldItem.roomPwd == newItem.roomPwd)
                &&(oldItem.speakerTotalNum == newItem.speakerTotalNum)
                &&(oldItem.userList == newItem.userList)
                &&(oldItem.userTotalNum == newItem.userTotalNum)
                &&(oldItem.roomName==newItem.roomName)
    }
}