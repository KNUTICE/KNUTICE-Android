package com.doyoonkim.network

import android.util.Log
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

    suspend fun getTopicSubscriptionStatus(token: String?, topicType: String) =
        withContext(ioDispatcher) {
            runCatching {
                if (token.isNullOrBlank()) {
                    throw Exception("No validated token found")
                } else {
                    knuticeApi.getTopicSubscriptionStatus(
                        token,
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

    suspend fun getCarrelRoomStatus(token: String?) =
        withContext(ioDispatcher) {
            runCatching {
                if (token.isNullOrBlank()) {
                    throw Exception("No validated token found")
                } else {
                    knuticeApi.getReadingRoomStatus(
                        token
                    )
                }
            }
        }

    suspend fun validateToken(cachedToken: String?, receivedToken: String, request: FcmTokenSaveRequest) =
        withContext(ioDispatcher) {
            runCatching {
                if (cachedToken.isNullOrBlank() || cachedToken != receivedToken) {
                    throw Exception("Token Mismatch\nReceived: $receivedToken Cached: $cachedToken")
                }
                knuticeApi.validateToken(cachedToken, request)
            }
        }

    suspend fun submitUserReport(token: String?, request: ReportSaveRequest) =
        withContext(ioDispatcher) {
            runCatching {
                if (token.isNullOrBlank()) {
                    throw Exception("No validated token found")
                } else {
                    knuticeApi.submitUserReport(
                        token,
                        request
                    )
                }
            }
        }

    suspend fun submitTopicSubscriptionPreferences(token: String?, type: TopicType, request: TopicUpdateRequest) =
        withContext(ioDispatcher) {
            runCatching {
                if (token.isNullOrBlank()) throw Exception("No validated token found")

                knuticeApi.submitTopicSubscriptionPreferences(
                    token,
                    type.name,
                    request
                )
            }
        }

    suspend fun updateDeviceToken(cachedToken: String?, newToken: String, request: FcmTokenUpdateRequest) =
        withContext(ioDispatcher) {
            runCatching {
                if (cachedToken.isNullOrBlank() || cachedToken != newToken) {
                    throw Exception("Token mismatch\nReceived: $newToken Cached: $cachedToken")
                }
                knuticeApi.updateFcmToken(newToken, request)
            }
        }
}
