package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.RemoteRepository
import com.doyoonkim.model.TopicSubscriptionPreferencesVO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchTopicSubscriptionStatus {
    operator fun invoke(): Flow<TopicSubscriptionPreferencesVO>
}

class FetchTopicSubscriptionStatusImpl @Inject constructor(
    private val remoteRepository: RemoteRepository
) : FetchTopicSubscriptionStatus {

    override operator fun invoke() =
        remoteRepository.queryTopicSubscriptionStatus().transform { nullable ->
            nullable?.let { emit(it) }
        }

}