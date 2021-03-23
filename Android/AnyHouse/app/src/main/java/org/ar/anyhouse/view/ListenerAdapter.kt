package org.ar.anyhouse.view

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.ar.anyhouse.R
import org.ar.anyhouse.utils.Constans
import org.ar.anyhouse.vm.Listener

class ListenerAdapter : BaseQuickAdapter<Listener,BaseViewHolder>(R.layout.item_listener) {
    override fun convert(holder: BaseViewHolder, item: Listener) {
        holder.setText(R.id.tv_name,item.userName)
        holder.setImageResource(R.id.iv_user,Constans.userIconArray[item.userIcon]!!)
    }
}