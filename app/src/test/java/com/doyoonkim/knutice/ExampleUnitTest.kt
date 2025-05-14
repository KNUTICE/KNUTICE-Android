package com.doyoonkim.knutice

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.doyoonkim.knutice.data.KnuticeService
import com.doyoonkim.knutice.data.NoticeLocalRepository
import com.doyoonkim.knutice.model.ApiDeviceTokenRequest
import com.doyoonkim.knutice.model.DeviceTokenRequest
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.model.ApiTopicSubscriptionRequest
import com.doyoonkim.knutice.model.ManageTopicRequest
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun retrofitRequestValidation() = runTest {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_ROOT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val requestResult = retrofit.create(KnuticeService::class.java)
            .getFirstPageOfNotice(NoticeCategory.GENERAL_NEWS)

        requestResult.body.forEach(::println)

        assertEquals(200, requestResult.result?.resultCode)
    }

    @Test
    fun postRequest() = runTest {
        val token = "etIbGyZaQ6aBgU5MMmjlbk:APA91bGrSkcX8xQF-w3fdZjoSGimtASOOKfjQVE9-gMpQpp6XBQAOleuIETp3b2XyvvgdZEmVHkJt89_wEFrAzJ8mUYKNLVqdPkkTZciq4kImbsw_VpJQiES1P6-LPGp2vAmss1L2ek4"

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.API_ROOT)
            .build()
        retrofit.create(KnuticeService::class.java).validateToken(
            ApiDeviceTokenRequest(body = DeviceTokenRequest(token))
        ).run {
            println(result)
            if (this.result?.resultCode == 200) println("TEST: Token Validated")
            else println("TEST: Unable to validate token")
        }
    }

    @Test
    fun queryNotices() = runTest {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.API_ROOT)
            .build()

        retrofit.create(KnuticeService::class.java).queryNoticeByKeyword(
            "공지"
        ).run {
            println(this)
        }
    }

    @Test
    fun updateNotificationChannelPreference() = runTest {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.API_ROOT)
            .build()

        retrofit.create(KnuticeService::class.java).submitTopicSubscriptionPreference(
            ApiTopicSubscriptionRequest(
                body = ManageTopicRequest(
                    "eGqbVMCpSdqGhE_j_fyvAK:APA91bGkrrPKR8QNMcM4Y3fQuiCKI-wCBOyFX1CDuziuWuGsqnXT-n7nqMxtf8Sp9frS1bHH_Rr8eKE3V5jvY2SZLuRp6iD3S4pYeFa0hRrDRK2SZiP-DwQ",
                    NoticeCategory.GENERAL_NEWS.name,
                    true
                )
            )
        ).run {
            if (this.result?.resultCode == 200) {
                println("[UnitTest] Topic preference has been updated\n${this.body}")
            } else {
                println("[UnitTest] Failed to update topic preference.\nREASON:${this.body ?: false}")
            }
        }
    }

    @Test
    fun roomDatabaseTest() = runTest {


    }

    /*
    @Entity
data class NoticeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo("ntt_id") val nttId: Int = -1,
    @ColumnInfo("notice_title") val title: String,
    @ColumnInfo("notice_url") val url: String,
    @ColumnInfo("notice_image") val imageUrl: String,
    @ColumnInfo("info_dept") val departName: String,
    @ColumnInfo("info_timestamp") val timestamp: String
)

@Entity
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val bookmarkId: Int,
    @ColumnInfo("bookmarked_notice") val notice: NoticeEntity,
    @ColumnInfo("isScheduled") val isScheduled: Boolean,
    @ColumnInfo("remind_schedule") val reminderSchedule: String,
    @ColumnInfo("bookmark_note") val note: String
)
     */

}