package com.soundmeter.application.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.soundmeter.application.data.local.SoundEntity
import com.soundmeter.application.domain.RemoteDataSource
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSourceImpl @Inject constructor() : RemoteDataSource {

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun uploadData(soundEntity: SoundEntity, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val recordings = mutableMapOf<String, Double>()
        soundEntity.listDb.reversed().forEachIndexed { index, db ->
            recordings[soundEntity.listTime[index]] = db.toDouble()
        }
        val soundHash = hashMapOf(
            "area" to soundEntity.title,
            "averageDb" to soundEntity.averageDb.toDouble(),
            "date" to Timestamp(DateTimeUtils.toDate(Instant.ofEpochMilli(soundEntity.timestamp))),
            "description" to soundEntity.subtitle,
            "latlng" to GeoPoint(soundEntity.latitude.toDouble(), soundEntity.longitude.toDouble()),
            "maxDb" to soundEntity.maxDb.toDouble(),
            "minDb" to soundEntity.minDb.toDouble(),
            "noiseDbValue" to soundEntity.noiseDb.toDouble(),
            "recordings" to recordings
        )
        fireStore.collection("data").add(soundHash)
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener {
                onFailure.invoke(it.message.orEmpty())
            }
    }
}