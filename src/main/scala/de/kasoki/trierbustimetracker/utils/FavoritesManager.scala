package de.kasoki.trierbustimetracker.utils

import scala.collection.mutable.Buffer

import android.app.Activity
import android.content.Context._

import de.kasoki.swtrealtime.BusStop

object FavoritesManager {
    def save(activity:Activity, list:Buffer[String]) {
        val prefs = activity.getSharedPreferences(Identifier.APP_FAVORITE_FILE_IDENTIFIER, MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putInt("favorites_length", list.length)

        for(i <- 0 until list.length) {
            editor.putString("favorites" + i, list(i))
        }

        editor.commit()
    }

    def load(activity:Activity):Buffer[String] = {
        val prefs = activity.getSharedPreferences(Identifier.APP_FAVORITE_FILE_IDENTIFIER, MODE_PRIVATE)

        var items = Buffer[String]()

        val length = prefs.getInt("favorites_length", 0)

        for(i <- 0 until length) {
            items += prefs.getString("favorites" + i, null)
        }

        return items.sorted
    }

    def add(activity:Activity, busStop:BusStop.BusStopType) {
        val curr = FavoritesManager.load(activity)

        curr += busStop.name

        FavoritesManager.save(activity, curr)
    }

    def remove(activity:Activity, busStop:BusStop.BusStopType) {
        val curr = FavoritesManager.load(activity)

        curr -= busStop.name

        FavoritesManager.save(activity, curr)
    }

    def has(activity:Activity, busStop:BusStop.BusStopType):Boolean = {
        val curr = FavoritesManager.load(activity)

        return curr.contains(busStop.name)
    }
}
