package de.kasoki.trierbustimetracker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigurationManager {
	
	private Activity parent;
	
	// settings_activity
	private boolean settingsLoaded = false;
	private boolean useAutoReload = false;
	private boolean useNotifications = false;
	
	public ConfigurationManager(Activity parent) {
		this.parent = parent;
	}
	
	public ArrayList<String> getFavorites() {
		ArrayList<String> favorites = new ArrayList<String>();

        SharedPreferences prefs = parent.getSharedPreferences(Identifier.APP_FAVORITE_FILE_IDENTIFIER, Context.MODE_PRIVATE);

        int length = prefs.getInt("favorites_length", 0);

        for(int i = 0; i < length; i++) {
            favorites.add(prefs.getString("favorites" + i, null));
        }

        return favorites;
	}

	public void saveFavorites(ArrayList<String> favorites) {
        SharedPreferences.Editor editor = parent.getSharedPreferences(Identifier.APP_FAVORITE_FILE_IDENTIFIER, Context.MODE_PRIVATE).edit();

        int length = favorites.size();

        editor.putInt("favorites_length", length);

        for(int i = 0; i < length; i++) {
            editor.putString("favorites" + i, favorites.get(i));
        }

        editor.commit();
    }

	/** Removes all configuration files */
	public void clear() {
		String[] configFileNames = {
			Identifier.APP_FAVORITE_FILE_IDENTIFIER,
			Identifier.APP_SETTINGS_FILE_IDENTIFIER
		};

		for(String configFile : configFileNames) {
			parent.getApplicationContext().deleteFile(configFile);
		}
	}

	public void saveSettingsActivity(boolean useAutoReload, boolean useNotifications) {
		try {
			FileOutputStream fout = parent.openFileOutput(Identifier.APP_SETTINGS_FILE_IDENTIFIER,
					Context.MODE_PRIVATE);

			JSONObject object = new JSONObject();

			object.put(Identifier.APP_SETTINGS_USE_AUTO_RELOAD_IDENTIFIER, useAutoReload);
			object.put(Identifier.APP_SETTINGS_USE_NOTIFICATIONS_IDENTIFIER, useNotifications);

			fout.write(object.toString().getBytes());
			fout.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
            e.printStackTrace();
        }

    }
	
	public void loadSettingsActivity() {
		try {
			BufferedInputStream in = new BufferedInputStream(
					parent.openFileInput(Identifier.APP_SETTINGS_FILE_IDENTIFIER));

			String content = "";

			while (in.available() > 0) {
				content += (char) in.read();
			}

			in.close();

			JSONObject object = new JSONObject(content);

			this.useAutoReload = object.getBoolean(Identifier.APP_SETTINGS_USE_AUTO_RELOAD_IDENTIFIER);
			this.useNotifications = object.getBoolean(Identifier.APP_SETTINGS_USE_NOTIFICATIONS_IDENTIFIER);

			this.settingsLoaded = true;
		} catch (FileNotFoundException e) {
			// Don't do anything, file will be created in onStop
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
            e.printStackTrace();
        }
    }
	
	public boolean useAutoReload() throws Exception {
		if(this.settingsLoaded) {
			return this.useAutoReload;
		} else {
			throw new Exception("You need to use :ConfigurationManager.loadSettingsActivity");
		}
	}
	
	public boolean useNotifications() throws Exception {
		if(this.settingsLoaded) {
			return this.useNotifications;
		} else {
			throw new Exception("You need to use :ConfigurationManager.loadSettingsActivity");
		}
	}
}
