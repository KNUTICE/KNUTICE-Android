package com.doyoonkim.domain.di

import com.doyoonkim.domain.usecases.FetchAllBookmarks
import com.doyoonkim.domain.usecases.FetchAllBookmarksImpl
import com.doyoonkim.domain.usecases.FetchNoticeById
import com.doyoonkim.domain.usecases.FetchNoticeByIdFromLocal
import com.doyoonkim.domain.usecases.FetchNoticeByIdFromLocalImpl
import com.doyoonkim.domain.usecases.FetchNoticeByIdImpl
import com.doyoonkim.domain.usecases.FetchNoticesByKeyword
import com.doyoonkim.domain.usecases.FetchNoticesByKeywordImpl
import com.doyoonkim.domain.usecases.FetchNoticesPerPage
import com.doyoonkim.domain.usecases.FetchNoticesPerPageImpl
import com.doyoonkim.domain.usecases.FetchTopThreeNotices
import com.doyoonkim.domain.usecases.FetchTopThreeNoticesImpl
import com.doyoonkim.domain.usecases.FetchTopicSubscriptionStatus
import com.doyoonkim.domain.usecases.FetchTopicSubscriptionStatusImpl
import com.doyoonkim.domain.usecases.ModifyBookmark
import com.doyoonkim.domain.usecases.ModifyBookmarkImpl
import com.doyoonkim.domain.usecases.SubmitNotificationPreferences
import com.doyoonkim.domain.usecases.SubmitNotificationPreferencesImpl
import com.doyoonkim.domain.usecases.SubmitUserReport
import com.doyoonkim.domain.usecases.SubmitUserReportImpl
import com.doyoonkim.domain.usecases.SyncDataWithUpdateDatabase
import com.doyoonkim.domain.usecases.SyncDataWithUpdatedDatabaseImpl
import com.doyoonkim.domain.usecases.ValidateDeviceToken
import com.doyoonkim.domain.usecases.ValidateDeviceTokenImpl
import dagger.Binds
import dagger.Module

@Module
abstract class DomainModule {

    @Binds
    abstract fun bindFetchAllBookmarks(
        impl: FetchAllBookmarksImpl
    ): FetchAllBookmarks

    @Binds
    abstract fun bindsFetchNoticeById(
        impl: FetchNoticeByIdImpl
    ): FetchNoticeById

   @Binds
   abstract fun bindsFetchNoticeByIdFromLocal(
       impl: FetchNoticeByIdFromLocalImpl
   ): FetchNoticeByIdFromLocal

   @Binds
   abstract fun bindsFetchNoticesByKeyword(
       impl: FetchNoticesByKeywordImpl
   ): FetchNoticesByKeyword

   @Binds
   abstract fun bindsFetchNoticesPerPage(
       impl: FetchNoticesPerPageImpl
   ): FetchNoticesPerPage

   @Binds
   abstract fun bindsFetchTopicSubscriptionStatus(
       impl: FetchTopicSubscriptionStatusImpl
   ): FetchTopicSubscriptionStatus

   @Binds
   abstract fun bindsFetchTopThreeNotices(
       impl: FetchTopThreeNoticesImpl
   ): FetchTopThreeNotices

   @Binds
   abstract fun bindsModifyBookmark(
       impl: ModifyBookmarkImpl
   ): ModifyBookmark

   @Binds
   abstract fun bindsSubmitNotificationPReferences(
       impl: SubmitNotificationPreferencesImpl
   ): SubmitNotificationPreferences

   @Binds
   abstract fun bindsSubmitUserReport(
       impl: SubmitUserReportImpl
   ): SubmitUserReport

   @Binds
   abstract fun bindsValidateDeviceToken(
       impl: ValidateDeviceTokenImpl
   ): ValidateDeviceToken

   @Binds
   abstract fun bindsSyncDataWithUpdatedDatabase(
       impl: SyncDataWithUpdatedDatabaseImpl
   ): SyncDataWithUpdateDatabase

}