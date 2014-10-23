package de.kasoki.trierbustimetracker.utils

import android.support.v7.app.ActionBarActivity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

object ActionBarHelper {
    def enableHomeAsUp(activity:ActionBarActivity) {
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}