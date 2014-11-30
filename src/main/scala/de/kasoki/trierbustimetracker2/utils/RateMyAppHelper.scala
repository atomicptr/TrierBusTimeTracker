package de.kasoki.trierbustimetracker2.utils

import org.scaloid.common._
import android.app._
import android.content._
import android.content.Context
import android.net.Uri

import de.kasoki.trierbustimetracker2.R

object RateMyAppHelper {
    def openAfterNCalls(activity:Activity, num:Int) {
        val prefs = activity.getSharedPreferences(Identifier.APP_RATE_FILE_IDENTIFIER, Context.MODE_PRIVATE)

        val count = prefs.getInt("count", 0)

        if(count < num) {
            val editor = prefs.edit()

            editor.putInt("count", count + 1)

            editor.commit()
        } else {
            if(!done(activity)) {
                open(activity)
            }
        }
    }

    private def open(activity:Activity) {
        val title = activity.getResources().getString(R.string.rate_title)
        val nowbtn = activity.getResources().getString(R.string.rate_now_button)
        val neverbtn = activity.getResources().getString(R.string.rate_never_button)

        new AlertDialog.Builder(activity)
            .setMessage(title)
            .setPositiveButton(nowbtn, new DialogInterface.OnClickListener {
                def onClick(dialog:DialogInterface, which:Int):Unit = {
                    openRateIntentAndMarkAsDone(activity)
                }
            })
            .setNegativeButton(neverbtn, new DialogInterface.OnClickListener {
                def onClick(dialog:DialogInterface, which:Int):Unit = {
                    dontShowAgain(activity)
                }
            }).show()
    }

    def openRateIntent(activity:Activity) {
        val context = activity
        val uri = Uri.parse("market://details?id=" + context.getPackageName())

        val marketIntent = new Intent(Intent.ACTION_VIEW, uri)

        try {
            activity.startActivity(marketIntent)
        } catch {
            case e:ActivityNotFoundException => {
                val altUri = Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())

                activity.startActivity(new Intent(Intent.ACTION_VIEW, altUri))
            }
        }
    }

    private def openRateIntentAndMarkAsDone(activity:Activity) {
        openRateIntent(activity)

        dontShowAgain(activity)
    }

    private def dontShowAgain(activity:Activity) {
        val prefs = activity.getSharedPreferences(Identifier.APP_RATE_FILE_IDENTIFIER, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putBoolean("done", true)

        editor.commit()
    }

    private def done(activity:Activity):Boolean = {
        val prefs = activity.getSharedPreferences(Identifier.APP_RATE_FILE_IDENTIFIER, Context.MODE_PRIVATE)

        return prefs.getBoolean("done", false)
    }
}
