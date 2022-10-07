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
import com.soundmeter.application.utils.*
import com.soundmeter.application.view.list.ListDataActivity
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.Instant
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
    private var speed = FAST
    private var type = TYPE_A

    private lateinit var timer: Timer

    private var mDbCount = 40f
    private var minDB = 100f
    private var maxDB = 0f
    private var lastDbCount = mDbCount
    private val minChange = 0.5f
    private var value = 0f

    private val df1 = DecimalFormat("####.0")

    private var listSample = ArrayList<Pair<String, String>>()

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
                listSample.clear()
                requestMicPermission()
            }

            btnStop.setOnClickListener {
                setIsRecording(false)
                btnStop.isEnabled = false
                recorder.stop()
                timer.stop()

                BottomSheetSaveData.Builder(supportFragmentManager)
                    .setButton {
                        viewModel.insertData(
                            it.first, it.second,
                            DateUtils.getDateFromMillis(System.currentTimeMillis()),
                            tvMin.text.toString(), tvMax.text.toString(), listSample
                        )
                    }
                    .show()
            }

            btnList.setOnClickListener {
                ListDataActivity.start(this@RecordingActivity)
            }

            swSpeed.setOnCheckedChangeListener { _, isChecked ->
                speed = if (isChecked) SLOW else FAST
            }

            swType.setOnCheckedChangeListener { _, isChecked ->
                type = if (isChecked) TYPE_C else TYPE_A
            }
        }
    }

    private fun setIsRecording(record: Boolean) {
        with(binding) {
            btnStart.isEnabled = !record
            swType.isClickable = !record
            swSpeed.isClickable = !record
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
                                timer.start(speed, type)
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

        binding.tvMin.text = "0"
        binding.tvMax.text = "0"
        binding.tvCurrent.text = "0"

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

    private fun getVolume(type: String) {

        val volume = recorder.maxAmplitude
        if (type == TYPE_A) {
            if (volume in 31..10009) {
                countDb(volume, type)
            }
        } else {
            if (volume in 315..3162280) {

                countDb(volume, type)
            }
        }
    }

    private fun countDb(volume: Int, type: String) {
        val db = 20 * (log10(volume.toDouble())).toFloat()

        value = if (db > lastDbCount) {
            if (db - lastDbCount > minChange) db - lastDbCount else minChange
        } else {
            if (db - lastDbCount < -minChange) db - lastDbCount else -minChange
        }
        mDbCount = lastDbCount + value * 0.2f
        lastDbCount = mDbCount

        when (type) {

            TYPE_A -> {
                if (mDbCount in 30f..80f) {
                    if (mDbCount < minDB) minDB = mDbCount
                    if (mDbCount > maxDB) maxDB = mDbCount
                }
            }
            TYPE_C -> {
                if (mDbCount in 50f..130f) {
                    if (minDB < 50) minDB = mDbCount
                    if (mDbCount < minDB) minDB = mDbCount
                    if (mDbCount > maxDB) maxDB = mDbCount
                }
            }
        }

        with(binding) {
            tvMax.text = df1.format(maxDB)
            tvMin.text = df1.format(minDB)
            tvCurrent.text = df1.format(mDbCount)
        }
    }

    override fun onTimerTick(duration: String, seconds: Long, millis: Long, speed: String, type: String) {
        binding.tvTimer.text = duration

        when (speed) {

            SLOW -> if (seconds.toInt() % 1 == 0 && millis == 0L) getVolume(type)

            FAST -> getVolume(type)
        }

        if (seconds.toInt() % 5 == 0 && millis == 0L)
            listSample.add(Pair(duration, df1.format(mDbCount)))

        binding.btnStop.isEnabled = listSample.size > 0
    }
}