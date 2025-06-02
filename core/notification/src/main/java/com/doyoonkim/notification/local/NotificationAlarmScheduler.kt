package com.doyoonkim.notification.local

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import com.doyoonkim.model.BookmarkVO

/**
 * Local Push Notification (Bookmark Reminder)
 * Reference: https://medium.com/@tolgapirim25/send-notifications-at-a-specific-time-with-alarm-manager-on-android-13c7cc9d8e7a
 */
interface AlarmScheduler {
    fun createPendingIntent(target: BookmarkVO): PendingIntent

    fun schedule(target: BookmarkVO)

    fun cancel(target: BookmarkVO)
}

class NotificationAlarmScheduler(
    private val context: Context
) : AlarmScheduler {
    private val TAG = "NotificationAlarmScheduler"
    // AlarmManager Instance
    // Context: ApplicationContext
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun createPendingIntent(target: BookmarkVO): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)
            .apply {
                putExtra("content", target.bookmarkNote)
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
    override fun schedule(target: BookmarkVO) {
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
            createPendingIntent(target)
        ).also { Log.d(TAG, "Alarm Scheduled.") }
    }

    override fun cancel(target: BookmarkVO) {
        alarmManager.cancel(
            createPendingIntent(target)
        )
    }

}