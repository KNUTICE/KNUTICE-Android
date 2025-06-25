package com.doyoonkim.network.model.dto

import androidx.annotation.Keep
import com.doyoonkim.model.TopicSubscriptionPreferencesVO
import com.google.gson.annotations.SerializedName

/**
 * @author kimdoyoon
 * Created 6/3/25 at 12:23 AM
 */
@Keep
data class TopicSubscriptionPreferencesDTO(
    @SerializedName("generalNewsTopic") var generalNewsTopic: Boolean = false,
    @SerializedName("scholarshipNewsTopic") var scholarshipNewsTopic: Boolean = false,
    @SerializedName("eventNewsTopic") var eventNewsTopic: Boolean = false,
    @SerializedName("academicNewsTopic") var academicNewsTopic: Boolean = false,
    @SerializedName("employmentNewsTopic") var employmentNewsTopic: Boolean = false
) {
    fun toVO() =
        TopicSubscriptionPreferencesVO(
            general = this.generalNewsTopic,
            scholarship = this.scholarshipNewsTopic,
            event = this.eventNewsTopic,
            academic = this.academicNewsTopic,
            employment = this.employmentNewsTopic
        )
}
