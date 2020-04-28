package com.example.scheduleme.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.provider.Settings
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import com.example.scheduleme.R
import com.example.scheduleme.ui.MainActivity


class AlarmReceiver : BroadcastReceiver() {
    companion object {
        @Nullable
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI)
        mediaPlayer?.start()
        val content = intent.getStringExtra("content")
        val i = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, i, 0)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel: NotificationChannel = NotificationChannel(
                "APPOINTMENT",
                "Appointment",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, "APPOINTMENT")
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("$content is now !")
                .setSmallIcon(R.drawable.ic_schedule)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        notificationManager.notify(1, notificationBuilder.build())

    }
}
