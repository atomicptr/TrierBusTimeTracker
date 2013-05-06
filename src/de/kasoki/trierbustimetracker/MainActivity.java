package de.kasoki.trierbustimetracker;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import de.kasoki.swtrealtime.BusStop;
import de.kasoki.trierbustimetracker.utils.Helper;

public class MainActivity extends Activity {

	private ArrayAdapter<String> spinnerAdapter;
	private Spinner busStopSpinner;

	private ArrayAdapter<String> listAdapter;
	private ListView listView;

	private BusStop[] busStopIndex;
	private ArrayList<String> busStopList;
	private ArrayList<String> favorites;

	private static final String APP_IDENTIFIER = "de.kasoki.TRIER_BUS_TIME_TRACKER";
	private static final String APP_PREFERENCES_FAVORITE_IDENTIFIER = "de.kasoki.trierbustimetracker.FAVORITES";
	private static final int SETTINGS_REQUEST_CODE = 0x01;

	private static final int MAJOR_VERSION = 0;
	private static final int MINOR_VERSION = 0;
	private static final int PATCH_VERSION = 12;
	private static final boolean IS_DEVELOPMENT_BUILD = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.d("TBBT", getVersion());

		busStopList = new ArrayList<String>();
		favorites = new ArrayList<String>();

		busStopIndex = new BusStop[BusStop.values().length];

		// create the BusStop list and an index for later usage
		for (BusStop bs : BusStop.values()) {
			busStopList.add(bs.getName());

			busStopIndex[busStopList.indexOf(bs.getName())] = bs;
		}

		// initialize stuff
		busStopSpinner = (Spinner) this.findViewById(R.id.busStopSpinner);
		listView = (ListView) this.findViewById(R.id.favoritesListView);

		spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, busStopList);

		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, favorites);

		// set adapters
		busStopSpinner.setAdapter(spinnerAdapter);
		listView.setAdapter(listAdapter);

		// add OnItemClickListener to listView
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String item = (String) parent.getItemAtPosition(position);

				busStopSpinner.setSelection(busStopList.indexOf(item));

				onActionSelectedButtonClicked(view);
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent;

		switch (item.getItemId()) {
		// settings menu clicked
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			this.startActivityForResult(intent, SETTINGS_REQUEST_CODE);
			return true;
			// about menu clicked
		case R.id.action_about_tbbt:
			intent = new Intent(this, AboutActivity.class);
			this.startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onAddToFavoritesButtonClicked(View view) {
		final String item = (String) busStopSpinner.getSelectedItem();

		if (!favorites.contains(item)) {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					favorites.add(item);
					Collections.sort(favorites);
					listAdapter.notifyDataSetChanged();
				}
			});
		} else {
			String itemAlreadyOnFavoritesListText = getResources().getString(
					R.string.item_already_on_favorites_list_text);

			Toast toast = Toast.makeText(this.getApplicationContext(),
					itemAlreadyOnFavoritesListText, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public void onActionSelectedButtonClicked(View view) {
		BusStop busStop = busStopIndex[busStopSpinner.getSelectedItemPosition()];

		startBusTimeActivity(busStop);
	}

	private void startBusTimeActivity(BusStop busStop) {
		if(Helper.isNetworkAvailable(this)) {
			Intent intent = new Intent(this, BusTimeActivity.class);
	
			intent.putExtra("BUS_TIME_CODE", busStop.getStopCode());
	
			this.startActivity(intent);
		} else {
			Log.d("NETWORK", "NO NETWORK CONNECTION");
			
			String noNetworkConnectionText = getResources().getString(R.string.no_network_connection_text);
			
			Toast toast = Toast.makeText(this.getApplicationContext(), noNetworkConnectionText, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle state) {
		state.putStringArrayList(
				MainActivity.APP_PREFERENCES_FAVORITE_IDENTIFIER,
				this.favorites);
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		this.favorites = state
				.getStringArrayList(MainActivity.APP_PREFERENCES_FAVORITE_IDENTIFIER);
		this.listAdapter.notifyDataSetChanged();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			// Got request code from settings
			if (requestCode == MainActivity.SETTINGS_REQUEST_CODE) {
				boolean deleteSettings = data.getExtras().getBoolean(
						"DELETE_SETTINGS", false);

				if (deleteSettings) {
					this.deleteAllSettings();

					favorites.clear();
					listAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	// This method loads the favorites list on start
	protected void onStart() {
		try {
			BufferedInputStream in = new BufferedInputStream(
					openFileInput(MainActivity.APP_IDENTIFIER));

			String content = "";

			while (in.available() > 0) {
				content += (char) in.read();
			}

			in.close();

			JSONObject object = new JSONObject(content);

			JSONArray array = (JSONArray) object
					.get(MainActivity.APP_PREFERENCES_FAVORITE_IDENTIFIER);

			favorites.clear();

			for (int i = 0; i < array.length(); i++) {
				String item = array.getString(i);

				favorites.add(item);
			}

			listAdapter.notifyDataSetChanged();
		} catch (FileNotFoundException e) {
			// Don't do anything, file will be created in onStop
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.onStart();
	}

	// This method saves the favorites list to a file
	protected void onStop() {
		try {
			FileOutputStream fout = openFileOutput(MainActivity.APP_IDENTIFIER,
					Context.MODE_PRIVATE);

			JSONObject object = new JSONObject();
			JSONArray array = new JSONArray(favorites);

			object.put(MainActivity.APP_PREFERENCES_FAVORITE_IDENTIFIER, array);

			fout.write(object.toString().getBytes());
			fout.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.onStop();
	}

	public static String getVersion() {
		String developmentBuild = "";

		if (IS_DEVELOPMENT_BUILD) {
			developmentBuild = "-DEV";
		}

		return String.format("%d.%d.%d%s", MAJOR_VERSION, MINOR_VERSION,
				PATCH_VERSION, developmentBuild);
	}

	// delete ALL the settings!
	public void deleteAllSettings() {
		getApplicationContext().deleteFile(MainActivity.APP_IDENTIFIER);
	}
}
