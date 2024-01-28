package com.binbard.geu.one.helpers

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object FirebaseUtils {
    fun subscribeTo(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Subscribed to $topic"
                if (!task.isSuccessful) {
                    msg = "Failed to subscribe to $topic"
                }
                Log.d("FirebaseUtils", msg)
            }
    }
    fun unsubscribeFrom(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Unsubscribed from $topic"
                if (!task.isSuccessful) {
                    msg = "Failed to unsubscribe from $topic"
                }
                Log.d("FirebaseUtils", msg)
            }
    }
    fun subscribeToAll() {
        subscribeTo("all")
    }
}