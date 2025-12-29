package model.dto

import androidx.annotation.Keep
import com.doyoonkim.model.NoticeVO
import com.google.gson.annotations.SerializedName

/**
 * @author kimdoyoon
 * Created 6/2/25 at 11:08 PM
 */
@Keep
data class NoticeDTO(
    @SerializedName("nttId") var nttId: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("contentUrl") var contentUrl: String? = null,
    @SerializedName("contentImageUrl") var contentImage: String? = null,
    @SerializedName("department") var departName: String? = null,
    @SerializedName("registrationDate") var registeredAt: String? = null,
    @SerializedName("topic") var topic: String? = null,
    @SerializedName("isAttachment") var hasAttachment: Boolean? = null
) {
    fun toVO() =
        NoticeVO(
            nttId = this.nttId ?: -1,
            title = this.title ?: "",
            url = this.contentUrl ?: "",
            imageUrl = this.contentImage,
            departName = this.departName ?: "",
            timestamp = this.registeredAt ?: "",
            noticeName = this.topic ?: ""
        )
}

//@Keep
//data class Topic(
//    @SerializedName("bbsPath") var bbsPath: String? = null,
//    @SerializedName("category") var category: String? = null,
//    @SerializedName("noticeUrl") var noticeUrl: String? = null,
//    @SerializedName("rootDomain") var rootDomain: String? = null,
//    @SerializedName("topicName") var topicName: String? = null
//)

