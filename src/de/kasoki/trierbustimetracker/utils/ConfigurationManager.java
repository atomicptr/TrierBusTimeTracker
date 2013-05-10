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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/** Removes all configuration files */
	public void clear() {
		parent.getApplicationContext().deleteFile(Identifier.APP_FAVORITE_FILE_IDENTIFIER);
		
	}
}
