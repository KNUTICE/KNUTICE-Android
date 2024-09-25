package com.doyoonkim.knutice.domain

import android.util.Log
import com.doyoonkim.knutice.model.TopThreeNotices
import kotlinx.coroutines.delay

class NoticeDummySource {
    companion object {
        /**
         * TEST ONLY (Should be removed later)
         */
        suspend fun getTopThreeNoticeDummy(): TopThreeNotices {
            Log.d("NoticeRemoteSource", "Dummy Data Created")
            delay(1000)
            return TopThreeNotices(
                result = TopThreeNotices.Result(
                    resultCode = 200,
                    resultMessage = "Dummy Data",
                    resultDescription = "DummyData Created"
                ),
                body = TopThreeNotices.Body(
                    latestThreeGeneralNews = arrayListOf(
                        TopThreeNotices.Body.LatestThreeNews(
                            1, "General 0", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        TopThreeNotices.Body.LatestThreeNews(
                            2, "General 1", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        TopThreeNotices.Body.LatestThreeNews(
                            3, "General 2", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        )
                    ),
                    latestThreeAcademicNews = arrayListOf(
                        TopThreeNotices.Body.LatestThreeNews(
                            1, "Academic 0", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        TopThreeNotices.Body.LatestThreeNews(
                            2, "Academic 1", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        TopThreeNotices.Body.LatestThreeNews(
                            3, "Academic 2", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        )
                    ),
                    latestThreeScholarshipNews = arrayListOf(
                        TopThreeNotices.Body.LatestThreeNews(
                            1, "Scholarship 0", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        TopThreeNotices.Body.LatestThreeNews(
                            2, "Scholarship 1", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        TopThreeNotices.Body.LatestThreeNews(
                            3, "Scholarship 2", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        )
                    ),
                    latestThreeEventNews = arrayListOf(
                        TopThreeNotices.Body.LatestThreeNews(
                            1, "Event 0", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        TopThreeNotices.Body.LatestThreeNews(
                            2, "Event 1", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        ),
                        TopThreeNotices.Body.LatestThreeNews(
                            3, "Event 2", "https:/www.ut.ac.kr/", "Dept", "2024-00-00"
                        )
                    )
                )
            )
        }
    }
}