package org.ar.anyhouse.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.kongzue.dialog.util.BaseDialog
import com.kongzue.dialog.v3.BottomMenu
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.v3.TipDialog
import com.kongzue.dialog.v3.WaitDialog
import com.lxj.xpopup.XPopup
import kotlinx.coroutines.delay
import org.ar.anyhouse.R
import org.ar.anyhouse.databinding.ActivityChannelBinding
import org.ar.anyhouse.utils.*
import org.ar.anyhouse.vm.*
import org.ar.anyhouse.weight.InviteDialogFragment
import org.ar.anyhouse.weight.RaisedHandsListPop
import org.ar.anyhouse.weight.TopTipDialog
import org.ar.rtc.Constants


class ChannelActivity : BaseActivity() {
    private lateinit var binding: ActivityChannelBinding
    private lateinit var speakerAdapter: SpeakerAdapter
    private lateinit var listenerAdapter: ListenerAdapter
    private var isReconnect = false

    private val channelVM: ChannelVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        channelVM.channelInfo.observe(this, Observer {
            if (it.isNull()){
                toast("未能获取到房间信息，请重新尝试进入")
                finish()
                return@Observer
            }else {
                binding.tvRoomTopic.apply {
                    text = it.channelName
                    setCompoundDrawablesWithIntrinsicBounds(0,0,(it.roomType==1).ternary(R.drawable.vector_drawable_password,R.drawable.vector_channel),0)
                }
            }
        })
        binding.tvUserName.text = channelVM.getSelf()?.userName
        binding.ivUser.setImageResource(Constans.userIconArray[channelVM.getSelf()?.userIcon]!!)
        binding.tvRoomTopic.setOnClickListener {
            if (!channelVM.isOpenRoom()){
                //todo : 这里可以加一个点击显示密码的功能
               // XPopup.Builder(this).atView(binding.tvRoomTopic).asAttachList(arrayOf(channelVM.getChannelInfo().channelId),null,null).show()
            }
        }
        initAdapter()
        initRvList()
        initLiveData()
        join()


    }

    private fun join() {
        channelVM.initSDK(this)
        channelVM.joinChannelSDK()
    }


    private fun initLiveData() {
        if (channelVM.isMeHost()) {
            binding.flRaisedHands.visibility = View.VISIBLE
            binding.ivMic.visibility = View.VISIBLE
            channelVM.observeRaisedHandsList.observe(this, Observer {
                if (it.size == 0) {
                    binding.viewRaisedHandsTip.visibility = View.GONE
                } else {
                    binding.viewRaisedHandsTip.visibility = View.VISIBLE
                }
            })
        } else {
            binding.ivApply.visibility = View.VISIBLE
            channelVM.observeReceiveInvite.observe(this, Observer {
                if (it) {
                    InviteDialogFragment(channelVM).show(supportFragmentManager,
                        object : InviteDialogFragment.OnDissmissCallback {
                            override fun dismiss() {
                                binding.ivApply.isSelected = false
                            }
                        })
                }
            })
            channelVM.observeMyRoleChange.observe(this, Observer {
                if (it == Constants.CLIENT_ROLE_AUDIENCE) {
                    binding.ivApply.isSelected = false
                    binding.ivApply.visibility = View.VISIBLE
                    binding.ivMic.visibility = View.GONE
                    binding.ivMic.isSelected = false //恢复原样
                    channelVM.muteLocalAudio(false)
                    TopTipDialog().showDialog(
                        supportFragmentManager,
                        getString(R.string.role_changed)
                    )
                } else {
                    binding.ivApply.visibility = View.GONE
                    binding.ivMic.visibility = View.VISIBLE
                }
            })
            channelVM.observerCloseMic.observe(this, Observer {
                if (it) {
                    binding.ivMic.isSelected = true
                }
            })
            channelVM.observeHostStatus.observe(this, Observer {
                binding.tvHostLeave.visibility = if (it!=0){View.VISIBLE}else{View.GONE}

            })
        }

        channelVM.observerSpeakerVolume.observe(this, Observer { it ->
            it?.forEach {
                val userId = if (it.uid == "0") {
                    channelVM.getSelf()?.userId
                } else {
                    it.uid
                }
                speakerAdapter.data.forEachIndexed { index, speaker ->
                    if (userId == speaker.userId) {
                        speakerAdapter.notifyItemChanged(index, SpeakerPayload.VOLUME(it))
                    }
                }

            }
        })

        channelVM.observerHosterLeave.observe(this, Observer {
            toast(getString(R.string.host_leave))
            leave()
        })

        channelVM.observeRtcStatus.observe(this, Observer {
            when (it) {
                110 -> {
                    toast("rtc Token 验证失败")
                    channelVM.leaveChannelSDK()
                    finish()
                }
            }
        })

        channelVM.observeReject.observe(this, Observer {
            TopTipDialog().showDialog(supportFragmentManager,"$it 拒绝了你的邀请",R.color.error_red)
        })
        channelVM.observerTokenPrivilegeWillExpire.observe(this, Observer {
            toast("体验时间已到")
            channelVM.leaveChannelSDK()
            finish()
        })
        channelVM.observeConnectStatus.observe(this, Observer { state->
            if (state == 4) {
                isReconnect = true
                WaitDialog.show(this,"正在重连...").cancelable=false
            } else if (state == 3) {
                if (isReconnect) {
                    isReconnect = false
                    WaitDialog.dismiss()
                    channelVM.getListenerListFormHttp(false)//重连成功后重新拉取听众列表 防止断网期间有人离开没更新
                    channelVM.sendSelfInfo()//重连成功后发送个人信息
                    if (channelVM.isMeHost()){//重连成功后重新拉取下举手列表
                        channelVM.getRaisedHandsListFormHttp()
                    }
                    TipDialog.show(this, "重连成功！", TipDialog.TYPE.SUCCESS)
                }
            }
        })

    }


    private fun initAdapter() {
        speakerAdapter = SpeakerAdapter()
        speakerAdapter.setOnItemClickListener { adapter, view, position ->
            if (channelVM.isMeHost() && !speakerAdapter.getItem(position).isHoster) {
                val array = if (speakerAdapter.getItem(position).isOpenAudio) {
                    arrayOf(
                        getString(R.string.set_as_listener),
                        getString(R.string.close_other_mic)
                    )
                } else {
                    arrayOf(getString(R.string.set_as_listener))
                }
                BottomMenu.show(this@ChannelActivity, array) { text, index ->
                    when (index) {
                        0 -> {
                            channelVM.changeRoleGuest(speakerAdapter.getItem(position).userId)
                        }
                        1 -> {
                            channelVM.muteRemoteMic(speakerAdapter.getItem(position).userId)
                        }
                    }
                }
            } else if (channelVM.isMe(speakerAdapter.getItem(position).userId) && !speakerAdapter.getItem(
                    position
                ).isHoster
            ) {
                //是自己下麦
                BottomMenu.show(
                    this@ChannelActivity,
                    arrayOf(getString(R.string.hang_up))
                ) { text, index ->
                    channelVM.changeRoleToListener()
                }
            }

        }
        listenerAdapter = ListenerAdapter()
        listenerAdapter.setOnItemClickListener { adapter, view, position ->
            if (channelVM.isMeHost()) {
                BottomMenu.reset()
                BottomMenu.show(
                    this@ChannelActivity,
                    listenerAdapter.getItem(position).isInvite.ternary( arrayOf(getString(R.string.has_invited)),  arrayOf(getString(R.string.invite_person)))
                ) { text, index ->
                    if (text == getString(R.string.has_invited)){
                        toast("已经发送过邀请")
                    }else{
                        channelVM.inviteLine(listenerAdapter.getItem(position).userId)
                        listenerAdapter.getItem(position).isInvite=true
                    }

                }
            }
        }

        listenerAdapter.setDiffCallback(ListenerDiffCallback())
        speakerAdapter.setDiffCallback(SpeakerDiffCallback())

        channelVM.observerSpeakList.observe(this, Observer {
            speakerAdapter.setDiffNewData(it)
        })
        channelVM.observerListenerList.observe(this, Observer {
            listenerAdapter.setDiffNewData(it)
        })

    }

    private fun initRvList() {
        val speakerLayoutManager = GridLayoutManager(this, 3)
        val listenerLayoutManager = GridLayoutManager(this, 4)
        binding.rvSpeaker.layoutManager = speakerLayoutManager
        binding.rvListener.layoutManager = listenerLayoutManager
        binding.rvSpeaker.adapter = speakerAdapter
        binding.rvListener.adapter = listenerAdapter
        binding.refreshLayout.setOnRefreshListener {
            channelVM.getListenerListFormHttp(false)
            it.finishRefresh(400)
        }

    }


    fun raisedHands(view: View) {
        if (channelVM.isHostOnline()) {
            if (view.isSelected) {
                channelVM.cancleApplyLine()
            } else {
                channelVM.applyLine()
                TopTipDialog().showDialog(
                    supportFragmentManager,
                    getString(R.string.raisedHandsTip)
                )
            }
            view.isSelected = !view.isSelected
        }else {
            toast("主持人已经离开")
        }
    }

    fun leaveChannel(view: View) {
        leave()
    }

    private fun leave() {
        if (channelVM.isMeHost()) {
            showLeaveDialog()
        } else {
            channelVM.leaveChannelSDK()
            finish()
        }
    }

    private fun showLeaveDialog() {
        MessageDialog.show(this, getString(R.string.leave), getString(R.string.leave_tip))
            .setCancelable(false)
            .setOkButton(getString(R.string.leave))
            .setCancelButton(getString(R.string.cancle)) { baseDialog, v ->
                baseDialog.doDismiss()
                true
            }.setOnOkButtonClickListener { baseDialog, v ->
                    baseDialog.doDismiss()
                    channelVM.leaveChannelSDK()
                    finish()
                true
            }
    }

    fun raisedHandsListClick(view: View) {
        XPopup.Builder(this@ChannelActivity).enableDrag(true)
            .asCustom(RaisedHandsListPop(this@ChannelActivity, channelVM)).show()
    }

    fun micClick(view: View) {
        if (view.isSelected) {
            channelVM.muteLocalAudio(false)
        } else {
            channelVM.muteLocalAudio(true)
        }
        view.isSelected = !view.isSelected
    }


    override fun onBackPressed() {
        leave()
    }


}