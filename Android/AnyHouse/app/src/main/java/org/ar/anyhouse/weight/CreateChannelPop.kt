package org.ar.anyhouse.weight

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.kongzue.dialog.v3.CustomDialog
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.v3.WaitDialog
import com.lxj.xpopup.core.BottomPopupView
import org.ar.anyhouse.R
import org.ar.anyhouse.databinding.LayoutCreateBinding
import org.ar.anyhouse.service.ServiceManager
import org.ar.anyhouse.utils.launch
import org.ar.anyhouse.utils.toast
import org.ar.anyhouse.view.ChannelActivity
import org.ar.anyhouse.view.MainActivity
import org.ar.anyhouse.vm.Channel
import org.ar.anyhouse.vm.MainVM


class CreateChannelPop(context: Context,mainVM: MainVM)  : BottomPopupView(context){


    private var _binding: LayoutCreateBinding? =null //谷歌推荐写法

    private val binding get()= _binding!!

    private val mainVM = mainVM

    private val tipArray = arrayOf(context.getString(R.string.public_channel),context.getString(R.string.privice_channel))

    private var etTopic : EditText ? = null

    override fun onCreate() {
        super.onCreate()
        _binding = LayoutCreateBinding.bind(popupImplView)

        mainVM.curChannelType = 0
        binding.flOpen.isSelected = true
        binding.tvTip.text = tipArray[0]


        binding.flOpen.setOnClickListener {
            binding.flOpen.isSelected = true
            binding.flPassword.isSelected = false
            binding.etPassword.visibility = View.GONE
            mainVM.curChannelType = 0
            if (mainVM.curTopic.isNullOrEmpty()){
                binding.tvTip.text = tipArray[0]
            }else{
                binding.tvTip.text = tipArray[0] + " 主题： "+mainVM.curTopic
            }

        }

        binding.flPassword.setOnClickListener {
            binding.flOpen.isSelected = false
            binding.flPassword.isSelected = true
            binding.etPassword.visibility = View.VISIBLE
            mainVM.curChannelType = 1
            if (mainVM.curTopic.isNullOrEmpty()){
                binding.tvTip.text = tipArray[1]
            }else{
                binding.tvTip.text = tipArray[1] + " 主题： "+mainVM.curTopic
            }
        }

        binding.tvAddTopic.setOnClickListener {
            showSetTopicDialog(mainVM.curTopic)
        }

        binding.btnCreate.setOnClickListener {
            launch({
                val status = ServiceManager.instance.getRoomStatus()
                if (status == 1){//房间未结束
                    showUnFinishDialog()
                }else if (status ==2 || status ==1054){//房间已经结束
                    launch({
                        if (mainVM.curChannelType == 1){
                            if (mainVM.password.isNullOrEmpty()){
                                (context as MainActivity).toast("密码不能为空")
                                return@launch
                            }
                        }
                        val info =ServiceManager.instance.createChannel(mainVM.curChannelType,mainVM.curTopic,mainVM.password,it).await()
                        if (info.code == 0 ){
                            ServiceManager.instance.setChannelInfo(Channel(info.data.roomId,info.data.roomName,info.data.ownerId,info.data.rtmToken,info.data.rtcToken))
                            launch({
                                WaitDialog.show(context as MainActivity,"正在进入...")
                                val loginRTM = mainVM.loginRtm()
                                if (loginRTM) {
                                    (context as MainActivity).startActivity(Intent().apply {
                                        setClass(context as MainActivity, ChannelActivity::class.java)
                                    })
                                }else{
                                    (context as MainActivity).toast("登入rtm失败")
                                }
                                dismiss()
                                WaitDialog.dismiss()
                            })

                        }else{
                            (context as MainActivity).toast("创建失败")
                        }
                    })
                }else{
                    (context as MainActivity).toast("查询房间信息失败～")
                }
            })
        }
        binding.etPassword.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mainVM.password=p0.toString()
            }

        })

    }

    override fun getImplLayoutId(): Int {
        return R.layout.layout_create
    }

    override fun onDismiss() {
        super.onDismiss()
        mainVM.curTopic = ""
        _binding = null
    }

    private fun showSetTopicDialog(tp:String = ""){
        MessageDialog.show(context as MainActivity,context.getString(R.string.add_topic),context.getString(R.string.eg))
            .setCancelable(true)
            .setCustomView(R.layout.layout_topic
            ) { dialog, v ->
                val tip = v?.findViewById<TextView>(R.id.tv_tip)
                etTopic = v?.findViewById<EditText>(R.id.et_topic)
               if (tp.isNotEmpty()){
                   etTopic?.setText(tp)
                   etTopic?.setSelection(tp.length)
                   tip?.text = "还能输入 ${60- tp?.length!!} 个字符"
               }


                etTopic?.addTextChangedListener(object :TextWatcher{
                    override fun afterTextChanged(p0: Editable?) {
                        tip?.text = "还能输入 ${60- p0?.length!!} 个字符"
                    }

                    override fun beforeTextChanged(
                        p0: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                })
            }
            .setOkButton(context.getString(R.string.setting))
            .setCancelButton(context.getString(R.string.cancle)) { baseDialog, v ->
                baseDialog.doDismiss()
                true
            }.setOnOkButtonClickListener { baseDialog, v ->
                if (etTopic?.text.toString().trim().isNullOrEmpty()){
                    mainVM.curTopic = ""
                    binding.tvTip.text = tipArray[mainVM.curChannelType]
                }else{
                    mainVM.curTopic = (etTopic?.text.toString())
                    binding.tvTip.text = tipArray[mainVM.curChannelType]+" 主题： "+mainVM.curTopic
                }
                baseDialog.doDismiss()
                true
            }
    }

    private fun showUnFinishDialog(){
        MessageDialog.show(context as MainActivity,"您上次创建的房间还未结束","继续进入？")
                .setCancelable(true)
                .setOkButton("继续")
                .setOnOkButtonClickListener { baseDialog, v ->
                    baseDialog.doDismiss()
                   launch({
                       WaitDialog.show(context as MainActivity,"正在进入...")
                       val loginRTM = mainVM.loginRtm()
                       if (loginRTM) {
                           (context as MainActivity).startActivity(Intent().apply {
                               setClass(context as MainActivity, ChannelActivity::class.java)
                           })
                       }else{
                           (context as MainActivity).toast("登入rtm失败")
                       }
                       dismiss()
                       WaitDialog.dismiss()
                   })
                    true
                }.cancelButton = "取消"

    }
}