package com.soundmeter.application.view.recording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.soundmeter.application.databinding.BottomSheetSaveDataBinding

class BottomSheetSaveData : BottomSheetDialogFragment() {
    private var _binding: BottomSheetSaveDataBinding? = null
    private val binding get() = _binding!!
    
    var okListener: ((Pair<String, String?>) -> Unit)? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSaveDataBinding.inflate(
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
        
        binding.edtTitle.doAfterTextChanged {
            binding.btnOk.isEnabled = it.toString().isNotEmpty()
        }
    }
    
    private fun initListener() {
        
        with(binding) {
            btnOk.setOnClickListener {
                okListener?.invoke(Pair(edtTitle.text.toString(), edtSubtitle.text.toString()))
                dismiss()
            }
            
            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }
    
    data class Builder(
        var fragmentManager: FragmentManager,
        var okListener: ((Pair<String, String?>) -> Unit)? = null,
    ) {
        
        fun setButton(okListener: ((Pair<String, String?>) -> Unit)?) = apply {
            this.okListener = okListener
        }
        
        fun show() {
            
            val dialog = BottomSheetSaveData()
            
            okListener?.let {
                dialog.okListener = okListener
            }
            
            dialog.show(fragmentManager, TAG)
        }
    }
    
    companion object {
        private val TAG: String = BottomSheetSaveData::class.java.name
    }
}