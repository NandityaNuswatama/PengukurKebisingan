package com.soundmeter.application.utils

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.soundmeter.application.databinding.BottomSheetWarningBinding

class BottomSheetWarning : BottomSheetDialogFragment() {
    private var _binding: BottomSheetWarningBinding? = null
    private val binding get() = _binding!!
    
    var okListener: (() -> Unit)? = null
    
    private var title: String? = ""
    private var message: String? = ""
    private var positiveText: String? = "Ok"
    private var negativeText: String? = "Batal"
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetWarningBinding.inflate(
            requireActivity().layoutInflater,
            container,
            false
        )
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun initView() {
        
        binding.title.apply {
            text = title
        }
        
        binding.message.apply {
            text = message
        }
        
        binding.okButton.apply {
            text = positiveText
        }
        
        binding.cancelButton.apply {
            text = negativeText
        }
    }
    
    private fun initListener() {
        
        binding.okButton.setOnClickListener {
            okListener?.invoke()
            dismiss()
        }
        
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }
    
    data class Builder(
        var fragmentManager: FragmentManager,
        var title: String? = null,
        var message: String? = null,
        var positiveText: String? = null,
        var okListener: (() -> Unit)? = null,
    ) {
        
        fun setTitle(title: String?) = apply {
            this.title = title
        }
        
        fun setMessage(message: String?) = apply {
            this.message = message
        }
        
        fun setPositive(text: String = "Ok", okListener: (() -> Unit)?) = apply {
            this.positiveText = text
            this.okListener = okListener
        }
        
        fun show() {
            
            val dialog = BottomSheetWarning()
            
            dialog.title = title
            dialog.message = message
            dialog.positiveText = positiveText
            
            okListener?.let {
                dialog.okListener = okListener
            }
            
            dialog.show(fragmentManager, TAG)
        }
    }
    
    companion object {
        private val TAG: String = BottomSheetWarning::class.java.name
    }
}