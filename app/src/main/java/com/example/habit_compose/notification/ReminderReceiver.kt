package com.example.habit_compose.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.habit_compose.R
import java.util.*

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitName = intent.getStringExtra("habitName") ?: "Your Habit"
        val daysSelected = intent.getStringExtra("daysSelected") ?: ""

        // 🔁 تحقق من اليوم الحالي
        val todayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 // Sunday = 0
        if (daysSelected.isNotEmpty() && todayIndex.toString() !in daysSelected.split(",")) {
            return
        }

        // 🔊 إعداد الصوت
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channelId = "habit_channel"

        // 📢 إنشاء قناة التنبيهات (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Habit Reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                setSound(soundUri, attributes)
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // 🧱 بناء الإشعار
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Habit Reminder")
            .setContentText("Don't forget: $habitName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(soundUri)

        // ✅ تحقق من صلاحية POST_NOTIFICATIONS فقط على Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // 🔔 إرسال الإشعار
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
