package com.soundmeter.application.utils

import android.os.Handler
import android.os.Looper

class Timer(listener: OnTimerTickListener) {
    
    interface OnTimerTickListener{
        fun onTimerTick(duration: String, seconds: Long, millis: Long)
    }
    
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    
    private var duration = 0L
    private val delay = 100L
    
    init {
        runnable = Runnable {
            duration += delay
            handler.postDelayed(runnable, delay)
            listener.onTimerTick(format(), (duration / 1000) % 60, duration % 1000)
        }
    }
    
    fun start(){
        handler.postDelayed(runnable, delay)
    }
    
    fun stop(){
        handler.removeCallbacks(runnable)
        duration = 0L
    }
    
    private fun format(): String{
        val seconds = (duration / 1000) % 60
        val millis = duration % 1000
        val minutes = (duration / (1000 * 60)) % 60
        
        return "%02d:%02d.%02d".format(minutes, seconds, millis/10)
    }
}