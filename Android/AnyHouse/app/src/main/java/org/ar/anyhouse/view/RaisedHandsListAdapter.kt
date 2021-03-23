package org.ar.anyhouse.view

import android.widget.Button
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.ar.anyhouse.R
import org.ar.anyhouse.utils.Constans
import org.ar.anyhouse.utils.RaisedHandsPayload
import org.ar.anyhouse.vm.RaisedHandsMember

class RaisedHandsListAdapter : BaseQuickAdapter<RaisedHandsMember,BaseViewHolder>(R.layout.item_raised_hands) {

    override fun convert(holder: BaseViewHolder, item: RaisedHandsMember) {
        holder.setText(R.id.tv_name,item.userName)
        val button = holder.getView<Button>(R.id.btn_invite)
        if (item.isInvited){
            button.text = "已邀请"
            holder.setEnabled(R.id.btn_invite,false)
        }else{
            button.text = "邀请"
            holder.setEnabled(R.id.btn_invite,true)
        }
        button.isSelected = item.isInvited
        holder.setImageResource(R.id.iv_user,Constans.userIconArray[item.userIcon]!!)
    }


    override fun convert(holder: BaseViewHolder, item: RaisedHandsMember, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        if (payloads.isNullOrEmpty()){
            convert(holder,item)
            return
        }
        payloads.forEach {
            when(val  payload = it as RaisedHandsPayload){
                RaisedHandsPayload.INVITE ->{
                    val isInvited = payload.data as Boolean
                    val button = holder.getView<Button>(R.id.btn_invite)
                    if (isInvited){
                        button.text = "已邀请"
                        holder.setEnabled(R.id.btn_invite,false)
                    }else{
                        button.text = "邀请"
                        holder.setEnabled(R.id.btn_invite,true)
                    }
                    button.isSelected = isInvited
                }
            }
        }
    }
}