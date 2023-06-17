package com.soundmeter.application.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.soundmeter.application.data.local.SoundEntity
import java.io.File

object FileUtils {
    
    fun generateFile(context: Context, fileName: String): File? {
        val csvFile = File(context.filesDir, fileName)
        csvFile.createNewFile()
        
        return if (csvFile.exists()) {
            csvFile
        } else {
            null
        }
    }
    
    fun goToFileIntent(context: Context, file: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val contentUri =
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val mimeType = context.contentResolver.getType(contentUri)
        intent.setDataAndType(contentUri, mimeType)
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        
        return intent
    }
    
    fun exportMoviesWithDirectorsToCSVFile(csvFile: File, listSounds: List<SoundEntity>) {
        csvWriter().open(csvFile, append = false) {
            // Header
            writeRow(
                listOf(
                    "[No]",
                    "[Judul]",
                    "[Keterangan]",
                    "[Waktu]",
                    "[Maksimum dB]",
                    "[Minimum dB]",
                    "[Waktu dB]",
                    "[Nilai dB]",
                )
            )
            listSounds.forEachIndexed { index, sound ->
                writeRow(
                    listOf(index + 1, sound.title, sound.subtitle, sound.date,
                        sound.maxDb, sound.minDb, sound.listTime, sound.listDb
                    )
                )
            }
        }
    }
}