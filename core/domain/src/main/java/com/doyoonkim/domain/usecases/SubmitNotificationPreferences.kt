package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.TopicSubscriptionRemoteRepository
import com.doyoonkim.model.di.IoDispatcher
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

interface SubmitNotificationPreferences {
    operator fun invoke(body: TopicSubscriptionPreferencesBody): Flow<Boolean>
}

class SubmitNotificationPreferencesImpl @Inject constructor(
    private val remoteRepository: TopicSubscriptionRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SubmitNotificationPreferences {

    override operator fun invoke(body: TopicSubscriptionPreferencesBody) =
        remoteRepository.requestTopicSubscriptionPreferencesSubmission(body)
            .catch {
                /* Internal Error. Consume values, and never emit values. */
            }.flowOn(ioDispatcher)
}
