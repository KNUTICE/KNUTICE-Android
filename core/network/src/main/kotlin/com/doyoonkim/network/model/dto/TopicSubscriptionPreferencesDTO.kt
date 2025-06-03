package com.doyoonkim.network.model.dto

import com.google.gson.annotations.SerializedName

/**
 * @author kimdoyoon
 * Created 6/3/25 at 12:23 AM
 */
data class TopicSubscriptionPreferencesDTO(
    @SerializedName("generalNewsTopic") var generalNewsTopic: Boolean = false,
    @SerializedName("scholarshipNewsTopic") var scholarshipNewsTopic: Boolean = false,
    @SerializedName("eventNewsTopic") var eventNewsTopic: Boolean = false,
    @SerializedName("academicNewsTopic") var academicNewsTopic: Boolean = false,
    @SerializedName("employmentNewsTopic") var employmentNewsTopic: Boolean = false
)
