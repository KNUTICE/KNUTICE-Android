package com.doyoonkim.domain.util

import org.openkoreantext.processor.OpenKoreanTextProcessorJava

class KoreanTokenizer {

    companion object {
        fun getTokenizedString(target: String): String {
            val token = OpenKoreanTextProcessorJava.tokenize(target)
            return OpenKoreanTextProcessorJava
                .tokensToJavaStringList(token)
                .joinToString(" ")
        }
    }

}