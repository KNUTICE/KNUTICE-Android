package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.TopicSubscriptionRemoteRepository
import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.TopicType
import com.doyoonkim.model.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface FetchTopicSubscriptionStatus {
    operator fun invoke(topicType: TopicType): Flow<Result<List<Boolean>>>
}

class FetchTopicSubscriptionStatusImpl @Inject constructor(
    private val remoteRepository: TopicSubscriptionRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchTopicSubscriptionStatus {

    override operator fun invoke(topicType: TopicType) =
        remoteRepository.queryTopicSubscriptionStatus(topicType).transform { nullable ->
            nullable?.let {
                // TODO: Revise Later
                val result = listOf(
                    it.subscribed.contains(NoticeCategory.GENERAL_NEWS.name),
                    it.subscribed.contains(NoticeCategory.ACADEMIC_NEWS.name),
                    it.subscribed.contains(NoticeCategory.SCHOLARSHIP_NEWS.name),
                    it.subscribed.contains(NoticeCategory.EVENT_NEWS.name),
                    it.subscribed.contains(NoticeCategory.EMPLOYMENT_NEWS.name)
                )
                emit(Result.success(result))

            } ?: emit(Result.failure(NoSuchElementException("Subscription Status Not Found")))
        }.catch {
            // Internal Error
            emit(Result.failure(it))
        }.flowOn(ioDispatcher)
}