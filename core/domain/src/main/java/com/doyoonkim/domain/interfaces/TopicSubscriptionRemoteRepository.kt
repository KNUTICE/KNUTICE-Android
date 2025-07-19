package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.TopicSubscriptionPreferencesVO
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import kotlinx.coroutines.flow.Flow

interface TopicSubscriptionRemoteRepository {
    fun queryTopicSubscriptionStatus(): Flow<TopicSubscriptionPreferencesVO?>

    fun requestTopicSubscriptionPreferencesSubmission(
        body: TopicSubscriptionPreferencesBody
    ): Flow<Boolean>
}