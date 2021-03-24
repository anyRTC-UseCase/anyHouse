package org.ar.anyhouse.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import org.ar.anyhouse.R

abstract class BaseActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.app_color).statusBarDarkFont(true).navigationBarColor(R.color.app_color,0.15f).init()
    }
}