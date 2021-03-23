package org.ar.anyhouse.weight

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.delay
import org.ar.anyhouse.R
import org.ar.anyhouse.databinding.DialogTopTipBinding
import org.ar.anyhouse.utils.launch

class TopTipDialog : DialogFragment() {

    private var _binding: DialogTopTipBinding? = null

    private val binding get() = _binding!!

    private lateinit var tip :String
    private var bgColor = R.color.green_color
    private var cancleable = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogTopTipBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = cancleable
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(bgColor)
                .init()
        binding.llBg.setBackgroundColor(resources.getColor(bgColor))
        binding.tvTip.text=tip
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyDialog)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.let {
            it.setGravity(Gravity.TOP)
            it.setWindowAnimations(R.style.TopAnimation)
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun showDialog(manager: FragmentManager, tip: String?,bgColor:Int = R.color.green_color,cancle:Boolean = true) {
        this.tip = tip.toString()
        this.bgColor = bgColor
        this.cancleable = cancle
        show(manager,"tip")
        if (cancleable) {
            launch({
                delay(2000)
                dismiss()
            })
        }
    }

}