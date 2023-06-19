package com.soundmeter.application.view.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.soundmeter.application.R
import com.soundmeter.application.data.local.SoundEntity
import com.soundmeter.application.databinding.ActivityListDataBinding
import com.soundmeter.application.utils.BottomSheetWarning
import com.soundmeter.application.utils.FileUtils
import com.soundmeter.application.utils.csvFileName
import com.soundmeter.application.utils.showSnackBarWithAction
import com.soundmeter.application.view.detail.DetailActivity
import com.soundmeter.application.view.webview.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListDataBinding
    private var listSounds: MutableList<SoundEntity> = mutableListOf()
    
    private val viewModel: ListViewModel by viewModels()
    
    private val soundAdapter = SoundAdapter()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initObserve()
        initListener()
    }
    
    override fun onResume() {
        super.onResume()
        initView()
    }
    
    private fun initView() {
        viewModel.getListData()
        with(binding.rvSound) {
            adapter = soundAdapter
        }
    }
    
    private fun initObserve() {
        viewModel.soundList.observe(this) { sounds ->
            binding.imgEmpty.isGone = sounds.isNotEmpty()
            listSounds.clear()
            listSounds.addAll(sounds)
            soundAdapter.submitList(sounds)
            soundAdapter.notifyDataSetChanged()
            binding.fabUpload.visibility = if (listSounds.any { !it.isUploaded } && listSounds.isNotEmpty()) View.VISIBLE else View.GONE
            toggleLoading(false)
        }

        viewModel.onSuccessDelete.observe(this) {
            toggleLoading(false)
            showSnackBarWithAction(binding.root, this, getString(R.string.delete_success), true, getString(R.string.ok)) {}
        }

        viewModel.onSuccessUpload.observe(this) {
            toggleLoading(false)
            showSnackBarWithAction(binding.root, this, getString(R.string.upload_success), true, getString(R.string.see)) {
                WebViewActivity.start(this)
            }
        }

        viewModel.onFailureUpload.observe(this) {
            toggleLoading(false)
            showSnackBarWithAction(binding.root, this, getString(R.string.upload_failure, it), false, getString(R.string.retry)) {
                viewModel.uploadData(listSounds)
            }
        }
    }
    
    private fun initListener() {
        with(binding) {
            toolbar.menu.findItem(R.id.export).setOnMenuItemClickListener {
                exportDatabaseToCSVFile()
                true
            }
    
            toolbar.setNavigationOnClickListener {
                finish()
            }
            
            soundAdapter.onItemClick = {
                DetailActivity.start(this@ListDataActivity, it)
            }
            
            soundAdapter.onDeleteClick = {
                BottomSheetWarning.Builder(supportFragmentManager)
                    .setTitle(getString(R.string.confirm_delete_title))
                    .setMessage(getString(R.string.confirm_delete_desc))
                    .setPositive {
                        toggleLoading(true)
                        viewModel.delete(it)
                    }
                    .show()
            }

            fabUpload.setOnClickListener {
                BottomSheetWarning.Builder(supportFragmentManager)
                    .setTitle(getString(R.string.confirm_upload_title))
                    .setMessage(getString(R.string.confirm_upload_desc))
                    .setPositive(getString(R.string.upload)) {
                        toggleLoading(true)
                        viewModel.uploadData(listSounds)
                    }
                    .show()
            }
        }
    }
    
    private fun exportDatabaseToCSVFile() {
        toggleLoading(true)
        val csvFile = FileUtils.generateFile(this, csvFileName)
        if (csvFile != null) {
            FileUtils.exportMoviesWithDirectorsToCSVFile(csvFile, listSounds)
            toggleLoading(false)
            showSnackBarWithAction(binding.root, this, getString(R.string.csv_success), true, getString(R.string.see)) {
                val intent = FileUtils.goToFileIntent(this, csvFile)
                startActivity(intent)
            }
        } else {
            showSnackBarWithAction(binding.root, this, getString(R.string.csv_failed), false, getString(R.string.retry)) {
                exportDatabaseToCSVFile()
            }
        }
    }

    private fun toggleLoading(show: Boolean) {
        with(binding.progressBar) {
            visibility = if (show) View.VISIBLE else View.GONE
        }
    }
    
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ListDataActivity::class.java)
            context.startActivity(intent)
        }
    }
}