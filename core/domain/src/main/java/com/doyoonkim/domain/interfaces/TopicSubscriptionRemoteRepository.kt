package com.doyoonkim.domain.interfaces

import com.doyoonkim.model.TopicSubscriptionStatusVO
import com.doyoonkim.model.TopicType
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import kotlinx.coroutines.flow.Flow

interface TopicSubscriptionRemoteRepository {
    fun queryTopicSubscriptionStatus(topicType: TopicType): Flow<TopicSubscriptionStatusVO?>

    fun requestTopicSubscriptionPreferencesSubmission(
        body: TopicSubscriptionPreferencesBody
    ): Flow<Boolean>
}
