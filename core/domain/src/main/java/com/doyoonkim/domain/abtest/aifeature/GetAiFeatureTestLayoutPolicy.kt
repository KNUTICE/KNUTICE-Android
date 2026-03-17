package com.doyoonkim.domain.abtest.aifeature

import com.doyoonkim.domain.interfaces.abtest.FirebaseRemoteConfigRepository
import javax.inject.Inject

// Layout Policy
sealed interface AiFeatureAbTestLayoutPolicy {
    val variantName: String

    // Variant A (Control)
    data class VariantA(override val variantName: String = "layout_a"): AiFeatureAbTestLayoutPolicy

    // Variant B (Retention)
    data class VariantB(override val variantName: String = "layout_b"): AiFeatureAbTestLayoutPolicy
}


class GetAiFeatureTestLayoutPolicy @Inject constructor(
  private val remoteConfigRepository: FirebaseRemoteConfigRepository
) {

    private val TAG = "GetAiFeatureTestLayoutPolicy"

    fun getLayoutPolicy(): AiFeatureAbTestLayoutPolicy {
        val rawValue = remoteConfigRepository.getTestRawVariantValue("layout_type")
        println("$TAG\t Received Variant: $rawValue")
        return when (rawValue) {
            "layout_a" -> AiFeatureAbTestLayoutPolicy.VariantA()
            "layout_b" -> AiFeatureAbTestLayoutPolicy.VariantB()
            else -> AiFeatureAbTestLayoutPolicy.VariantA()      // Self-healing
        }
    }


}