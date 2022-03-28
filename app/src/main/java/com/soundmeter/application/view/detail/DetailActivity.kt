package com.soundmeter.application.view.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.soundmeter.application.R
import com.soundmeter.application.databinding.ActivityDetailBinding
import com.soundmeter.application.domain.TimeStampDb
import com.soundmeter.application.utils.BottomSheetWarning
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDetailBinding
    private val detailAdapter = DetailAdapter()
    private val viewModel: DetailViewModel by viewModels()
    
    private val dataId by lazy {
        intent.getIntExtra(DATA_ID, 0)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initView()
        initObserver()
        initListener()
    }
    
    private fun initView(){
    
        viewModel.getDetailData(dataId)
        
        with(binding){
            rvDetail.adapter = detailAdapter
            
            rvDetail.addItemDecoration(
                DividerItemDecoration(
                    this@DetailActivity,
                    LinearLayoutManager.VERTICAL
                ))
        }
    }
    
    private fun initObserver(){
        
        viewModel.detailData.observe(this){
            
            with(binding){
                
                toolbar.title = it.title
                tvSubtitle.text = it.subtitle
                tvDate.text = it.date
                tvMax.text = getString(R.string.max_db, it.maxDb)
                tvMin.text = getString(R.string.min_db, it.minDb)
                
                val timeStampDb = mutableListOf<TimeStampDb>()
                val pairList = it.listTime.zip(it.listDb)
                pairList.forEach { (time, db) ->
                    timeStampDb.add(TimeStampDb(time, db))
                }

                detailAdapter.submitList(timeStampDb)
            }
        }
    }
    
    private fun initListener(){
        
        with(binding){
            
            toolbar.setNavigationOnClickListener {
                finish()
            }
            
            toolbar.menu.findItem(R.id.delete).setOnMenuItemClickListener {
                
                BottomSheetWarning.Builder(supportFragmentManager)
                    .setTitle(getString(R.string.confirm_delete_title))
                    .setMessage(getString(R.string.confirm_delete_desc))
                    .setPositive { viewModel.deleteData(dataId) }
                    .show()
                
                finish()
                true
            }
        }
    }
    
    companion object {
        
        const val DATA_ID = "dataId"
        
        fun start(context: Context, id: Int) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(DATA_ID, id)
            context.startActivity(intent)
        }
    }
}