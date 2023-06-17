package com.soundmeter.application.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.soundmeter.application.di.RemoteDataSource
import java.util.Date
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
): RemoteDataSource {

    override fun uploadData() {
        val soundHash = hashMapOf(
            "area" to "",
            "averageDb" to 0.0,
            "date" to Timestamp(Date()),
            "description" to "",
            "latlng" to GeoPoint(0.0, 0.0),
            "maxDb" to 0.0,
            "minDb" to 0.0,
            "noiseDbValue" to 0.0,
            "recordings" to mapOf("00:00:05" to 0.0, "00:00:10" to 0.0, "00:00:15" to 0.0)
        )
        val db = fireStore.collection("data").add(soundHash)
            .addOnSuccessListener {}
            .addOnFailureListener {  }
    }
}