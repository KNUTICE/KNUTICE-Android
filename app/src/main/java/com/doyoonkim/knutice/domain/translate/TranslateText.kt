package com.doyoonkim.knutice.domain.translate

import com.google.android.gms.tasks.Task
import com.google.mlkit.nl.translate.TranslateLanguage

interface TranslateText {
    fun translateTo(t: String): Task<String>
}