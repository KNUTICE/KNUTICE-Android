package com.doyoonkim.network

import com.doyoonkim.network.model.TopicSubscriptionUpdateRequest
import com.doyoonkim.network.model.dto.SubscribedTopic
import com.doyoonkim.network.model.dto.TopicSubscriptionStatus
import com.doyoonkim.network.retrofit.KnuticeTopicService
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import model.Metadata
import model.TopicSubscriptionStatusResponse
import model.TopicSubscriptionUpdateResponse
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

class KnuticeTopicServiceTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var api: KnuticeTopicService

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KnuticeTopicService::class.java)
    }

    @After
    fun tearDown() {
        mockServer.close()
    }

    @Test
    fun deserializeResponse_getAvailableTopic_onSuccess() = runBlocking {
        val mockResponse = MockResponse(
            code = 200,
            headers = Headers.headersOf("Content-Type", "application/json"),
            body = """
                {
                  "metaData": {
                    "success": true,
                    "code": 200,
                    "message": "success"
                  },
                  "data": {
                    "subscribedTopics": [
                      {
                        "topic": "topic",
                        "topicId": 1,
                        "name": "Test Topic",
                        "college": "Test College"
                      }
                    ]
                  }
                }
            """.trimIndent()
        )
        mockServer.enqueue(mockResponse)

        val expected = TopicSubscriptionStatusResponse(
            result = Metadata(
                isSuccessful = true,
                resultCode = 200,
                resultMessage = "success"
            ),
            body = TopicSubscriptionStatus(
                subscribed = listOf(
                    SubscribedTopic(
                        topic = "topic",
                        id = 1,
                        name = "Test Topic",
                        college = "Test College"
                    )
                )
            )
        )

        val actual = api.getAvailableTopic("token", "type")
        assertEquals(expected, actual)
    }

    @Test
    fun deserializeResponse_getAvailableTopic_onFailure() = runBlocking {
        val mockResponse = MockResponse(
            code = 404,
            headers = Headers.headersOf("Content-Type", "application/json"),
            body = """
                {
                  "metaData": {
                    "success": false,
                    "code": 404,
                    "message": "failure"
                  }
                }
            """.trimIndent()
        )
        mockServer.enqueue(mockResponse)

        val expected = TopicSubscriptionStatusResponse(
            result = Metadata(
                isSuccessful = false,
                resultCode = 404,
                resultMessage = "failure"
            ),
            body = null
        )

        try {
            api.getAvailableTopic("token", "type")
            fail("Expected Exception is not thrown")
        } catch (e: HttpException) {
            assertEquals(404, e.code())
            val errorResponseJson = e.response()?.errorBody()?.string()
            assertNotNull(errorResponseJson)

            val actual = Gson().fromJson(errorResponseJson, TopicSubscriptionStatusResponse::class.java)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun deserialize_updateTopicSubscription_onSuccess() = runBlocking {
        val mockResponse = MockResponse(
            code = 200,
            headers = Headers.headersOf("Content-Type", "application/json"),
            body = """
                {
                  "metaData": {
                    "success": true,
                    "code": 200,
                    "message": "success"
                  },
                  "data": true
                }
            """.trimIndent()
        )
        mockServer.enqueue(mockResponse)

        val expected = TopicSubscriptionUpdateResponse(
            result = Metadata(
                isSuccessful = true,
                resultCode = 200,
                resultMessage = "success"
            ),
            body = true
        )

        val actual = api.updateTopicSubscription(
            token = "token",
            type = "type",
            request = TopicSubscriptionUpdateRequest(
                topicId = 0,
                enabled = true
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun deserialize_updateTopicSubscription_onFailure() = runBlocking {
        val mockResponse = MockResponse(
            code = 404,
            headers = Headers.headersOf("Content-Type", "application/json"),
            body = """
                {
                  "metaData": {
                    "success": false,
                    "code": 404,
                    "message": "failure"
                  }
                }
            """.trimIndent()
        )
        mockServer.enqueue(mockResponse)

        val expected = TopicSubscriptionUpdateResponse(
            result = Metadata(
                isSuccessful = false,
                resultCode = 404,
                resultMessage = "failure"
            ),
            body = null
        )

        try {
            api.updateTopicSubscription(
                token = "token",
                type = "type",
                request = TopicSubscriptionUpdateRequest(
                    topicId = 0,
                    enabled = true
                )
            )
            fail("Expected Exception is not thrown")
        } catch (e: HttpException) {
            assertEquals(404, e.code())

            val errorBodyResponseJson = e.response()?.errorBody()?.string()
            assertNotNull(errorBodyResponseJson)

            val actual = Gson().fromJson(errorBodyResponseJson, TopicSubscriptionUpdateResponse::class.java)
            assertEquals(expected, actual)
        }
    }
}
