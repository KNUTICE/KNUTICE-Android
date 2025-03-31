package com.doyoonkim.knutice.domain.translate

import com.doyoonkim.knutice.model.Notice
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import javax.inject.Inject

class TextTranslator @Inject constructor() : TranslateText {
    private val translator by lazy {
        Translation.getClient(initializeTranslator())
    }

    var translationNeeded: Boolean = false

    private fun initializeTranslator(): TranslatorOptions {
        return TranslatorOptions.Builder().apply {
            setSourceLanguage(TranslateLanguage.KOREAN)
            when (Locale.getDefault()) {
                Locale.JAPANESE, Locale.JAPAN -> {
                    setTargetLanguage(TranslateLanguage.JAPANESE)
                }
                Locale.ENGLISH -> {
                    setTargetLanguage(TranslateLanguage.ENGLISH)
                }
            }
        }.build().also {
            translationNeeded = true
        }
    }

    // Need to be revised later.
    fun downloadLanguageModel(): Task<Void> {
        val condition = DownloadConditions.Builder()
            .requireWifi()
            .build()
        return translator.downloadModelIfNeeded(condition)
    }


    override fun translateTo(t: String): Task<String> {
        return translator.translate(t)
    }

    fun translate(t: String) = callbackFlow<Result<String>> {
        translator.translate(t).addOnSuccessListener {
            trySend(Result.success(it))
        }.addOnFailureListener {
            trySend(Result.failure(it))
        }
        awaitClose {  }
    }

    fun translate(source: List<Notice>): List<Notice> {
        val translated = mutableListOf<Notice>()
        source.forEach { notice ->
            translator.run {
                translate(notice.title).addOnSuccessListener {  }
            }
        }
        return source
    }

    suspend fun translate_suspended(source: String) : String {
        return translator.translate(source).result
    }

}