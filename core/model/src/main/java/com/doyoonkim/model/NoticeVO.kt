package com.doyoonkim.model

data class NoticeVO(
    val entityId: Int? = null, // Later migrated and removed.
    val nttId: Int = -1,
    val title: String = "",
    val url: String = "",
    val imageUrl: String? = null,
    val departName: String = "",
    val timestamp: String = "",
    val noticeName: String = "",
    val isSummaryAvailable: Boolean = false,
    val isRecent: Boolean = false
) {
    companion object {
        fun NoticeVO?.isEmpty(): Boolean = this.isNotEmpty()

        fun NoticeVO?.isNotEmpty(): Boolean {
            return this != null && nttId != -1
        }
    }
}
