package com.doyoonkim.main.contract

import androidx.compose.runtime.Immutable
import com.doyoonkim.common.base.UiEvent
import com.doyoonkim.common.base.UiMutation
import com.doyoonkim.common.base.UiSideEffect
import com.doyoonkim.common.base.UiState
import com.doyoonkim.common.base.ViewModelState
import com.doyoonkim.model.NoticeVO

@Immutable
data class NoticeListViewModelState(
    val categories: List<String> = emptyList(),
    val notices: Map<String, List<NoticeVO>> = mapOf(),
    val isFetchable: Map<String, Boolean> = mapOf(),
    val isLoading: Map<String, Boolean> = mapOf(),
    val isInitialized: Boolean = false,
    val isRefreshing: Boolean = false,
    val isError: Boolean = false,
    val errorMessages: List<String> = emptyList()
) : ViewModelState<NoticeListUiState> {
    override fun toUiState(): NoticeListUiState {
        return when {
            !this.isInitialized -> NoticeListUiState.Initialize
            this.isRefreshing -> NoticeListUiState.NoticeRefreshing
            !this.isError -> NoticeListUiState.NoticeAvailableOnSuccess(
                categories = categories,
                notices = notices,
                isLoading = isLoading
            )

            else -> NoticeListUiState.NoticeUnavailableOnError(
                errorMessage = errorMessages.last()
            )
        }
    }
}

@Immutable
sealed interface NoticeListUiState : UiState {

    data class NoticeAvailableOnSuccess(
        val categories: List<String>,
        val notices: Map<String, List<NoticeVO>>,
        val isLoading: Map<String, Boolean>
    ) : NoticeListUiState

    data class NoticeUnavailableOnError(
        val errorMessage: String
    ) : NoticeListUiState

    data object Initialize : NoticeListUiState
    data object NoticeRefreshing : NoticeListUiState
}

sealed interface NoticeListEvent : UiEvent {
    data object FetchCoreCategories : NoticeListEvent
    data class FetchNotices(val category: String) : NoticeListEvent
    data object RequestRefresh : NoticeListEvent
    data class UpdateMajorSubscription(val status: Set<String>) : NoticeListEvent
    data object RequestNavToSettings : NoticeListEvent
    data object RequestGoBack : NoticeListEvent
}

sealed interface NoticeListSideEffect : UiSideEffect {
    data class ShowMessage(val message: String) : NoticeListSideEffect
    data object NavToSettings : NoticeListSideEffect
    data object GoBack : NoticeListSideEffect
}

sealed interface NoticeListMutation : UiMutation {
    data class Loading(val category: String) : NoticeListMutation
    data object Refreshing : NoticeListMutation
    data class MajorUpdated(val categories: Set<String>) : NoticeListMutation
    sealed interface Notices : NoticeListMutation {
        data class Success(val category: String, val received: List<NoticeVO>) : Notices
        data class Failure(val category: String, val reason: String) : Notices
    }
}
