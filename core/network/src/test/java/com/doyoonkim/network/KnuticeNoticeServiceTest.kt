package com.doyoonkim.network

import com.doyoonkim.network.model.dto.NoticeSummaryDTO
import com.doyoonkim.network.retrofit.KnuticeNoticeService
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import model.Metadata
import model.NoticeByIdResult
import model.NoticeSummaryResult
import model.NoticesPerPageResult
import model.dto.NoticeDTO
import okhttp3.Headers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class KnuticeNoticeServiceTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var api: KnuticeNoticeService

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KnuticeNoticeService::class.java)
    }

    @After
    fun tearDown() {
        mockServer.close()
    }

    @Test
    fun deserializeResponse_toNoticesPerPageResult_onSuccess() = runBlocking {
        val mockResponse = createMockResponse(
            code = 200,
            body = """
                {
                  "metaData": {
                    "success": true,
                    "code": 200,
                    "message": "success"
                  },
                  "data": [
                    {
                      "nttId": 1,
                      "title": "Notice 1",
                      "contentUrl": "noticeUrl1",
                      "contentImageUrl": null,
                      "isContentSummary": true,
                      "department": "department1",
                      "registrationDate": "2026-10-23",
                      "isAttachment": true,
                      "topic": "GENERAL_NEWS",
                      "topicId": 1
                    }
                  ]
                }
            """.trimIndent()
        )
        mockServer.enqueue(mockResponse)

        val expected = NoticesPerPageResult(
            result = Metadata(
                isSuccessful = true,
                resultCode = 200,
                resultMessage = "success"
            ),
            body = arrayListOf(
                NoticeDTO(
                    nttId = 1,
                    title = "Notice 1",
                    contentUrl = "noticeUrl1",
                    contentImage = null,
                    isSummaryAvailable = true,
                    departName = "department1",
                    registeredAt = "2026-10-23",
                    hasAttachment = true,
                    topic = "GENERAL_NEWS",
                    topicId = 1
                )
            )
        )

        val actual = api.getNoticesPerPage(
            topicId = 1,
            size = 1
        )
        assertEquals(expected, actual)
    }

    @Test
    fun deserializeResponse_toNoticesPerPageResult_onFailure() = runBlocking {
        deserializeFailureCase(
            expected = NoticesPerPageResult(
                result = Metadata(
                    isSuccessful = false,
                    resultCode = 404,
                    resultMessage = "Notices not exists"
                ),
                body = null
            ),
            actual = {
                api.getNoticesPerPage(
                    topicId = -1,
                    size = 5
                )
            }
        )
    }

    @Test
    fun deserializeResponse_toNoticeByIdResult_onSuccess() = runBlocking {
        val mockResponse = createMockResponse(
            code = 200,
            body = """
                {
                  "metaData": {
                    "success": true,
                    "code": 200,
                    "message": "success"
                  },
                  "data": {
                    "nttId": 1,
                    "title": "Notice 1",
                    "contentUrl": "noticeUrl1",
                    "contentImageUrl": null,
                    "isContentSummary": true,
                    "department": "department1",
                    "registrationDate": "2026-10-23",
                    "isAttachment": true,
                    "topic": "GENERAL_NEWS",
                    "topicId": 1
                  }
                }
            """.trimIndent()
        )
        mockServer.enqueue(mockResponse)

        val expected = NoticeByIdResult(
            result = Metadata(
                isSuccessful = true,
                resultCode = 200,
                resultMessage = "success"
            ),
            body = NoticeDTO(
                nttId = 1,
                title = "Notice 1",
                contentUrl = "noticeUrl1",
                contentImage = null,
                isSummaryAvailable = true,
                departName = "department1",
                registeredAt = "2026-10-23",
                hasAttachment = true,
                topic = "GENERAL_NEWS",
                topicId = 1
            )
        )

        val actual = api.getNoticeById(nttId = 1)
        assertEquals(expected, actual)
    }

    @Test
    fun deserializeResponse_toNoticeByIdResult_onFailure() = runBlocking {
        deserializeFailureCase(
            expected = NoticesPerPageResult(
                result = Metadata(
                    isSuccessful = false,
                    resultCode = 404,
                    resultMessage = "Notices not exists"
                ),
                body = null
            ),
            actual = {
                api.getNoticesPerPage(
                    topicId = -1,
                    size = 5
                )
            }
        )
    }

    @Test
    fun deserializeResponse_toNoticeSummaryResult_onSuccess() = runBlocking {
        val mockResponse = createMockResponse(
            code = 200,
            body = """
                {
                  "metaData": {
                    "success": true,
                    "code": 200,
                    "message": "success"
                  },
                  "data": {
                    "nttId": 1,
                    "contentSummary": "summary of target notice"
                  }
                }
            """.trimIndent()
        )
        mockServer.enqueue(mockResponse)

        val expected = NoticeSummaryResult(
            result = Metadata(
                isSuccessful = true,
                resultCode = 200,
                resultMessage = "success"
            ),
            body = NoticeSummaryDTO(
                nttId = 1,
                rawSummary = "summary of target notice"
            )
        )

        val actual = api.getNoticesSummary(1)
        assertEquals(expected, actual)
    }

    @Test
    fun deserializeResponse_toNoticeSummaryResult_onFailure() = runBlocking {
        deserializeFailureCase(
            expected = NoticeSummaryResult(
                result = Metadata(
                    isSuccessful = false,
                    resultCode = 404,
                    resultMessage = "Notices not exists"
                ),
                body = null
            ),
            actual = {
                api.getNoticesSummary(-1)
            }
        )
    }

    private inline fun <reified T> deserializeFailureCase(expected: T, actual: () -> T) {
        val mockResponse = createMockResponse(
            code = 404,
            body = """
                {
                  "metaData": {
                    "success": false,
                    "code": 404,
                    "message": "Notices not exists"
                  }
                }
            """.trimIndent()
        )
        mockServer.enqueue(mockResponse)

        try {
            actual()
            fail("Expected Exception is not thrown")
        } catch (e: HttpException) {
            assertEquals(404, e.code())
            val errorResponseJson = e.response()?.errorBody()?.string()
            assertNotNull(errorResponseJson)

            val actual = Gson().fromJson(errorResponseJson, T::class.java)
            assertEquals(expected, actual)
        }
    }

    private fun createMockResponse(code: Int, headers: Headers? = null, body: String): MockResponse {
        return MockResponse(
            code = code,
            headers = headers ?: Headers.headersOf("Content-Type", "application/json"),
            body = body
        )
    }
}
