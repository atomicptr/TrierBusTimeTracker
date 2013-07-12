package de.kasoki.trierbustimetracker.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.lazydroid.autoupdateapk.AutoUpdateApk;
import de.kasoki.trierbustimetracker.MainActivity;
import de.kasoki.trierbustimetracker.R;

import java.util.Observable;
import java.util.Observer;

public class AppUpdateObserver implements Observer {

    private MainActivity activity;
    private volatile boolean showStatus = false;

    public AppUpdateObserver(MainActivity activity) {
        this.activity = activity;
    }

    public void showStatusOnce() {
        this.showStatus = true;
    }

    @Override
    public void update(Observable observable, Object data) {
        String update = (String) data;

        if(update.equals(AutoUpdateApk.AUTOUPDATE_NO_UPDATE)) {
            Log.i("TBBT-AppUpdate", "no update");
            if(showStatus) {
                activity.showNoUpdatesToast();
                this.showStatus = false;
            }
        }
    }
}
