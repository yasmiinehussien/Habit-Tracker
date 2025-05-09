package com.example.habit_compose.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import java.util.*

fun scheduleHabitNotification(context: Context, habitName: String, reminderTime: String, daysSelected: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // ✅ تحقق من الإذن إذا كان Android 12 أو أعلى
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = android.net.Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Toast.makeText(
                context,
                "من فضلك فعّل التنبيهات الدقيقة للتطبيق من الإعدادات.",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "لا يمكن فتح إعدادات التنبيهات الدقيقة تلقائيًا. افتحها يدويًا.",
                Toast.LENGTH_LONG
            ).show()
        }
        return
    }

    // ✅ تحويل الوقت (مثلاً: 07:30 AM)
    val parts = reminderTime.split(" ", ":")
    val hour = parts[0].toInt()
    val minute = parts[1].toInt()
    val amPm = parts[2]

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, when {
            amPm == "PM" && hour != 12 -> hour + 12
            amPm == "AM" && hour == 12 -> 0
            else -> hour
        })
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // ✅ لو الوقت اللي فات، خليه لليوم اللي بعده
    if (calendar.timeInMillis < System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("title", "Reminder")
        putExtra("description", "It's time for your habit: $habitName")
        putExtra("daysSelected", daysSelected)
        putExtra("habitName", habitName)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        habitName.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}
