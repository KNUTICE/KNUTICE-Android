package com.doyoonkim.knutice.domain.translate

import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ToJapanese @Inject constructor() : TranslateText {
    private val koreanJapaneseTranslator by lazy {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.KOREAN)
            .setTargetLanguage(TranslateLanguage.JAPANESE)
            .build()
        Translation.getClient(options)
    }

    fun downloadLanguageModel() = callbackFlow<Boolean> {
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        koreanJapaneseTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                trySend(true)
            }
            .addOnFailureListener {
                trySend(false)
            }
    }

    override fun translateTo(t: String): Task<String> {
        TODO("Not yet implemented")
    }
}