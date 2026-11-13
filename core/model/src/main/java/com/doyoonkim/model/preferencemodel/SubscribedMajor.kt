package com.doyoonkim.model.preferencemodel

import kotlinx.serialization.Serializable

@Serializable
data class SubscribedMajors(
    val updated: Long,
    val subscriptions: List<Major>
)

@Serializable
data class Major(
    val id: Int,
    val legacyId: String?, // Regression
    val name: String,
    val college: String?
)
