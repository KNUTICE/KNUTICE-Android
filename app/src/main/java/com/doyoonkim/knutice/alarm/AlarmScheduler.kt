package com.doyoonkim.knutice.alarm

import android.app.PendingIntent
import com.doyoonkim.knutice.model.Bookmark


/**
 * Alarm feature (Part of the Bookmark)
 * Reference: https://medium.com/@tolgapirim25/send-notifications-at-a-specific-time-with-alarm-manager-on-android-13c7cc9d8e7a
 */
interface AlarmScheduler {
    fun createPendingIntent(target: Bookmark): PendingIntent

    fun schedule(target: Bookmark)

    fun cancel(target: Bookmark)
}