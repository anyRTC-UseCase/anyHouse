package org.ar.anyhouse.view

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.v3.WaitDialog
import com.lxj.xpopup.XPopup
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.coroutines.delay
import org.ar.anyhouse.R
import org.ar.anyhouse.databinding.ActivityMainBinding
import org.ar.anyhouse.sdk.RtcManager
import org.ar.anyhouse.sdk.RtmManager
import org.ar.anyhouse.service.ServiceManager
import org.ar.anyhouse.utils.Constans
import org.ar.anyhouse.utils.clickWithTrigger
import org.ar.anyhouse.utils.launch
import org.ar.anyhouse.utils.toast
import org.ar.anyhouse.vm.ChannelListDiffCallback
import org.ar.anyhouse.vm.MainVM
import org.ar.anyhouse.weight.CreateChannelPop
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.system.exitProcess

class MainActivity : BaseActivity() {

    private val mainVM: MainVM by viewModels()

    private lateinit var binding: ActivityMainBinding
    private val channelListAdapter = ChannelListAdapter()
    private var exitTime = 0L
    private var pwdEdittext: EditText? = null
    private var permissionOk = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivUser.setImageResource(Constans.userIconArray[ServiceManager.instance.getSelfInfo()?.userIcon]!!)
        initListAdapter()
        RtmManager.instance.init(this)//放这因为登录后才能获取到appid
        launch({
            permissionOk = if (AndPermission.hasPermissions(this,Permission.WRITE_EXTERNAL_STORAGE, Permission.RECORD_AUDIO)){
                true
            }else{
                requestPermission()
            }
        })


        mainVM.observerRoomList.observe(this, Observer {
            channelListAdapter.setDiffNewData(it.data.list)
            if (mainVM.pageNum == 1){
                binding.refreshLayout.finishRefresh()
            }else{
                binding.refreshLayout.finishLoadMore()
            }

        })



        mainVM.observerJoinChannel.observe(this, Observer {
            if (it.code == 0) {
                launch({
                    val loginRTM = mainVM.loginRtm()
                    if (loginRTM) {
                        startActivity(Intent().apply {
                            setClass(this@MainActivity, ChannelActivity::class.java)
                        })
                    }else{
                        toast("登入rtm失败")
                    }
                    WaitDialog.dismiss()
                })

            } else {
                WaitDialog.dismiss()
                toast("加入失败 ${it.msg}")
            }
        })


        binding.llUser.setOnClickListener {
            startActivity(Intent().apply {
                setClass(this@MainActivity, SettingActivity::class.java)
            })
        }

        binding.btnCreate.clickWithTrigger {
            XPopup.Builder(this@MainActivity).enableDrag(true).isDestroyOnDismiss(true).asCustom(CreateChannelPop(this@MainActivity, mainVM)).show()
        }

        binding.refreshLayout.setOnRefreshListener {
            launch({
                mainVM.pageNum = 1
                mainVM.getRoomList(1)
            },{
                binding.refreshLayout.finishRefresh()
                toast("发生错误")
            })
        }

        binding.refreshLayout.setOnLoadMoreListener {
            launch({
                if (mainVM.haveNext != 0){
                    mainVM.pageNum ++
                    mainVM.getRoomList(mainVM.pageNum)
                }else{
                    binding.refreshLayout.finishLoadMoreWithNoMoreData()
                }
            },{
                binding.refreshLayout.finishLoadMore()
                toast("发生错误")
            })
        }

    }

    private fun initListAdapter() {
        binding.rvChannel.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = channelListAdapter
        }
        channelListAdapter.setEmptyView(R.layout.lauoyt_empty_room_list)
        channelListAdapter.setDiffCallback(ChannelListDiffCallback())
        channelListAdapter.setOnItemClickListener { adapter, view, position ->
            val item = channelListAdapter.getItem(position)
            if (item.isPrivate == 1) {
                showPwdInputDialog(channelListAdapter.getItem(position).roomId)
            } else {
                WaitDialog.show(this,"正在进入...")
                mainVM.joinRoom(item.roomId)
            }
        }
    }


    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            toast("再按一次退出")
            exitTime = System.currentTimeMillis()
        } else {
            RtcManager.instance.release()
            RtmManager.instance.release()
            finish()
            exitProcess(0)
        }

    }


    private suspend fun requestPermission() = suspendCoroutine<Boolean> {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.RECORD_AUDIO)
                .onGranted { _: List<String?>? ->
                    it.resume(true)
                }
                .onDenied { _: List<String?>? ->
                    it.resume(false)

                }
                .start()
    }

    private fun showPwdInputDialog(roomId: String) {
        MessageDialog.show(this, "输入密码", "请输入该房间密码，最长8位数字")
                .setCancelable(true)
                .setCustomView(R.layout.layout_modify_name
                ) { dialog, v ->
                    pwdEdittext = v.findViewById<EditText>(R.id.et_name)
                }
                .setOkButton("确定")
                .setCancelButton("取消") { baseDialog, v ->
                    baseDialog.doDismiss()
                    true
                }.setOnOkButtonClickListener { baseDialog, v ->
                    if (pwdEdittext?.text?.trim().isNullOrEmpty()){
                        toast("密码不能为空")
                        false
                    }
                   baseDialog.doDismiss()
                    WaitDialog.show(this,"正在进入...")
                    mainVM.joinRoom(roomId,pwdEdittext?.text.toString())
                    true
                }
    }


    override fun onResume() {
        super.onResume()
        binding.tvUserName.text = ServiceManager.instance.getSelfInfo()?.userName
        mainVM.getRoomList(1)

    }

}