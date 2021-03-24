package org.ar.anyhouse.utils

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.ar.anyhouse.App
import rxhttp.wrapper.exception.HttpStatusCodeException
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

fun Activity.toast(str: String) {
    Toast.makeText(App.app.applicationContext,str, Toast.LENGTH_SHORT).show()
}

fun Float.dp():Int{
    return (this * App.app.resources.displayMetrics.density).roundToInt()
}

fun Float.dp2px():Int{
    return (0.5f + this * App.app.resources.displayMetrics.density).roundToInt()
}


inline operator fun <T> MutableLiveData<T>.plusAssign(value: T?) {
    setValue(value)
}

inline fun <T> Boolean.ternary(trueValue: T, falseValue: T): T {
    return if (this) {
        trueValue
    } else {
        falseValue
    }
}


/**
 * 默认主线程的协程
 */
fun launch(
    block: suspend (CoroutineScope) -> Unit,
    error_: ((e: Throwable) -> Unit)? = null,
    context: CoroutineContext = Dispatchers.Main
) = GlobalScope.launch(context + CoroutineExceptionHandler { _, e ->
    error_?.let { it(e) }
}) {
    try {
        block(this)
    } catch (e: Exception) {
        e.printStackTrace()
        if (e is HttpStatusCodeException) {
            if (e.statusCode == "401") {
                App.app.cleanAndReLogin()
            }
        }
        error_?.let { it(e) }
    }
}


/**
 * 默认主线程的协程
 * 添加生命周期管理
 */
fun launchWithLife(
    life: LifecycleOwner?,
    block: suspend (CoroutineScope) -> Unit,
    error_: ((e: Throwable) -> Unit)? = null,
    context: CoroutineContext = Dispatchers.Main
) {
    val job = launch(block, error_, context)

    life?.lifecycle?.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (life.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                job.cancel("the lifecycleOwner(${life.javaClass.simpleName}) has been destroyed")
                return;
            }
        }
    })
}

fun RecyclerView.divider(
    block: DefaultDecoration.() -> Unit
): RecyclerView {
    val itemDecoration = DefaultDecoration(context).apply(block)
    addItemDecoration(itemDecoration)
    return this
}

/**
 * 指定Drawable资源为分割线, 分割线的间距和宽度应在资源文件中配置
 * @param drawable 描述分割线的drawable
 * @param orientation 分割线方向, 仅[androidx.recyclerview.widget.GridLayoutManager]需要使用此参数, 其他LayoutManager都是根据其方向自动推断
 */
fun RecyclerView.divider(
    @DrawableRes drawable: Int,
    orientation: DefaultDecoration.DividerOrientation = DefaultDecoration.DividerOrientation.HORIZONTAL
): RecyclerView {
    return divider {
        setDrawable(drawable)
        this.orientation = orientation
    }
}

fun <T : View> T.withTrigger(delay: Long = 800): T {
    triggerDelay = delay
    return this
}

/***
 * 点击事件的View扩展
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.click(block: (T) -> Unit) = setOnClickListener {
//    ClickUtils.applyPressedViewAlpha(this,0.8f)
    if (clickEnable()) {
        block(it as T)
    }
}

/***
 * 带延迟过滤的点击事件View扩展
 * @param delay Long 延迟时间，默认800毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.clickWithTrigger(time: Long = 800, block: (T) -> Unit) {
    // 此处是点击后按钮背景透明度变化 可参考blankj开源工具类
//    ClickUtils.applyPressedViewAlpha(this,0.6f)
    triggerDelay = time
    setOnClickListener {
        if (clickEnable()) {
            block(it as T)
        }
    }
}

private var <T : View> T.triggerLastTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else 0
    set(value) {
        setTag(1123460103, value)
    }

private var <T : View> T.triggerDelay: Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else -1
    set(value) {
        setTag(1123461123, value)
    }

private fun <T : View> T.clickEnable(): Boolean {
    var flag = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        flag = true
    }
    triggerLastTime = currentClickTime
    return flag
}

