package org.ar.anyhouse.vm

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.ar.anyhouse.service.ServiceManager
import org.ar.anyhouse.utils.Constans
import org.ar.anyhouse.utils.SpUtil
import org.ar.anyhouse.utils.toast
import org.ar.anyhouse.view.MainActivity

class LoginVM : ViewModel() {

    val observerLoginResult = MutableLiveData<String>()

    fun Login(nickName:String){
        viewModelScope.launch {
            val signInfo = ServiceManager.instance.login(nickName,viewModelScope).await()
            if (signInfo.code == 0){
                SpUtil.edit { editor ->
                    editor.putString(Constans.USER_ID,signInfo.data.uid)
                    editor.putInt(Constans.USER_ICON,signInfo.data.avatar)
                    editor.putString(Constans.USER_NAME,signInfo.data.userName)
                    editor.putString(Constans.APP_ID,signInfo.data.appId)
                    editor.putString(Constans.HTTP_TOKEN,signInfo.data.userToken)

                }
                observerLoginResult.value = "success"
            }else{
                observerLoginResult.value = signInfo.msg
            }
        }

    }
}