package com.doyoonkim.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.doyoonkim.data.repository.local.preferences.AppSubscriptionPreferenceRepositoryImpl
import com.doyoonkim.model.preferencemodel.Major
import com.doyoonkim.model.preferencemodel.SubscribedMajors
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class AppSubscriptionPreferenceRepositoryImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val majorSubscriptionPreferenceStore: DataStore<SubscribedMajors> = mockk()

    // Create a temporary DataStore for testing
    private val testDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = { File.createTempFile("test_prefs", ".preferences_pb") }
    )
    private lateinit var repository: AppSubscriptionPreferenceRepositoryImpl

    @Before
    fun setUp() {
        repository = AppSubscriptionPreferenceRepositoryImpl(
            preferenceDataStore = testDataStore,
            majorSubscriptionPreferenceStore = majorSubscriptionPreferenceStore
        )
    }

    @Test
    fun getSubscribedMajor_returnsEmptySetByDefault() = runTest(testDispatcher) {
        val result = repository.getSubscribedMajor().first()
        assertEquals(emptySet<String>(), result)
    }

    @Test
    fun updateSubscribedMajor_updatesMajorsSubscribedCorrectly() = runTest(testDispatcher) {
        repository.updateSubscribedMajor("Computer Science")

        val result = repository.getSubscribedMajor().first()
        assertEquals(setOf("Computer Science"), result)
    }

    @Test
    fun getSubscribedMajor_fallsBackToLegacyPreferenceWhenMajorsSubscribedAbsent() = runTest(testDispatcher) {
        // Inject legacy preference manually into DataStore
        val legacyKey = stringPreferencesKey("SUBSCRIBED_MAJOR")
        testDataStore.edit { it[legacyKey] = "Electrical Engineering" }

        val result = repository.getSubscribedMajor().first()
        assertEquals(setOf("Electrical Engineering"), result)
    }

    @Test
    fun getSubscriptionPending_returnsEmptySetByDefault() = runTest(testDispatcher) {
        val result = repository.getSubscriptionPending().first()
        assertEquals(emptySet<String>(), result)
    }

    @Test
    fun addPendingTarget_addsTargetMajorToPendingSet() = runTest(testDispatcher) {
        repository.addPendingTarget("Math")
        repository.addPendingTarget("Physics")

        val result = repository.getSubscriptionPending().first()
        assertEquals(setOf("Math", "Physics"), result)
    }

    @Test
    fun removePendingTarget_removesCompletedItemsProvided() = runTest(testDispatcher) {
        repository.addPendingTarget("Math")
        repository.addPendingTarget("Physics")
        repository.addPendingTarget("Chemistry")

        repository.removePendingTarget(setOf("Math", "Physics"))

        val result = repository.getSubscriptionPending().first()
        assertEquals(setOf("Chemistry"), result)
    }

    @Test
    fun getUnSubscribedPending_returnsEmptySetByDefault() = runTest(testDispatcher) {
        val result = repository.getUnsubscribePending().first()
        assertEquals(emptySet<String>(), result)
    }

    @Test
    fun updateUnsubscribeTarget_addsTargetItemToUnsubscribePendingSet() = runTest(testDispatcher) {
        repository.updateUnsubscribeTarget("History")

        val result = repository.getUnsubscribePending().first()
        assertEquals(setOf("History"), result)
    }

    @Test
    fun removeUnsubscribedTarget_removesCompletedItemsProvided() = runTest(testDispatcher) {
        repository.updateUnsubscribeTarget("History")
        repository.updateUnsubscribeTarget("Literature")

        repository.removeUnsubscribedTarget(setOf("History"))

        val result = repository.getUnsubscribePending().first()
        assertEquals(setOf("Literature"), result)
    }

    @Test
    fun getSubscribedMajors_carriesMajorSubscriptionPreferenceStore() = runTest(testDispatcher) {
        val mockSubscribedMajors = SubscribedMajors(
            updated = 100L,
            subscriptions = listOf(Major(id = 0, legacyId = "GENERAL_NEWS", name = "General News", college = null))
        )
        every { majorSubscriptionPreferenceStore.data } returns flowOf(mockSubscribedMajors)

        val result = repository.getSubscribedMajors().first()
        assertEquals(mockSubscribedMajors, result)
    }

    @Test
    fun addPendingSubscribeTarget_addsTargetToPendingByMajorId() = runTest(testDispatcher) {
        val major = Major(id = 101, legacyId = null, name = "COMPUTER_SCIENCE", college = null)

        repository.addPendingSubscribeTarget(major)

        val result = repository.getSubscriptionPending().first()
        assertEquals(setOf("101"), result)
    }

    @Test
    fun removePendingSubscribeTarget_removesTargetsFromPendingByMajorId() = runTest(testDispatcher) {
        repository.addPendingTarget("101")
        repository.addPendingTarget("102")

        val subscribeCompleted = setOf(Major(id = 101, legacyId = null, name = "COMPUTER_SCIENCE", college = null))
        repository.removePendingSubscribeTarget(subscribeCompleted)

        val result = repository.getSubscriptionPending().first()
        assertEquals(setOf("102"), result)
    }

    @Test
    fun addPendingUnsubscribeTarget_addsTargetToUnsubscribePendingByMajorId() = runTest(testDispatcher) {
        val major = Major(id = 101, legacyId = null, name = "COMPUTER_SCIENCE", college = null)

        repository.addPendingUnsubscribeTarget(major)

        val result = repository.getUnsubscribePending().first()
        assertEquals(setOf("101"), result)
    }

    @Test
    fun removePendingUnsubscribeTarget_removesTargetsFromPendingByMajorId() = runTest(testDispatcher) {
        repository.updateUnsubscribeTarget("101")
        repository.updateUnsubscribeTarget("102")

        val unsubscribeCompleted = setOf(Major(id = 101, legacyId = null, name = "COMPUTER_SCIENCE", college = null))
        repository.removePendingUnsubscribeTarget(unsubscribeCompleted)
        val result = repository.getUnsubscribePending().first()
        assertEquals(setOf("102"), result)
    }
}
