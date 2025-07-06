package com.doyoonkim.domain

import com.doyoonkim.model.NoticeCategory
import com.doyoonkim.model.NoticeVO
import com.doyoonkim.model.TipVO
import com.doyoonkim.model.TopThreeNoticeVO
import com.doyoonkim.model.TopicSubscriptionPreferencesVO
import com.doyoonkim.model.requestBody.DeviceTokenBody
import com.doyoonkim.model.requestBody.TopicSubscriptionPreferencesBody
import com.doyoonkim.model.requestBody.UserReportBody
import kotlinx.coroutines.flow.Flow

// Dependency Inversion
interface RemoteRepository {
    fun queryTopThreeNotices(): Flow<TopThreeNoticeVO?>

    fun queryNoticesPerPage(category: NoticeCategory, lastNttId: Int?): Flow<List<NoticeVO>?>

    fun queryNoticeById(nttId: Int): Flow<NoticeVO?>

    fun queryNoticesByKeyword(keyword: String): Flow<List<NoticeVO>?>

    fun queryTopicSubscriptionStatus(): Flow<TopicSubscriptionPreferencesVO?>

    fun queryAllTips(): Flow<List<TipVO>?>

    fun requestTokenValidation(body: DeviceTokenBody): Flow<Boolean>

    fun requestUpdateValidatedToken(fcmToken: String)

    fun requestUserReportSubmission(body: UserReportBody): Flow<Boolean>

    fun requestTopicSubscriptionPreferencesSubmission(
        body: TopicSubscriptionPreferencesBody
    ): Flow<Boolean>
}