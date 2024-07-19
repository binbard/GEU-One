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
import androidx.core.content.ContentProviderCompat.requireContext
import com.binbard.geu.one.helpers.FirebaseUtils
import com.binbard.geu.one.helpers.SharedPreferencesHelper
import com.binbard.geu.one.ui.erp.ErpCacheHelper
import com.binbard.geu.one.ui.erp.ErpViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NanoMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMsgService"
    private val sharedPreferencesHelper: SharedPreferencesHelper by lazy {
        SharedPreferencesHelper(this)
    }
    private val erpCacheHelper: ErpCacheHelper by lazy {
        ErpCacheHelper(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "FCM Message payload: ${remoteMessage.data}")

        if (remoteMessage.notification == null) return

        val channelId = remoteMessage.notification?.channelId
        val title = remoteMessage.notification?.title
        val icon = remoteMessage.notification?.icon
        val body = remoteMessage.notification?.body

        val extras = remoteMessage.data

        if (remoteMessage.data.isNotEmpty()) {
            sendNotification(this, body.toString(), title, channelId, icon, extras)
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "New token: $token")
        FirebaseUtils.subscribeTo("notes")
        FirebaseUtils.subscribeTo("all")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
        sharedPreferencesHelper.setFbToken(token)
    }

    @SuppressLint("DiscouragedApi")
    fun sendNotification(
        context: Context,
        messageBody: String,
        pTitle: String? = null,
        pChannelId: String? = null,
        pIcon: String? = null,
        extras: Map<String, String>? = null,
    ) {
        val channelId = pChannelId ?: "Default"
        val title = pTitle ?: "GEU One"
        val icon = pIcon ?: "ic_feeds"

        val intent = Intent(context, MainActivity::class.java)
        for((k,v) in extras ?: emptyMap()) {
            intent.putExtra(k, v)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            context,
            System.nanoTime().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(context.resources.getIdentifier(icon, "drawable", context.packageName))
            .setContentTitle(title)
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
        notificationManager.notify(System.nanoTime().toInt(), notificationBuilder.build())

    }

}