package com.doyoonkim.model

sealed interface MarkdownString {
    data class Heading(val weight: Int, val text: String) : MarkdownString
    data class Body(val text: String) : MarkdownString
    data class BulletPoint(val text: String, val level: Int) : MarkdownString
    data class Table(val header: List<String>, val contents: List<List<String>>) : MarkdownString
    data object Divider : MarkdownString
}
