package com.soundmeter.application.view.recording

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.soundmeter.application.databinding.ActivityRecordingBinding
import com.soundmeter.application.utils.DateUtils
import com.soundmeter.application.utils.Timer
import com.soundmeter.application.view.list.ListDataActivity
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.Instant
import timber.log.Timber
import java.io.IOException
import java.text.DecimalFormat
import kotlin.math.log10

@AndroidEntryPoint
class RecordingActivity : AppCompatActivity(), Timer.OnTimerTickListener {
    
    private lateinit var binding: ActivityRecordingBinding
    
    private val viewModel: RecordingViewModel by viewModels()
    
    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var fileName = ""
    
    private lateinit var timer: Timer
    
    private var mDbCount = 40f
    private var minDB = 100f
    private var maxDB = 0f
    private var lastDbCount = mDbCount
    private val min = 0.5f
    private var value = 0f
    
    private var listSample = ArrayList<Pair<String, String>>()
    private val df1 = DecimalFormat("####.0")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    
        initListener()
    
        timer = Timer(this)
    }
    
    private fun initListener() {
        
        with(binding) {
            
            btnStart.setOnClickListener {
                requestMicPermission()
            }
            
            btnStop.setOnClickListener {
                setIsRecording(false)
                recorder.stop()
                timer.stop()
                
                BottomSheetSaveData.Builder(supportFragmentManager)
                    .setButton {
                        viewModel.insertData(
                            it.first, it.second,
                            DateUtils.getDateFromMillis(System.currentTimeMillis()),
                            tvMax.text.toString(), tvMin.text.toString(), listSample
                        )
                    }
                    .show()
            }
            
            btnList.setOnClickListener {
                ListDataActivity.start(this@RecordingActivity)
            }
        }
    }
    
    private fun setIsRecording(record: Boolean) {
        with(binding) {
            btnStart.isEnabled = !record
            btnStop.isEnabled = record
        }
    }
    
    private fun requestMicPermission() {
        
        val listPermission =
            listOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        
        Dexter.withContext(this).withPermissions(listPermission)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    
                    report?.let {
                        when {
                            report.areAllPermissionsGranted() -> {
                                startRecording()
                                setIsRecording(true)
                                timer.start()
                            }
                            report.isAnyPermissionPermanentlyDenied -> {
                                try {
                                    val intent =
                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts(
                                        "package",
                                        this@RecordingActivity.packageName,
                                        null
                                    )
                                    intent.data = uri
                                    ContextCompat.startActivity(this@RecordingActivity, intent, null)
                                } catch (e: ActivityNotFoundException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
                
                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            }.check()
    }
    
    private fun startRecording() {
        
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else {
            MediaRecorder()
        }
        
        dirPath = "${externalCacheDir?.absolutePath}/"
        val date = getDateFromMillis(System.currentTimeMillis()).substringAfterLast(":")
        fileName = "audio_record_$date"
        
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$fileName.mp3")
            
            try {
                prepare()
                
            } catch (e: IOException) {
                Toast.makeText(this@RecordingActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
            
            start()
        }
    }
    
    private fun getDateFromMillis(millis: Long): String {
        return Instant.ofEpochMilli(millis).toString()
    }
    
    private fun countDb() {
    
        val volume = recorder.maxAmplitude
        Timber.d(volume.toString())
        if (volume in 1..999999) {
            val db = 20 * (log10(volume.toDouble())).toFloat()
            Timber.d(db.toString())
    
            value = if (db > lastDbCount) {
                if (db - lastDbCount > min) db - lastDbCount else min
            } else {
                if (db - lastDbCount < -min) db - lastDbCount else -min
            }
            mDbCount = lastDbCount + value * 0.2f
            lastDbCount = mDbCount
            if (mDbCount < minDB) minDB = mDbCount
            if (mDbCount > maxDB) maxDB = mDbCount
    
            with(binding) {
                tvMax.text = df1.format(maxDB)
                tvMin.text = df1.format(minDB)
                tvCurrent.text = df1.format(mDbCount)
            }
        }
    }
    
    override fun onTimerTick(duration: String, seconds: Long, millis: Long) {
        binding.tvTimer.text = duration
        countDb()
        
        if (seconds.toInt() % 5 == 0 && millis == 0L)
            listSample.add(Pair(duration, df1.format(mDbCount)))
    }
}