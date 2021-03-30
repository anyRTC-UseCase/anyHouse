package org.ar.anyhouse

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.kongzue.dialog.util.DialogSettings
import com.tencent.bugly.Bugly
import okhttp3.OkHttpClient
import org.ar.anyhouse.sdk.RtcManager
import org.ar.anyhouse.sdk.RtmManager
import org.ar.anyhouse.service.ServiceManager
import org.ar.anyhouse.utils.Constans
import org.ar.anyhouse.utils.Constans.HTTP_TOKEN
import org.ar.anyhouse.utils.SpUtil
import org.ar.anyhouse.utils.toast
import org.ar.anyhouse.view.LoginActivity
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.ssl.HttpsUtils
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession
import kotlin.properties.Delegates


class App : Application(),Application.ActivityLifecycleCallbacks{

    private var activity : Activity? = null

    companion object{
        var app : App by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        Bugly.init(this, "3ea89c6cab", true)
        registerActivityLifecycleCallbacks(this)
        SpUtil.init(this)
        DialogSettings.style = DialogSettings.STYLE.STYLE_IOS
        RxHttp.init(getDefaultOkHttpClient(),true)
        RxHttp.setOnParamAssembly {
            val token = SpUtil.get().getString(HTTP_TOKEN,"")
            token?.let {token ->
                it.addHeader("Authorization","Bearer $token")
            }
            it
        }


    }

    private fun getDefaultOkHttpClient(): OkHttpClient? {
        val sslParams = HttpsUtils.getSslSocketFactory()
        return OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager) //添加信任证书
                .hostnameVerifier(HostnameVerifier { hostname: String?, session: SSLSession? -> true }) //忽略host验证
                .build()
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityResumed(p0: Activity) {
        activity = p0
    }

    fun cleanAndReLogin(){

        SpUtil.edit { editor ->
            editor.putString(Constans.USER_ID,"")
            editor.putInt(Constans.USER_ICON,-1)
            editor.putString(Constans.USER_NAME,"")
            editor.putString(Constans.APP_ID,"")
            editor.putString(Constans.HTTP_TOKEN,"")
        }
        ServiceManager.instance.clean()
        RtmManager.instance.leaveChannel()
        RtmManager.instance.logOut()
        RtmManager.instance.release()
        RtcManager.instance.release()
        activity?.let {
            it.toast("登录已失效")
            it.startActivity(Intent().apply {
                setClass(it,LoginActivity::class.java)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }

}

