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
    @SerializedName("contentImage") var contentImage: String? = null,
    @SerializedName("departmentName") var departName: String? = null,
    @SerializedName("registeredAt") var registeredAt: String? = null,
    @SerializedName("noticeName") var noticeCategory: String? = null
) {
    fun toVO() =
        NoticeVO(
            nttId = this.nttId ?: -1,
            title = this.title ?: "",
            url = this.contentUrl ?: "",
            imageUrl = this.contentImage,
            departName = this.departName ?: "",
            timestamp = this.registeredAt ?: "",
            noticeName = this.noticeCategory ?: ""
        )
}
