package org.ar.anyhouse.weight

import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.gyf.immersionbar.ImmersionBar
import org.ar.anyhouse.R
import org.ar.anyhouse.databinding.DialogInviteBinding
import org.ar.anyhouse.vm.ChannelVM


class InviteDialogFragment constructor(channelVM: ChannelVM): DialogFragment() {

    private var _binding: DialogInviteBinding? = null

    private val binding get() = _binding!!

    private val channelVM = channelVM

    private var dismissCallback: OnDissmissCallback? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogInviteBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.green_color)
                .init()
        binding.btnJoin.setOnClickListener {
            channelVM.acceptLine()
            channelVM.changeRoleToSpeaker()
            dismiss()
        }
        binding.btnLater.setOnClickListener {
            channelVM.rejectLine()
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.MyDialog)
        isCancelable = false


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


    fun show(manager: FragmentManager,dismissCallback:OnDissmissCallback) {
        show(manager,"invite")
        this.dismissCallback = dismissCallback
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissCallback?.let {
            it.dismiss()
        }
    }

    interface OnDissmissCallback{
        fun dismiss()
    }
}