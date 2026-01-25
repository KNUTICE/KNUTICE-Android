package com.doyoonkim.domain.util

import com.doyoonkim.model.MarkdownString

class MarkdownParser {
    companion object {
        // TODO: Divier (---), Heading 1, 3, plain text with hyperlink, table
        suspend fun parseRawString(rawString: String?): List<MarkdownString> {
            val elements = mutableListOf<MarkdownString>()
            if (rawString.isNullOrBlank()) return elements

            // Process Raw String.
            // 1. Separate Line-by-line
            val lines = rawString.split("\n\n")
//            println(lineSeparated)

            lines.forEach { line ->
                val trimed = line.trim()
                when {
                    trimed.startsWith("# ") -> {
                        elements.add(MarkdownString.Heading(1, trimed.removePrefix("# ")))
                    }
                    trimed.startsWith("## ") -> {
                        elements.add(MarkdownString.Heading(2, trimed.removePrefix("## ")))
                    }
                    trimed.startsWith("### ") -> {
                        elements.add(MarkdownString.Heading(3, trimed.removePrefix("### ")))
                    }
                    trimed.startsWith("*  ") -> {
                        trimed.split("\n").forEach { string ->
                            val level = string.takeWhile { it == ' ' }.length / 4
                            elements.add(MarkdownString.BulletPoint(
                                text = string.trimStart().removePrefix("*").trimStart(), level = level)
                            )
                        }
                    }
                    trimed.startsWith("| ") -> {
                        val table = tableProcess(trimed)
                        elements.add(table)
                    }
                    trimed.contentEquals("---") -> {
                        elements.add(MarkdownString.Divider)
                    }
                    else -> {
                        elements.add(MarkdownString.Body(trimed))
                    }
                }
            }
            return elements
        }

        private suspend fun tableProcess(tableRowString: String): MarkdownString.Table {
            val headerElements = mutableListOf<String>()
            val contentElements = mutableListOf<List<String>>()
            val split = tableRowString.split("\n").map { it.removePrefix("| ") }
            // Add Header
            headerElements.addAll(
                split[0]
                    .removePrefix("|")
                    .removeSuffix("|")
                    .split("|")
                    .map { it.trim() }
            )

            var idx = 2
            while (idx < split.size) {
                contentElements.add(
                    split[idx]
                        .removePrefix("|")
                        .removeSuffix("|")
                        .split("|")
                        .map { it.trim() }
                )
                idx++
            }
            return MarkdownString.Table(header = headerElements, contents = contentElements)
        }
    }
}