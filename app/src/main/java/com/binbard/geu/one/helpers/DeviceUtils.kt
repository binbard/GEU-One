package com.binbard.geu.one.helpers

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object DeviceUtils {
    fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        if(resultCode != ConnectionResult.SUCCESS){
            googleApiAvailability.makeGooglePlayServicesAvailable(context as android.app.Activity)
        }
        return resultCode == ConnectionResult.SUCCESS
    }
}