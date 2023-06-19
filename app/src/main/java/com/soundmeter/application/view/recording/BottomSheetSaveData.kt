package com.soundmeter.application.view.recording

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orhanobut.hawk.Hawk
import com.soundmeter.application.R
import com.soundmeter.application.databinding.BottomSheetSaveDataBinding
import com.soundmeter.application.utils.DateUtils
import com.soundmeter.application.utils.LATITUDE
import com.soundmeter.application.utils.LONGITUDE

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
        isCancelable = false
        initView()
        initListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        isCancelable = false
        with(binding) {
            val areas = resources.getStringArray(R.array.array_area)
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, areas)
            edtArea.setAdapter(adapter)
            edtDate.setText(DateUtils.getDateFromMillis(System.currentTimeMillis()))
            edtLatitude.setText(Hawk.get<Double>(LATITUDE).toString())
            edtLongitude.setText(Hawk.get<Double>(LONGITUDE).toString())
        }
    }

    private fun initListener() {
        with(binding) {
            edtArea.doAfterTextChanged {
                binding.btnOk.isEnabled = it.toString().isNotEmpty()
            }
            btnOk.setOnClickListener {
                okListener?.invoke(Pair(edtArea.text.toString(), edtSubtitle.text.toString()))
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