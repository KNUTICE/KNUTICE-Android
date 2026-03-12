package com.doyoonkim.network

import android.util.Log
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.model.TopicType
import com.doyoonkim.model.di.IoDispatcher
import com.doyoonkim.network.model.FcmTokenSaveRequest
import com.doyoonkim.network.model.FcmTokenUpdateRequest
import com.doyoonkim.network.model.ReportSaveRequest
import com.doyoonkim.network.model.TopicUpdateRequest
import com.doyoonkim.network.retrofit.KnuticeService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author kimdoyoon
 * Created 6/3/25 at 12:27 AM
 */

// This class should be provided/injected as Singleton Instance.
class KnuticeRemoteSource @Inject constructor(
    private val knuticeApi: KnuticeService,
    private val appPreference: AppPreferences,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val TAG = "KnuticeRemoteSource"

    suspend fun getNoticesPerPage(
        category: String,
        size: Int = 20,
        lastNttId: Int? = null
    ) = withContext(ioDispatcher) {
        runCatching {
            knuticeApi.getNoticesPerPage(category, size, lastNttId)
        }
    }

    suspend fun getNoticeById(nttId: Int) =
        withContext(ioDispatcher) {
            runCatching {
                knuticeApi.getNoticeById(nttId.toString())
            }
        }


    suspend fun getNoticesByKeyword(keyword: String, nttId: Int?) =
        withContext(ioDispatcher) {
            runCatching {
                Log.d(TAG, "Received Parameter: $keyword $nttId")
                knuticeApi.getNoticesByKeyword(keyword, nttId)
            }
        }

    suspend fun getNoticeSummary(nttId: Int) =
        withContext(ioDispatcher) {
            runCatching {
                Log.d(TAG, "Summary Request for Notice $nttId")
                knuticeApi.getNoticeSummary(nttId.toString())
            }

        }

    suspend fun getTopicSubscriptionStatus(topicType: String) =
        withContext(ioDispatcher) {
            runCatching {
                if (appPreference.getCachedToken().isNullOrBlank()) throw Exception("No validated token found")
                else {
                    knuticeApi.getTopicSubscriptionStatus(
                        appPreference.getCachedToken()!!,
                        topicType
                    )
                }
            }
        }

    suspend fun getAllTips() =
        withContext(ioDispatcher) {
            runCatching {
                knuticeApi.getAllTips(deviceType = "AOS")
            }
        }

    suspend fun getCarrelRoomStatus() =
        withContext(ioDispatcher) {
            runCatching {
                if (appPreference.getCachedToken().isNullOrBlank()) throw Exception("No validated token found")
                else {
                    knuticeApi.getReadingRoomStatus(
                        appPreference.getCachedToken()!!
                    )
                }
            }
        }

    suspend fun validateToken(token: String, request: FcmTokenSaveRequest) =
        withContext(ioDispatcher) {
            runCatching {
                if (appPreference.getCachedToken().isNullOrBlank() || appPreference.getCachedToken() != token) {
                    throw Exception("Token Mismatch\nReceived: $token Cached: ${appPreference.getCachedToken()}")
                }
                knuticeApi.validateToken(appPreference.getCachedToken()!!, request)
            }
        }

    suspend fun submitUserReport(request: ReportSaveRequest) =
        withContext(ioDispatcher) {
            runCatching {
                if (appPreference.getCachedToken().isNullOrBlank()) throw Exception("No validated token found")
                else {
                    knuticeApi.submitUserReport(
                        appPreference.getCachedToken()!!, request
                    )
                }
            }
        }

    suspend fun submitTopicSubscriptionPreferences(type: TopicType, request: TopicUpdateRequest) =
        withContext(ioDispatcher) {
            runCatching {
                if (appPreference.getCachedToken().isNullOrBlank()) throw Exception("No validated token found")

                knuticeApi.submitTopicSubscriptionPreferences(
                    appPreference.getCachedToken()!!,
                    type.name,
                    request
                )
            }
        }

    suspend fun updateDeviceToken(newToken: String, request: FcmTokenUpdateRequest) =
        withContext(ioDispatcher) {
            runCatching {
                if (appPreference.getCachedToken().isNullOrBlank() || appPreference.getCachedToken() != newToken) {
                    throw Exception("Token mismatch\nReceived: $newToken Cached: ${appPreference.getCachedToken()}")
                }
                knuticeApi.updateFcmToken(newToken, request)
            }
        }
}