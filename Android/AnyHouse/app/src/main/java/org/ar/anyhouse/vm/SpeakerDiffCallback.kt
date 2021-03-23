package org.ar.anyhouse.vm

import androidx.recyclerview.widget.DiffUtil
import org.ar.anyhouse.utils.SpeakerPayload


class SpeakerDiffCallback : DiffUtil.ItemCallback<Speaker>(){

    override fun areItemsTheSame(oldItem: Speaker, newItem: Speaker): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: Speaker, newItem: Speaker): Boolean {
        return (oldItem.isHoster==newItem.isHoster)&&
                (oldItem.isOpenAudio==newItem.isOpenAudio)&&
                (oldItem.userId==newItem.userId)&&
                (oldItem.userIcon==newItem.userIcon)&&
                (oldItem.userName==newItem.userName)
    }

    override fun getChangePayload(oldItem: Speaker, newItem: Speaker): Any? {
        return SpeakerPayload.AUDIO(newItem.isOpenAudio)
    }
}