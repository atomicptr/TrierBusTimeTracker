package de.kasoki.trierbustimetracker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Helper {
	private Helper() {
	}
	
	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
	
	public static int getCurrentAPILevel() {
		return android.os.Build.VERSION.SDK_INT;
	}
	
	public static String getVersion(Activity activity) {
		try {
			return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
