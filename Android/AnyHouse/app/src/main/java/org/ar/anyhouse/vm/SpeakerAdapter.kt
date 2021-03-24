package org.ar.anyhouse.vm

import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.ar.anyhouse.R
import org.ar.anyhouse.utils.Constans
import org.ar.anyhouse.utils.SpeakerPayload
import org.ar.rtc.IRtcEngineEventHandler


class SpeakerAdapter : BaseQuickAdapter<Speaker,BaseViewHolder>(R.layout.item_speaker) {
    override fun convert(holder: BaseViewHolder, item: Speaker) {
        holder.setText(R.id.tv_name,if (item.isHoster){"🎉"+item.userName}else{item.userName})
        holder.setVisible(R.id.muted,!item.isOpenAudio)
        holder.setImageResource(R.id.iv_user, Constans.userIconArray[item.userIcon]!!)

    }

    override fun convert(holder: BaseViewHolder, item: Speaker, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        if (payloads.isNullOrEmpty()){
            convert(holder,item)
            return
        }
        payloads.forEach {
            when(val payload = it as SpeakerPayload){
                SpeakerPayload.VOLUME -> {
                    val volumeInfo = payload.data as IRtcEngineEventHandler.AudioVolumeInfo
                    val view = holder.getView<View>(R.id.view_audio_border)

                    if (volumeInfo.volume>10){
                            view.clearAnimation()
                            val alphaAnim = AlphaAnimation(1.0f, 1f)
                            alphaAnim.duration = 1000
                            alphaAnim.setAnimationListener(object : AnimationListener {
                                override fun onAnimationRepeat(p0: Animation?) {
                                }
                                override fun onAnimationEnd(animation: Animation) {
                                    view.visibility= (View.INVISIBLE)
                                }

                                override fun onAnimationStart(p0: Animation?) {
                                    view.visibility=(View.VISIBLE)
                                }
                            })
                            view.startAnimation(alphaAnim)
                    }else{
                    }
                }

                SpeakerPayload.AUDIO ->{
                    holder.setVisible(R.id.muted,!(payload.data as Boolean))
                }

            }
        }





    }
}