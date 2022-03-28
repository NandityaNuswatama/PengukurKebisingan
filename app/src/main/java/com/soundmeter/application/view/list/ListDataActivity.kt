package com.soundmeter.application.view.list

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.soundmeter.application.R
import com.soundmeter.application.data.SoundEntity
import com.soundmeter.application.databinding.ActivityListDataBinding
import com.soundmeter.application.utils.BottomSheetWarning
import com.soundmeter.application.utils.FileUtils
import com.soundmeter.application.utils.csvFileName
import com.soundmeter.application.view.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint

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
            addItemDecoration(
                object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        outRect.left = 8
                        outRect.right = 8
                        outRect.top = 16
                        outRect.bottom = if (position == state.itemCount - 1) 16 else 0
                    }
                }
            )
        }
    }
    
    private fun initObserve() {
        
        viewModel.soundList.observe(this) {
            binding.imgEmpty.isGone = it.isNotEmpty()
            listSounds = it
            soundAdapter.submitList(it)
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
                    .setPositive { viewModel.delete(it) }
                    .show()
                
                viewModel.getListData()
            }
        }
    }
    
    private fun exportDatabaseToCSVFile() {
        val csvFile = FileUtils.generateFile(this, csvFileName)
        if (csvFile != null) {
            
            FileUtils.exportMoviesWithDirectorsToCSVFile(csvFile, listSounds)
            
            Toast.makeText(this, "Generated", Toast.LENGTH_LONG).show()
            val intent = FileUtils.goToFileIntent(this, csvFile)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Not Generated", Toast.LENGTH_LONG).show()
        }
    }
    
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ListDataActivity::class.java)
            context.startActivity(intent)
        }
    }
}