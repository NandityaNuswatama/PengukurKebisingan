package com.soundmeter.application.utils

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

fun Context.requestMicPermission(onGranted: () -> Unit) {

    val listPermission =
        listOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    Dexter.withContext(this).withPermissions(listPermission)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                report?.let {
                    when {
                        report.areAllPermissionsGranted() -> {
                            onGranted.invoke()
                        }
                        report.isAnyPermissionPermanentlyDenied -> {
                            goToSetting(this@requestMicPermission)
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

fun Context.requestLocationPermission(onGranted: () -> Unit) {
    Dexter.withContext(this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        .withListener(object : MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.let {
                    when {
                        report.areAllPermissionsGranted() -> {
                            onGranted.invoke()
                        }
                        report.isAnyPermissionPermanentlyDenied -> {
                            goToSetting(this@requestLocationPermission)
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

private fun goToSetting(context: Context) {
    try {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts(
            "package",
            context.packageName,
            null
        )
        intent.data = uri
        ContextCompat.startActivity(context, intent, null)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}