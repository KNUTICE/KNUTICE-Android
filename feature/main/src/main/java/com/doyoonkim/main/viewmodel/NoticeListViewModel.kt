package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.KNBaseViewModel
import com.doyoonkim.domain.interfaces.AppSubscriptionPreferenceRepository
import com.doyoonkim.domain.usecases.FetchNoticesPerPage
import com.doyoonkim.main.contract.NoticeListEvent
import com.doyoonkim.main.contract.NoticeListMutation
import com.doyoonkim.main.contract.NoticeListSideEffect
import com.doyoonkim.main.contract.NoticeListUiState
import com.doyoonkim.main.contract.NoticeListViewModelState
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoticeListViewModel @Inject constructor(
    private val appSubscriptionPreferenceRepository: AppSubscriptionPreferenceRepository,
    private val fetchNoticesPerPage: FetchNoticesPerPage
) : KNBaseViewModel<NoticeListViewModelState, NoticeListUiState, NoticeListEvent, NoticeListSideEffect, NoticeListMutation>() {

    companion object {
        private const val TAG = "NoticeListViewModel"
    }

    override fun setInitialViewModelState(): NoticeListViewModelState {
        val keys = NoticeCategory.entries.dropLast(1).map(NoticeCategory::name)
        val noticesMap = hashMapOf<String, List<NoticeVO>>()
        val fetchableMap = hashMapOf<String, Boolean>()

        keys.forEach { category ->
            // NoticesMap
            // Provide empty NoticeVO as a default value to trigger Skeleton loading Animation
            noticesMap[category] = List<NoticeVO>(20) { NoticeVO() }
            fetchableMap[category] = true
        }

        return NoticeListViewModelState(
            isLoading = true,
            categories = keys,
            notices = noticesMap,
            isFetchable = fetchableMap
        )
    }

    override fun handleEvent(event: NoticeListEvent) {
        when (event) {
            is NoticeListEvent.FetchCoreCategories -> {
                val coreCategories = NoticeCategory.entries.map { it.name }
                coreCategories.forEach { category ->
                    fetchNotices(category)
                }
            }

            is NoticeListEvent.FetchNotices -> {
                fetchNotices(event.category)
            }

            is NoticeListEvent.RequestRefresh -> {
                mutate(NoticeListMutation.Refreshing)
                val currentAvailableCategories = viewModelState.value.notices.keys
                currentAvailableCategories.forEach { category ->
                    fetchNotices(category)
                }
            }

            is NoticeListEvent.UpdateMajorSubscription -> {
                mutate(NoticeListMutation.MajorUpdated(event.status))
                // Initiate Fetch for updated Majors only.
                event.status.forEach { category ->
                    fetchNotices(category)
                }
            }

            is NoticeListEvent.RequestNavToSettings -> sendSideEffect(NoticeListSideEffect.NavToSettings)
            is NoticeListEvent.RequestGoBack -> sendSideEffect(NoticeListSideEffect.GoBack)
        }
    }

    init {
        // Fetch Notices of Core Categories First.
        // Underlying problem: What if user access this UI via Deeplink that directly links to specific major notices?
        sendUiEvent(NoticeListEvent.FetchCoreCategories)

        // Receive values from DataStore.
        collectSubscriptionPreferenceFlow()
    }

    private fun collectSubscriptionPreferenceFlow() = viewModelScope.launch {
        // TODO: Collect exposed FLow<String> (Potentially, Flow<TopicType>) to initialize UiState.

        // TEST ONLY
        val received: Set<String> = setOf("COMPUTER_SCIENCE")
        // Tigger Event (UpdateMajorSubscription(Set<String>)
        sendUiEvent(NoticeListEvent.UpdateMajorSubscription(received))
    }

    // Later parameter type would be changed to Int
    private fun fetchNotices(category: String) =
        viewModelScope.launch {
            // Check Current category is fetchable.
            val isFetchable = viewModelState.value.isFetchable[category]
            if (isFetchable == null) {
                mutate(
                    NoticeListMutation.Notices.Failure(
                        category,
                        "State is not yet initialized correctly."
                    )
                )
                return@launch
            }

            if (!isFetchable) {
                TODO("Issue SideEffect to show Toast/SnackBar message.")
                return@launch
            }

            // Mutate State to isLoading
            mutate(NoticeListMutation.Loading)
            // Current Notices Map Snapshot
            val snapshot = viewModelState.value.notices[category]
            if (snapshot == null) {
                mutate(
                    NoticeListMutation.Notices.Failure(
                        category,
                        "State is not yet initialized correctly"
                    )
                )
                return@launch
            }

            // LastNttId for pagination.
            val currentLastNttId = snapshot.lastOrNull()?.nttId ?: 0

            fetchNoticesPerPage(category, currentLastNttId)
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { vo ->
                            // Update State using Key as Category, received list as a values.
                            mutate(NoticeListMutation.Notices.Success(category, vo))
                        },
                        onFailure = {
                            Log.d(TAG, "Unable to fetch notices: ${it.stackTraceToString()}")
                        }
                    )
                }
        }

    // Reducer
    override fun reduce(
        currentState: NoticeListViewModelState,
        mutation: NoticeListMutation
    ): NoticeListViewModelState {
        return when (mutation) {
            is NoticeListMutation.Loading -> {
                currentState.copy(
                    isLoading = true
                )
            }

            is NoticeListMutation.Refreshing -> {
                val availableCategories = currentState.notices.keys

                val newNoticesMap = hashMapOf<String, List<NoticeVO>>().apply {
                    availableCategories.forEach { key ->
                        this[key] = List(20) { NoticeVO() }
                    }
                }

                val newIsFetchableMap = hashMapOf<String, Boolean>().apply {
                    availableCategories.forEach { key ->
                        this[key] = true
                    }
                }

                currentState.copy(
                    isLoading = false,
                    isRefreshing = true,
                    notices = newNoticesMap,
                    isFetchable = newIsFetchableMap
                )
            }

            is NoticeListMutation.MajorUpdated -> {
                val coreCategories = NoticeCategory.entries.map { it.name }

                val updatedNoticeMap = hashMapOf<String, List<NoticeVO>>().apply {
                    // Initialize Core Categories first.
                    coreCategories.forEach { category ->
                        this[category] = currentState.notices[category] ?: List(20) { NoticeVO() }
                    }
                    // Initialize Updated Major Categories
                    mutation.categories.forEach { category ->
                        this[category] = currentState.notices[category] ?: List(20) { NoticeVO() }
                    }
                }
                val updatedFetchableMap = hashMapOf<String, Boolean>().apply {
                    coreCategories.forEach { category ->
                        this[category] = currentState.isFetchable[category] ?: true
                    }
                    mutation.categories.forEach { category ->
                        this[category] = currentState.isFetchable[category] ?: true
                    }
                }

                currentState.copy(
                    notices = updatedNoticeMap,
                    isFetchable = updatedFetchableMap
                )
            }

            is NoticeListMutation.Notices -> mutation.reducer(currentState)
        }
    }

    // Specialized Reducer
    private fun NoticeListMutation.Notices.reducer(
        currentState: NoticeListViewModelState
    ): NoticeListViewModelState {
        return when (this) {
            is NoticeListMutation.Notices.Success -> {
                // Append Received List to existing values.
                val existing = currentState.notices[category] ?: emptyList()
                val updated = existing.toMutableList().apply { addAll(received) }

                // Update Notice Map
                val updatedNotices = currentState.notices
                updatedNotices[category] = updated

                // Check Fetchable Status
                val updatedFetchableMap = currentState.isFetchable
                updatedFetchableMap[category] = received.size % 20 == 0

                currentState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isError = false,
                    notices = updatedNotices,
                    isFetchable = updatedFetchableMap
                )
            }

            is NoticeListMutation.Notices.Failure -> {
                currentState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isError = true,
                    errorMessages = currentState.errorMessages + this.reason
                )
            }
        }
    }

}