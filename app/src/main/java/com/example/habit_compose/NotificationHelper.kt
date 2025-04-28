package com.example.habit_compose

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

@SuppressLint("ScheduleExactAlarm")
fun scheduleHabitNotification(context: Context, habitName: String, reminderTime: String, daysSelected: String) {
    val parts = reminderTime.split(" ", ":")
    val hour = parts[0].toInt()
    val minute = parts[1].toInt()
    val amPm = parts[2]

    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, if (amPm == "PM" && hour != 12) hour + 12 else if (amPm == "AM" && hour == 12) 0 else hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

//    calendar.timeInMillis = System.currentTimeMillis() + 2 * 60 * 1000 // fire after 2 minutes

    // If the time has already passed today, schedule for tomorrow
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
        habitName.hashCode(), // unique id
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )

}
