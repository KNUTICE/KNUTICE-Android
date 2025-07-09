package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.TopicSubscriptionRemoteRepository
import com.doyoonkim.model.TopicSubscriptionPreferencesVO
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchTopicSubscriptionStatus {
    operator fun invoke(): Flow<TopicSubscriptionPreferencesVO>
}

class FetchTopicSubscriptionStatusImpl @Inject constructor(
    private val remoteRepository: TopicSubscriptionRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchTopicSubscriptionStatus {

    override operator fun invoke() =
        remoteRepository.queryTopicSubscriptionStatus().transform { nullable ->
            nullable?.let { emit(it) }
        }.catch {
            /* Internal Error. Consume values, and never emit values. */
        }.flowOn(ioDispatcher)

}