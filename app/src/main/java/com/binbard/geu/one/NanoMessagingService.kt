package com.binbard.geu.one

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NanoMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMsgService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            sendNotification(this, remoteMessage.data["message"].toString())
        }
        if (remoteMessage.notification == null) return

        val channelId = remoteMessage.notification?.channelId
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        val icon = remoteMessage.notification?.icon
        val clickAction = remoteMessage.notification?.clickAction

        val extras = remoteMessage.data

        sendNotification(this, body.toString(), channelId, title, icon, clickAction, extras)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    @SuppressLint("DiscouragedApi")
    fun sendNotification(
        context: Context,
        messageBody: String,
        pChannelId: String? = null,
        pTitle: String? = null,
        pIcon: String? = null,
        pClickAction: String? = null,
        extras: Map<String, String>? = null,
    ) {

        val channelId = pChannelId ?: "Default"
        val title = pTitle ?: "GEU One"
        val icon = pIcon ?: "ic_feeds"
        val clickAction = pClickAction ?: "android.intent.action.FEED_POST"

        val cls = getClickActionCls(extras)
        val intent = Intent(context, cls)
        for((k,v) in extras ?: emptyMap()) {
            intent.putExtra(k, v)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setSmallIcon(context.resources.getIdentifier(icon, "drawable", context.packageName))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            channelId,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(0, notificationBuilder.build())

    }

    private fun getClickActionCls(extras: Map<String, String>?): Class<*> {
        val mainCls = MainActivity::class.java
        if(extras == null) return mainCls
        val className = extras["click_action"] ?: return mainCls
        val cls: Class<*>
        try {
            cls = Class.forName(className)
        } catch (e: ClassNotFoundException) {
            Log.e("ResolveClickAction", "Failed to resolve")
            return mainCls
        }
        return cls
    }

}