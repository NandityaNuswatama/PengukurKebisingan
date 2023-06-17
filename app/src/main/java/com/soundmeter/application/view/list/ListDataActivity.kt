package com.soundmeter.application.view.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.soundmeter.application.R
import com.soundmeter.application.data.local.SoundEntity
import com.soundmeter.application.databinding.ActivityListDataBinding
import com.soundmeter.application.utils.BottomSheetWarning
import com.soundmeter.application.utils.FileUtils
import com.soundmeter.application.utils.csvFileName
import com.soundmeter.application.view.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ListDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListDataBinding
    private lateinit var listSounds: List<SoundEntity>
    
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
        
        viewModel.soundList.observe(this) {
            binding.imgEmpty.isGone = it.isNotEmpty()
            listSounds = it
            soundAdapter.submitList(it)
            Timber.tag("CalculateNoise").d("${it.map { it.title }}")
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
                        viewModel.delete(it)
                    }
                    .show()
            }
        }
    }
    
    private fun exportDatabaseToCSVFile() {
        val csvFile = FileUtils.generateFile(this, csvFileName)
        if (csvFile != null) {
            
            FileUtils.exportMoviesWithDirectorsToCSVFile(csvFile, listSounds)
            
            Toast.makeText(this, "CSV Successfully Generated", Toast.LENGTH_LONG).show()
            val intent = FileUtils.goToFileIntent(this, csvFile)
            startActivity(intent)
        } else {
            Toast.makeText(this, "CSV Failed to Generated", Toast.LENGTH_LONG).show()
        }
    }
    
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ListDataActivity::class.java)
            context.startActivity(intent)
        }
    }
}