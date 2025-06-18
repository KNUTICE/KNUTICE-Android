package model

import com.doyoonkim.network.model.dto.TopicSubscriptionPreferencesDTO
import com.google.gson.annotations.SerializedName
import model.dto.NoticeDTO
import model.dto.TopThreeNoticeDTO

/**
 * @author kimdoyoon
 * Created 6/2/25 at 11:04 PM
 */
data class NetworkResult(
    @SerializedName("resultCode") var resultCode: Int? = null,
    @SerializedName("resultMessage") var resultMessage: String? = null,
    @SerializedName("resultDescription") var resultDescription: String? = null
)

data class TopThreeNoticeResults(
    @SerializedName("result") var result: NetworkResult? = NetworkResult(),
    @SerializedName("body") var body: TopThreeNoticeDTO? = TopThreeNoticeDTO()
)

data class NoticesPerPageResult(
    @SerializedName("result") var result: NetworkResult? = NetworkResult(),
    @SerializedName("body") var body: ArrayList<NoticeDTO>
)

data class NoticeByIdResult(
    @SerializedName("result") var result: NetworkResult? = NetworkResult(),
    @SerializedName("body") var body: NoticeDTO? = NoticeDTO()
)

data class NoticesByKeywordResult(
    @SerializedName("result") var result: NetworkResult? = NetworkResult(),
    @SerializedName("body") var body: ArrayList<NoticeDTO>
)

data class TopicSubscriptionPreferencesResult(
    @SerializedName("reuslt") var result: NetworkResult? = NetworkResult(),
    @SerializedName("body") var body: TopicSubscriptionPreferencesDTO? = TopicSubscriptionPreferencesDTO()
)

data class PostResult(
    @SerializedName("result") var result: NetworkResult? = NetworkResult(),
    @SerializedName("body") var body: Boolean? = null
)
