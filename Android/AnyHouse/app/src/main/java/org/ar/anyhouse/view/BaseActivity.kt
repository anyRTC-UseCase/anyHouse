package org.ar.anyhouse.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.kongzue.dialog.interfaces.OnDismissListener
import com.kongzue.dialog.util.BaseDialog
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.v3.TipDialog
import org.ar.anyhouse.R
import org.ar.anyhouse.sdk.RtmConnectStateListener
import org.ar.anyhouse.sdk.RtmManager

abstract class BaseActivity : AppCompatActivity(), RtmConnectStateListener {

    private var isReconnect = false
    private var reconnectDialog: MessageDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.app_color).statusBarDarkFont(true).navigationBarColor(R.color.app_color,0.15f).init()
        RtmManager.instance.registerConnectListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        RtmManager.instance.registerConnectListener(this)
    }

    override fun onStop() {
        super.onStop()
        RtmManager.instance.unregisterConnectListener(this)
    }
    override fun onConnectionStateChanged(state: Int, var2: Int) {
        runOnUiThread {
            if (state == 4) {
                isReconnect = true
                reconnectDialog = MessageDialog.build(this@BaseActivity)
                    .setTitle("提示")
                    .setMessage("正在重连....")
                    .setOnOkButtonClickListener { baseDialog: BaseDialog?, v: View? -> true }
                    .setCancelable(false)
                reconnectDialog?.show()
            } else if (state == 3) {
                if (isReconnect) {
                    if (reconnectDialog != null) {
                        reconnectDialog!!.doDismiss()
                    }
                    isReconnect = false
                    TipDialog.show(this@BaseActivity, "重连成功！", TipDialog.TYPE.SUCCESS).onDismissListener = OnDismissListener { }
                }
            }
        }
    }
}