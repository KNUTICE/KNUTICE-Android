package com.doyoonkim.data.repository.local.preferences

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.doyoonkim.model.preferencemodel.SubscribedMajors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class MajorSubscriptionPreferenceSerializer @Inject constructor() : Serializer<SubscribedMajors> {
    override val defaultValue: SubscribedMajors = SubscribedMajors(
        updated = System.currentTimeMillis(),
        subscriptions = emptyList()
    )

    override suspend fun readFrom(input: InputStream): SubscribedMajors =
        try {
            json.decodeFromString(SubscribedMajors.serializer(), input.readBytes().decodeToString())
        } catch (exception: SerializationException) {
            throw CorruptionException("Unable to deserialize.", exception)
        }

    override suspend fun writeTo(t: SubscribedMajors, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(json.encodeToString(SubscribedMajors.serializer(), t).encodeToByteArray())
        }
    }

    companion object {
        private val json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }
}
