package de.kasoki.trierbustimetracker.utils

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

object ActionBarHelper {
    val actionBarColor = "#0356b9"

    def colorActionBar(activity:Activity) {
        activity.getActionBar().setBackgroundDrawable(
            new ColorDrawable(Color.parseColor(ActionBarHelper.actionBarColor))
        )
    }

    def enableHomeAsUp(activity:Activity) {
        activity.getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}