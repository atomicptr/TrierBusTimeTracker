package de.kasoki.trierbustimetracker2.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

import de.kasoki.trierbustimetracker2.R

object AndroidHelper {
    def currentApiLevel:Int = android.os.Build.VERSION.SDK_INT

    def version(activity:Activity):String = {
        return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName
    }

    def isNetworkAvailable(activity:Activity):Boolean = {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE).asInstanceOf[ConnectivityManager]
        val networkInfo = cm.getActiveNetworkInfo();

        // if there is no network available networkInfo is null
        if(networkInfo != null && networkInfo.isConnected()) {
            return true
        }

        return false
    }

    def slideIn(activity:Activity) {
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }

    def slideBack(activity:Activity) {
        activity.overridePendingTransition(R.anim.slideback_out, R.anim.slideback_in)
    }
}
