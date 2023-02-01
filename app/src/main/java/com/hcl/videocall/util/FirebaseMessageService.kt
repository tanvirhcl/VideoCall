package com.hcl.videocall.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hcl.videocall.R
import com.hcl.videocall.ui.VideoCallActivity


class FirebaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotification(message.data, message.notification)
    }

    private fun showNotification(data: Map<String,String>?, message: RemoteMessage.Notification?) {

        val intent = Intent(this, VideoCallActivity::class.java).apply {
            putExtra("roomId",data?.get("roomId"))
            putExtra("type",CallType.JOIN)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val sound =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, getString(R.string.channel_id))
            .setSmallIcon(R.drawable.bg_blur)
            .setContentTitle(message?.title)
            .setContentText(message?.body)
            .addAction(R.drawable.call, "Accept",pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setSound(sound)
            .setOngoing(true)
            .setAutoCancel(true)




        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(getString(R.string.channel_id), "web_app", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, builder.build())

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }
}