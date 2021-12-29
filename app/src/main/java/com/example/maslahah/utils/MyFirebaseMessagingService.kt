package com.example.maslahah.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.maslahah.R
import com.example.maslahah.ui.NotificationActivityScreen

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        Log.d("dddddddd", "onMessageReceived11111111:  $remoteMessage")

        if (remoteMessage.data["title"] == null || remoteMessage.data["message"] == null)
            return
        Log.d("dddddddd", "onMessageReceived2:  $remoteMessage")

        createNotification(
            remoteMessage.data["title"]!!,
            remoteMessage.data["message"]!!,
        )

    }

    private fun createNotification(title: String, body: String) {
        val sb: Spannable = SpannableString(title)
        sb.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val intent = Intent(this, NotificationActivityScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = getString(R.string.app_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notfication",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "test channel"
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationManager.createNotificationChannel(channel)
        }
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_deal_icon)
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle(sb)
                    .addLine(body)
            ).setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)


        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notificationBuilder.build()
        )

    }


    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("TAG", "onNewToken: ")
    }

}