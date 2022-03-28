package com.soundmeter.application.utils

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

object DateUtils {
    
    private fun convertInstant(date: String): String {
        
        return try {
            Instant
                .parse(date)
                .atZone(ZoneId.of("Asia/Jakarta"))
                .format(DateTimeFormatter.ofPattern(ddMMMMYYYYHHmm))!!
            
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
    
    fun getDateFromMillis(millis: Long): String{
        val date = Instant.ofEpochMilli(millis).toString()
        return convertInstant(date)
    }
    
    const val ddMMMMYYYYHHmm = "dd MMMM yyyy HH:mm"
}