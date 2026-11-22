package com.doyoonkim.data

import androidx.datastore.core.CorruptionException
import com.doyoonkim.data.repository.local.preferences.MajorSubscriptionPreferenceSerializer
import com.doyoonkim.model.preferencemodel.Major
import com.doyoonkim.model.preferencemodel.SubscribedMajors
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class MajorSubscriptionPreferenceSerializerTest {

    private val serializer = MajorSubscriptionPreferenceSerializer()

    private val base = SubscribedMajors(
        updated = 100L,
        subscriptions = listOf(
            Major(id = 101, legacyId = null, name = "Computer Science", college = "College of Science"),
            Major(id = 0, legacyId = "GENERAL_NEWS", name = "General News", college = null)
        )
    )

    @Test
    fun serializer_returnsInitialDefaultValue() {
        assertTrue(serializer.defaultValue.subscriptions.isEmpty())
    }

    @Test
    fun serializer_serializeAndDeserializeSuccessfully() = runTest {
        val outputStream = ByteArrayOutputStream()
        serializer.writeTo(base, outputStream)

        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        val actual = serializer.readFrom(inputStream)

        assertEquals(base, actual)
    }

    @Test
    fun serializer_returnsDefaultsOrThrows_onFailure() = runTest {
        val emptyStream = ByteArrayInputStream(byteArrayOf())

        try {
            serializer.readFrom(emptyStream)
            fail("Expected Exception is not thrown")
        } catch (e: Exception) {
            assertTrue(e is CorruptionException)
        }
    }
}
