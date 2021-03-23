package org.ar.anyhouse.view

import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.ar.anyhouse.R
import org.ar.anyhouse.service.ChannelListRep
import org.ar.anyhouse.utils.Constans

class ChannelListAdapter : BaseQuickAdapter<ChannelListRep.DataBean.ListBean, BaseViewHolder>(R.layout.item_channel) {

    override fun convert(holder: BaseViewHolder, item: ChannelListRep.DataBean.ListBean) {
        holder.setText(R.id.tv_channel_name,item.roomName)
        item.avatars?.let {
            it.forEachIndexed { index, i ->
            when(index){
                0 ->{
                    Constans.userIconArray[i]?.let { it1 ->
                        holder.setImageResource(R.id.iv_a,
                            it1
                        )
                    }
                }
                1 ->{
                    Constans.userIconArray[i]?.let { it1 ->
                        holder.setImageResource(R.id.iv_b,
                            it1
                        )
                    }
                }
            }
        }
        }
        holder.setText(R.id.tv_user_num,item.userTotalNum.toString())
        holder.setText(R.id.tv_speak_num,item.speakerTotalNum.toString())
        val memberGroup = holder.getView<LinearLayout>(R.id.ll_member_group)
        memberGroup.removeAllViews()
        item.userList?.let {
           it.forEach {
                val memberTextView = TextView(holder.itemView.context)
                memberTextView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0,0,0,1)
                }
                val drawableRight = holder.itemView.resources.getDrawable(R.drawable.vector_message)
                memberTextView.setCompoundDrawablesWithIntrinsicBounds(null,null,drawableRight,null)
                memberTextView.text = it.userName
                memberTextView.gravity = Gravity.CENTER
                memberTextView.setTextColor(holder.itemView.resources.getColor(R.color.font_color))
                memberGroup.addView(memberTextView)
            }
        }


    }
}