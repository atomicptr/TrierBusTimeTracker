package de.kasoki.trierbustimetracker.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.lazydroid.autoupdateapk.AutoUpdateApk;
import de.kasoki.trierbustimetracker.R;

import java.util.Observable;
import java.util.Observer;

public class AppUpdateObserver implements Observer {

    private Context context;
    private boolean showStatus = false;

    public AppUpdateObserver(Context context) {
        this.context = context;
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
                Toast.makeText(context, context.getResources().getString(R.string.no_update_text), Toast.LENGTH_SHORT).show();
                this.showStatus = false;
            }
        }
    }
}
