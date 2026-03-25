package com.doyoonkim.main.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.doyoonkim.common.base.BaseViewModel
import com.doyoonkim.domain.interfaces.AppPreferenceRepository
import com.doyoonkim.domain.interfaces.AsyncNoticeWidgetTaskScheduler
import com.doyoonkim.main.contract.WidgetConfigEvent
import com.doyoonkim.main.contract.WidgetConfigMutation
import com.doyoonkim.main.contract.WidgetConfigSideEffect
import com.doyoonkim.main.contract.WidgetConfigState
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author kimdoyoon
 * Created 2/16/26 at 8:38 PM
 */
class WidgetConfigViewModel @Inject constructor(
    private val appPreference: AppPreferenceRepository,
    private val noticeWidgetTaskScheduler: AsyncNoticeWidgetTaskScheduler,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<WidgetConfigState, WidgetConfigEvent, WidgetConfigSideEffect, WidgetConfigMutation>() {

    private val TAG = "WidgetConfigViewModel"

    override fun setInitialState(): WidgetConfigState = WidgetConfigState()

    override fun handleEvent(event: WidgetConfigEvent) {
        when(event) {
            is WidgetConfigEvent.FetchStatus -> { fetchWidgetCategoryStatus() }
            is WidgetConfigEvent.SelectPolicy -> {
                mutate(WidgetConfigMutation.Configuration.Selected(event.policy))
            }
            is WidgetConfigEvent.SaveSelection -> {
                updateWidgetConfiguration()
            }
            is WidgetConfigEvent.Exit -> sendSideEffect(WidgetConfigSideEffect.CloseSettings)
        }
    }

    private fun fetchWidgetCategoryStatus() =
        viewModelScope.launch(ioDispatcher) {
            // Default options (Main Notice Categories)
            val default: List<String> = NoticeCategory.entries.dropLast(1).map { it.name }
            mutate(WidgetConfigMutation.Configuration.Available(
                default, appPreference.getSubscribedMajor()
            ))
            // Check Current Policy Status
            val currentPolicy = appPreference.getWidgetCategoryPolicy()
            mutate(WidgetConfigMutation.Configuration.Selected(currentPolicy))
                .also { Log.d(TAG, "Selected CategoryPolicy Set") }
        }

    private fun updateWidgetConfiguration() {
        mutate(WidgetConfigMutation.Configuration.Processing)
        viewModelScope.launch {
            // Update WidgetCategoryPolicy with Current Category Policy selection
            appPreference.updateWidgetCategoryPolicy(uiState.value.selectedCategoryPolicy)
            // Execute worker to update widget.
            noticeWidgetTaskScheduler.executeImmediateTask()
            mutate(WidgetConfigMutation.Configuration.Processed)
            sendSideEffect(WidgetConfigSideEffect.ShowProcessedSnackBark)
        }
    }

    override fun reduce(
        currentState: WidgetConfigState,
        mutation: WidgetConfigMutation
    ): WidgetConfigState {
        return when (mutation) {
            is WidgetConfigMutation.Configuration -> mutation.reducer(currentState)
        }
    }

    private fun WidgetConfigMutation.Configuration.reducer(
        currentState: WidgetConfigState
    ): WidgetConfigState {
        return when(this) {
            is WidgetConfigMutation.Configuration.Available -> {
                currentState.copy(
                    defaultCategories = categories,
                    majorSubscribed = majorSubscribed
                )
            }
            is WidgetConfigMutation.Configuration.Selected -> {
                currentState.copy(
                    selectedCategoryPolicy = policy
                )
            }
            is WidgetConfigMutation.Configuration.Processing -> {
                currentState.copy(isProcessing = true)
            }
            is WidgetConfigMutation.Configuration.Processed -> {
                currentState.copy(isProcessing = false)
            }
        }
    }
}