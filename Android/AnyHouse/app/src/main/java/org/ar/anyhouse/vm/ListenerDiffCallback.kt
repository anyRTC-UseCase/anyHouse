package org.ar.anyhouse.vm

import androidx.recyclerview.widget.DiffUtil




class ListenerDiffCallback : DiffUtil.ItemCallback<Listener>(){

    override fun areItemsTheSame(oldItem: Listener, newItem: Listener): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: Listener, newItem: Listener): Boolean {
        return(oldItem.isOpenAudio==oldItem.isOpenAudio)&&
                (oldItem.userId==oldItem.userId)&&
                (oldItem.userIcon==oldItem.userIcon)&&
                (oldItem.userName==oldItem.userName)
    }
}