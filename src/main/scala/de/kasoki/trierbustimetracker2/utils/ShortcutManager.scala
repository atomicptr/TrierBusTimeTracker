package de.kasoki.trierbustimetracker2.utils

import android.app.Activity
import android.content.Intent

import de.kasoki.swtrealtime.BusStop

import de.kasoki.trierbustimetracker2.BusTimeActivity
import de.kasoki.trierbustimetracker2.R

object ShortcutManager {
    def create(activity:Activity, busStop:BusStop.BusStopType) {
        val shortcutIntent = new Intent(activity, classOf[BusTimeActivity])

        shortcutIntent.setAction(Intent.ACTION_MAIN)

        shortcutIntent.putExtra("BUS_TIME_CODE", busStop.code)

        val addIntent = new Intent()
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, busStop.name)
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(activity, R.drawable.ic_launcher))

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT")

        activity.sendBroadcast(addIntent)
    }
}
