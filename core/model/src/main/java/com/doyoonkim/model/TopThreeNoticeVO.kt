package com.doyoonkim.model

data class TopThreeNoticeVO(
    val general: List<NoticeVO> = listOf(),
    val scholarship: List<NoticeVO> = listOf(),
    val event: List<NoticeVO> = listOf(),
    val academic: List<NoticeVO> = listOf()
)
