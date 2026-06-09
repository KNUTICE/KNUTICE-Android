package com.doyoonkim.knutice.task

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.doyoonkim.common.worker.IntermediateWorkerFactory
import com.doyoonkim.domain.interfaces.AppSubscriptionPreferenceRepository
import com.doyoonkim.domain.usecases.SubmitNotificationPreferences
import com.doyoonkim.model.TopicType
import com.doyoonkim.model.di.ApplicationContext
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MajorSubscriptionUpdate(
    appContext: Context,
    workerParam: WorkerParameters,
    private val submitNotificationPreferences: SubmitNotificationPreferences,
    private val appSubscriptionPreferenceRepository: AppSubscriptionPreferenceRepository
): CoroutineWorker(appContext, workerParam) {
    override suspend fun doWork(): Result {
        try {
            // Perform Major Topic Subscription Update work.
            // 1. Update necessary value.
            // 2. Check prev registered topic.
            // 3. Necessary deletion work.
            val updateTarget = appSubscriptionPreferenceRepository.getSubscriptionPending().first()
            val unsubscribeTargets = appSubscriptionPreferenceRepository.getUnsubscribePending().first()

            /* TEMPORARY LOG FOR DEBUG */
            Log.d("MajorSubscriptionUpdate", "SUBSCRIPTION PENDING: ${updateTarget.toList()}")
            Log.d("MajorSubscriptionUpdate", "UNSUBSCRIPTION PENDING: ${unsubscribeTargets.toList()}")

            // In case both pending lists are empty, early exit the work with Success result.
            if (updateTarget.isEmpty() && unsubscribeTargets.isEmpty()) return Result.success()

            // New Major to subscribe
            updateTarget.forEach { target ->
                val result = submitNotificationPreferences.invoke(
                    TopicSubscriptionPreferencesBody(
                        topicType = TopicType.MAJOR,
                        noticeName = target,
                        isSubscribed = true
                    )
                ).first()

                /* TEMPORARY LOG FOR DEBUG */
                Log.d("MajorSubscriptionUpdate", "Subscribing target $target processed with result $result")

                // Regardless of retryCount, registration must be completed, in order to
                // promise PUSH notification on this topic.
                if (!result) return Result.retry()
            }
            // Clear out pending list.
            // Clear pending list early. In case, retry occurred in unsubscribed logic, above process will be safely ignored because
            // updateTarget would be empty.
            appSubscriptionPreferenceRepository.removePendingTarget(updateTarget)

            unsubscribeTargets.forEach {
                val unsubscribePrevTopic = submitNotificationPreferences.invoke(
                    TopicSubscriptionPreferencesBody(
                        topicType = TopicType.MAJOR,
                        noticeName = it,
                        isSubscribed = false
                    )
                ).first()

                /* TEMPORARY LOG FOR DEBUG */
                Log.d("MajorSubscriptionUpdate", "Subscribing target $it processed with result $unsubscribePrevTopic")

                // Regardless of retryCount, unsubscription must be completed, in order to avoid
                // PUSH notification on this topic.
                if (!unsubscribePrevTopic) return Result.retry()
            }

            // All unsubscription is completed.
            appSubscriptionPreferenceRepository.removeUnsubscribedTarget(unsubscribeTargets)
            return Result.success()
        } catch (e: Exception) {
            Log.d("MajorSubscriptionUpdate", "Unable to process Server-side Synchronization\nReason:${e.stackTraceToString()}")
            return Result.retry()
        }
    }

    class Factory @Inject constructor(
        @ApplicationContext private val appContext: Context,
        private val submitNotificationPreferences: SubmitNotificationPreferences,
        private val appSubscriptionPreferenceRepository: AppSubscriptionPreferenceRepository
    ): IntermediateWorkerFactory {
        override fun create(params: WorkerParameters): ListenableWorker {
            return MajorSubscriptionUpdate(
                appContext,
                params,
                submitNotificationPreferences,
                appSubscriptionPreferenceRepository
            )
        }

    }
}