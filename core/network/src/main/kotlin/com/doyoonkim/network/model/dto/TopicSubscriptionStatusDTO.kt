package com.doyoonkim.network.model.dto

import androidx.annotation.Keep
import com.doyoonkim.model.TopicSubscriptionStatusVO
import com.doyoonkim.model.TopicType
import com.google.gson.annotations.SerializedName

/**
 * @author kimdoyoon
 * Created 6/3/25 at 12:23 AM
 */

@Keep
data class TopicSubscriptionStatusDTO(
    @SerializedName("subscribedTopics") val subscribed: ArrayList<String> = ArrayList<String>()
) {
    fun toVO(type: TopicType) =
        TopicSubscriptionStatusVO(
            type = type,
            subscribed = this.subscribed
        )
}
