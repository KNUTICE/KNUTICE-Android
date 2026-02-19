package com.doyoonkim.notification.local

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import com.doyoonkim.common.di.ApplicationContext
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.model.BookmarkVO
import javax.inject.Inject

/**
 * Local Push Notification (Bookmark Reminder)
 * Reference: https://medium.com/@tolgapirim25/send-notifications-at-a-specific-time-with-alarm-manager-on-android-13c7cc9d8e7a
 */
interface AlarmScheduler {
    fun createPendingIntent(target: BookmarkVO, nav: BookmarkInfo): PendingIntent

    fun schedule(target: BookmarkVO, nav: BookmarkInfo)

    fun cancel(target: BookmarkVO, nav: BookmarkInfo)

    fun canScheduleExactAlarms(): Boolean
}

class NotificationAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : AlarmScheduler {
    private val TAG = "NotificationAlarmScheduler"
    // AlarmManager Instance
    // Context: ApplicationContext

    override fun createPendingIntent(target: BookmarkVO, nav: BookmarkInfo): PendingIntent {
        val uri = "knutice://bookmark?id=${nav.noticeId}&title=${Uri.encode(nav.noticeTitle)}&info=${Uri.encode(nav.noticeInfo)}"
        val intent = Intent(context, AlarmReceiver::class.java)
            .apply {
                putExtra("content", target.bookmarkNote)
                putExtra("uri_string", uri)
            }

        return PendingIntent.getBroadcast(
            context,
            target.targetNoticeNttId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // Permission Declared and granted at the APP level entry point.
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun schedule(target: BookmarkVO, nav: BookmarkInfo) {
        // Android Version Check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.d(TAG, "Schedule Exact Alarms is not possible.")
                return
            }
        }

        Log.d(TAG, "Alarm is being scheduled.")
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            target.reminderSchedule,
            createPendingIntent(target, nav)
        ).also { Log.d(TAG, "Alarm Scheduled.") }
    }

    override fun cancel(target: BookmarkVO, nav: BookmarkInfo) {
        alarmManager.cancel(
            createPendingIntent(target, nav)
        ).also { Log.d("NotificationAlarmScheduler", "Cancellation Completed") }
    }

    override fun canScheduleExactAlarms(): Boolean {
        return alarmManager.canScheduleExactAlarms()
    }

}