package org.ar.anyhouse.weight

import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.lxj.xpopup.core.BottomPopupView
import org.ar.anyhouse.*
import org.ar.anyhouse.utils.RaisedHandsPayload
import org.ar.anyhouse.view.ChannelActivity
import org.ar.anyhouse.vm.ChannelVM
import org.ar.anyhouse.view.RaisedHandsListAdapter
import org.ar.anyhouse.vm.RaisedHandsMemberDiffCallback

class RaisedHandsListPop(ctx: Context,channelVM: ChannelVM)  : BottomPopupView(ctx),OnItemChildClickListener{

    private lateinit var rvList : RecyclerView
    private lateinit var raisedHandsListAdapter : RaisedHandsListAdapter
    private val channelVM: ChannelVM = channelVM
    private val ctx = ctx

    override fun getImplLayoutId(): Int {

        return R.layout.layout_raised_hands_list
    }

    override fun onCreate() {
        super.onCreate()
        rvList  = findViewById(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(context)
        raisedHandsListAdapter = RaisedHandsListAdapter()
        raisedHandsListAdapter.setDiffCallback(RaisedHandsMemberDiffCallback())
        rvList.adapter = raisedHandsListAdapter
        raisedHandsListAdapter.setEmptyView(R.layout.layout_empty_raised)
        raisedHandsListAdapter.addChildClickViewIds(R.id.btn_invite)
        raisedHandsListAdapter.setOnItemChildClickListener(::onItemChildClick)

        channelVM.observeRaisedHandsList.observe(ctx as ChannelActivity, Observer {
            raisedHandsListAdapter.setDiffNewData(it)
        })

        channelVM.observeInviteStatus.observe(ctx as ChannelActivity, Observer {
            raisedHandsListAdapter.notifyItemChanged(it,RaisedHandsPayload.INVITE(true))
        })


    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        channelVM.inviteLine(raisedHandsListAdapter.getItem(position).userId,true)
    }
}