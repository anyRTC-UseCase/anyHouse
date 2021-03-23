package org.ar.anyhouse.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.kongzue.dialog.v3.MessageDialog
import org.ar.anyhouse.BuildConfig
import org.ar.anyhouse.R
import org.ar.anyhouse.databinding.LayoutSettingBinding
import org.ar.anyhouse.service.ServiceManager
import org.ar.anyhouse.utils.Constans
import org.ar.anyhouse.utils.SpUtil
import org.ar.anyhouse.utils.launch
import org.ar.anyhouse.utils.toast
import org.ar.rtc.RtcEngine

class SettingActivity : BaseActivity() {
    private lateinit var binding: LayoutSettingBinding
    private var  etName :EditText? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvUser.text = ServiceManager.instance.getSelfInfo()?.userName
        binding.ivUser.setImageResource(Constans.userIconArray[ServiceManager.instance.getSelfInfo()?.userIcon]!!)


        binding.tvSdkVersion.text = "v ${RtcEngine.getSdkVersion()}"
        binding.tvAppVersion.text = "v ${BuildConfig.VERSION_NAME}"
        binding.tvTime.text = packageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA).metaData["releaseTime"].toString()

    }

    fun modifyNameClick(view: View) {
        showModifyNameDialog(ServiceManager.instance.getSelfInfo()?.userName.toString())
    }
    fun privacyCLick(view: View) {
        goH5("https://www.anyrtc.io/hide")
    }
    fun statementClick(view: View) {
        goH5("https://www.anyrtc.io/termsOfService")
    }
    fun registerClick(view: View) {
        goH5("https://console.anyrtc.io/signup")
    }
    fun closeClick(view: View) {
        finish()
    }

    private fun goH5(url:String){
        startActivity(Intent().apply {
            setAction("android.intent.action.VIEW")
            setData(Uri.parse(url))
        })
    }

    private fun showModifyNameDialog(name:String = ""){
        MessageDialog.show(this,"修改昵称","最多输入 8 个字符")
            .setCancelable(true)
            .setCustomView(R.layout.layout_modify_name
            ) { dialog, v ->
                etName = v.findViewById<EditText>(R.id.et_name)
                etName?.let {
                    it.setText(name)
                    it.setSelection(name.length)
                }

            }
            .setOkButton("确定")
            .setCancelButton("取消") { baseDialog, v ->
                baseDialog.doDismiss()
                true
            }.setOnOkButtonClickListener { baseDialog, v ->
                    launch({
                        if (etName?.text.toString().trim().isNullOrEmpty()){
                            toast("昵称不能为空")
                            return@launch
                        }
                        val result = ServiceManager.instance.updateUserName(etName?.text.toString(),it).await()
                        if (result.code == 0){
                            toast("修改成功")
                            binding.tvUser.text = result.data.userName
                            ServiceManager.instance.getSelfInfo()?.userName = result.data.userName
                            SpUtil.edit { editor ->
                                editor.putString(Constans.USER_NAME,result.data.userName)
                            }
                            baseDialog.doDismiss()
                        }else{
                            toast("修改失败")
                        }
                    })


                true
            }
    }
}