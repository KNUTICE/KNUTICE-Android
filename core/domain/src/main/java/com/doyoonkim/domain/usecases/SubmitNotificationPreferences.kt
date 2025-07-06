package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.TopicSubscriptionRemoteRepository
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

interface SubmitNotificationPreferences {
    operator fun invoke(body: TopicSubscriptionPreferencesBody): Flow<Boolean>
}

class SubmitNotificationPreferencesImpl @Inject constructor(
    private val remoteRepository: TopicSubscriptionRemoteRepository
) : SubmitNotificationPreferences {

    override operator fun invoke(body: TopicSubscriptionPreferencesBody) =
        remoteRepository.requestTopicSubscriptionPreferencesSubmission(body)
            .catch {
                /* Internal Error. Consume values, and never emit values. */
            }
}