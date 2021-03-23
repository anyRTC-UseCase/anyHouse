package org.ar.anyhouse.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import org.ar.anyhouse.databinding.ActivityLoginBinding
import org.ar.anyhouse.sdk.RtmManager
import org.ar.anyhouse.service.ServiceManager
import org.ar.anyhouse.utils.Constans
import org.ar.anyhouse.utils.SpUtil
import org.ar.anyhouse.utils.launch
import org.ar.anyhouse.utils.toast
import org.ar.rtm.ErrorInfo
import org.ar.rtm.ResultCallback
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
            val userId = SpUtil.get().getString(Constans.USER_ID,"")
            if (!userId.isNullOrEmpty()){//有userId 说明已经登录过了
                    startActivity(Intent().apply {
                        setClass(this@LoginActivity, MainActivity::class.java)
                    })
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                    finish()
            }
        binding.etName.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.btnLogin.isSelected = p0?.length!! >= 1
            }

        })

        binding.btnLogin.setOnClickListener {
            launch({ it ->
                if (binding.etName.text.isEmpty()){
                        toast("请输入昵称")
                        return@launch
                    }

                val signInfo = ServiceManager.instance.login(binding.etName.text.toString(),it).await()
                if (signInfo.code == 0){
                    SpUtil.edit { editor ->
                        editor.putString(Constans.USER_ID,signInfo.data.uid)
                        editor.putInt(Constans.USER_ICON,signInfo.data.avatar)
                        editor.putString(Constans.USER_NAME,signInfo.data.userName)
                        editor.putString(Constans.APP_ID,signInfo.data.appId)
                        editor.putString(Constans.HTTP_TOKEN,signInfo.data.userToken)

                    }
                        startActivity(Intent().apply {
                            setClass(this@LoginActivity, MainActivity::class.java)
                        })
                         overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                        finish()
                }else{
                    toast(signInfo.msg)
                }
            })


        }
    }


}

