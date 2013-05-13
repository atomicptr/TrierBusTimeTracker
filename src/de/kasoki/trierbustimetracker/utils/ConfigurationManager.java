package de.kasoki.trierbustimetracker.utils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

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
		try {
			BufferedInputStream in = new BufferedInputStream(
					parent.openFileInput(Identifier.APP_FAVORITE_FILE_IDENTIFIER));

			String content = "";

			while (in.available() > 0) {
				content += (char) in.read();
			}

			in.close();

			JSONObject object = new JSONObject(content);

			JSONArray array = (JSONArray) object
					.get(Identifier.APP_FAVORITE_FILE_IDENTIFIER);

			ArrayList<String> favorites = new ArrayList<String>();

			for (int i = 0; i < array.length(); i++) {
				String item = array.getString(i);

				favorites.add(item);
			}

			return favorites;
		} catch (FileNotFoundException e) {
			// Don't do anything, file will be created in onStop
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void saveFavorites(ArrayList<String> favorites) {
		try {
			FileOutputStream fout = parent.openFileOutput(Identifier.APP_FAVORITE_FILE_IDENTIFIER,
					Context.MODE_PRIVATE);

			JSONObject object = new JSONObject();
			JSONArray array = new JSONArray(favorites);

			object.put(Identifier.APP_FAVORITE_FILE_IDENTIFIER, array);

			fout.write(object.toString().getBytes());
			fout.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/** Removes all configuration files */
	public void clear() {
		parent.getApplicationContext().deleteFile(Identifier.APP_FAVORITE_FILE_IDENTIFIER);
		
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
