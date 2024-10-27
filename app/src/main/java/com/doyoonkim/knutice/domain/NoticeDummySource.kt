package com.doyoonkim.knutice.domain

import android.util.Log
import com.doyoonkim.knutice.model.RawNoticeData
import com.doyoonkim.knutice.model.Result
import com.doyoonkim.knutice.model.TopThreeNotices
import kotlinx.coroutines.delay

class NoticeDummySource {
    companion object {
        /**
         * TEST ONLY (Should be removed later)
         */
        suspend fun getTopThreeNoticeDummy(): TopThreeNotices {
            Log.d("KnuticeRemoteSource", "Dummy Data Created")
            delay(1000)
            return TopThreeNotices(
                result = Result(
                    resultCode = 200,
                    resultMessage = "Dummy Data",
                    resultDescription = "DummyData Created"
                ),
                body = TopThreeNotices.Body(
                    latestThreeGeneralNews = arrayListOf(
                        RawNoticeData(
                            1, "General 0", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        RawNoticeData(
                            2, "General 1", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        RawNoticeData(
                            3, "General 2", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        )
                    ),
                    latestThreeAcademicNews = arrayListOf(
                        RawNoticeData(
                            1, "Academic 0", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        RawNoticeData(
                            2, "Academic 1", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        RawNoticeData(
                            3, "Academic 2", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        )
                    ),
                    latestThreeScholarshipNews = arrayListOf(
                        RawNoticeData(
                            1, "Scholarship 0", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        RawNoticeData(
                            2, "Scholarship 1", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        RawNoticeData(
                            3, "Scholarship 2", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        )
                    ),
                    latestThreeEventNews = arrayListOf(
                        RawNoticeData(
                            1, "Event 0", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        RawNoticeData(
                            2, "Event 1", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        RawNoticeData(
                            3, "Event 2", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        )
                    )
                )
            )
        }
    }
}