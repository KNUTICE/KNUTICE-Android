package com.doyoonkim.model

data class TopThreeNoticeVO(
    val general: List<NoticeVO> = listOf(),
    val scholarship: List<NoticeVO> = listOf(),
    val event: List<NoticeVO> = listOf(),
    val academic: List<NoticeVO> = listOf(),
    val employment: List<NoticeVO> = listOf()
) {
    fun isEmpty(): Boolean {
        return general.isEmpty() &&
            scholarship.isEmpty() &&
            event.isEmpty() &&
            academic.isEmpty() &&
            employment.isEmpty()
    }
}
