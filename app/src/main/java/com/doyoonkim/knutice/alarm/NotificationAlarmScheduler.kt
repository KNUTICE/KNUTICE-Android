package com.doyoonkim.knutice.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.doyoonkim.knutice.model.Bookmark
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {
    // AlarmManager Instance
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun createPendingIntent(target: Bookmark): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("note", target.note)
        }

        return PendingIntent.getBroadcast(
            context,
            target.bookmarkId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun schedule(target: Bookmark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.d("NotificationAlarmScheduler", "Unable")
                return
            }
        }
        Log.d("NotificationAlarmSchedule", "Would be scheduled")
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            target.reminderSchedule,
            createPendingIntent(target)
        )

        Log.d("NotificationAlarmSchedule", "Scheduled")
    }

    override fun cancel(target: Bookmark) {
        alarmManager.cancel(
            createPendingIntent(target)
        )
    }

    fun canScheduleExactAlarms(): Boolean {
        return alarmManager.canScheduleExactAlarms()
    }

}