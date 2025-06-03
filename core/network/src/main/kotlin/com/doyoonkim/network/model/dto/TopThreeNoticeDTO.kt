package model.dto

import com.google.gson.annotations.SerializedName

/**
 * @author kimdoyoon
 * Created 6/2/25 at 11:01 PM
 */
data class TopThreeNoticeDTO(
    @SerializedName("latestThreeGeneralNews") var generalNotices: ArrayList<NoticeDTO> = arrayListOf(),
    @SerializedName("latestThreeScholarshipNews") var scholarshipNotices: ArrayList<NoticeDTO> = arrayListOf(),
    @SerializedName("latestThreeEventNews") var eventNotices: ArrayList<NoticeDTO> = arrayListOf(),
    @SerializedName("latestThreeAcademicNews") var academicNews: ArrayList<NoticeDTO> = arrayListOf()
)
