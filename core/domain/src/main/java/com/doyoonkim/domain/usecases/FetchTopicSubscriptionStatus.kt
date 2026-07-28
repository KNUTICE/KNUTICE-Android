package com.doyoonkim.domain.usecases

import com.doyoonkim.domain.interfaces.TopicSubscriptionRemoteRepository
import com.doyoonkim.model.MealCategory
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
    // Change Return Type to either Map<String, Boolean> or Pair<String.Boolean>
    operator fun invoke(topicType: TopicType): Flow<Result<List<Pair<String, Boolean>>>>
}

class FetchTopicSubscriptionStatusImpl @Inject constructor(
    private val remoteRepository: TopicSubscriptionRemoteRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FetchTopicSubscriptionStatus {

    override operator fun invoke(topicType: TopicType) =
        remoteRepository.queryTopicSubscriptionStatus(topicType).transform { nullable ->
            nullable?.let {
                when (topicType) {
                    TopicType.NOTICE -> {
                        // TODO: Revise Later (Change Return Type to either Map<String, Boolean> or Pair<String, Boolean>
                        val result = listOf(
                            Pair(NoticeCategory.GENERAL_NEWS.name, it.subscribed.contains(NoticeCategory.GENERAL_NEWS.name)),
                            Pair(NoticeCategory.ACADEMIC_NEWS.name, it.subscribed.contains(NoticeCategory.ACADEMIC_NEWS.name)),
                            Pair(NoticeCategory.SCHOLARSHIP_NEWS.name, it.subscribed.contains(NoticeCategory.SCHOLARSHIP_NEWS.name)),
                            Pair(NoticeCategory.EVENT_NEWS.name, it.subscribed.contains(NoticeCategory.EVENT_NEWS.name)),
                            Pair(NoticeCategory.EMPLOYMENT_NEWS.name, it.subscribed.contains(NoticeCategory.EMPLOYMENT_NEWS.name))
                        )
                        emit(Result.success(result))
                    }
                    TopicType.MAJOR -> {
                        val result = it.subscribed.map { major ->
                            Pair(major, true)
                        }
                        emit(Result.success(result))
                    }
                    TopicType.MEAL -> {
                        val result = mutableListOf<Pair<String, Boolean>>()
                        MealCategory.entries.forEach { category ->
                            result.add(
                                Pair(category.name, it.subscribed.contains(category.name))
                            )
                        }
                        emit(Result.success(result))
                    }
                }
            } ?: emit(Result.failure(NoSuchElementException("Subscription Status Not Found")))
        }.catch {
            // Internal Error
            emit(Result.failure(it))
        }.flowOn(ioDispatcher)
}
