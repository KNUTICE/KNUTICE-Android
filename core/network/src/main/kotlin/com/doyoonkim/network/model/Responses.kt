package model

import androidx.annotation.Keep
import com.doyoonkim.network.model.dto.NoticeSummaryDTO
import com.doyoonkim.network.model.dto.TipDTO
import com.doyoonkim.network.model.dto.TopicSubscriptionStatusDTO
import com.google.gson.annotations.SerializedName
import model.dto.NoticeDTO
import model.dto.TopThreeNoticeDTO

/**
 * @author kimdoyoon
 * Created 6/2/25 at 11:04 PM
 */

@Keep
data class Metadata(
    @SerializedName("success") var isSuccessful: Boolean? = null,
    @SerializedName("code") var resultCode: Int? = null,
    @SerializedName("message") var resultMessage: String? = null
)

@Keep
data class TopThreeNoticeResults(
    @SerializedName("metaData") var result: Metadata? = Metadata(),
    @SerializedName("data") var body: TopThreeNoticeDTO? = TopThreeNoticeDTO()
)

@Keep
data class NoticesPerPageResult(
    @SerializedName("metaData") var result: Metadata? = Metadata(),
    @SerializedName("data") var body: ArrayList<NoticeDTO>?
)

@Keep
data class NoticeByIdResult(
    @SerializedName("metaData") var result: Metadata? = Metadata(),
    @SerializedName("data") var body: NoticeDTO? = NoticeDTO()
)

@Keep
data class NoticesByKeywordResult(
    @SerializedName("metaData") var result: Metadata? = Metadata(),
    @SerializedName("data") var body: ArrayList<NoticeDTO>?
)

@Keep
data class NoticeSummaryResult(
    @SerializedName("metaData") var result: Metadata? = Metadata(),
    @SerializedName("data") var body: NoticeSummaryDTO?
)

@Keep
data class TopicSubscriptionPreferencesResult(
    @SerializedName("metaData") var result: Metadata? = Metadata(),
    @SerializedName("data") var body: TopicSubscriptionStatusDTO? = TopicSubscriptionStatusDTO()
)

@Keep
data class PostResult(
    @SerializedName("metaData") var result: Metadata? = Metadata(),
    @SerializedName("data") var body: Boolean? = null
)

@Keep
data class PatchResult(
    @SerializedName("metaData") var result: Metadata? = Metadata(),
    @SerializedName("data") var body: Boolean? = null
)

@Keep
data class TipResult(
    @SerializedName("metaData") var result: Metadata? = Metadata(),
    @SerializedName("data") var body: ArrayList<TipDTO>? = null
)
