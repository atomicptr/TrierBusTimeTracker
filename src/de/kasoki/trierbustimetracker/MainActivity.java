package de.kasoki.trierbustimetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lazydroid.autoupdateapk.AutoUpdateApk;
import de.kasoki.swtrealtime.BusStop;
import de.kasoki.trierbustimetracker.adapter.FavoriteListAdapter;
import de.kasoki.trierbustimetracker.utils.AppUpdateObserver;
import de.kasoki.trierbustimetracker.utils.ConfigurationManager;
import de.kasoki.trierbustimetracker.utils.Helper;
import de.kasoki.trierbustimetracker.utils.Identifier;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends SherlockActivity {

	private ArrayAdapter<String> spinnerAdapter;
	private Spinner busStopSpinner;

	private FavoriteListAdapter listAdapter;
	private ListView listView;

	private ArrayList<String> busStopList;
	private ArrayList<String> favorites;

	private ConfigurationManager config;

    private AutoUpdateApk updater;
    private AppUpdateObserver appUpdateObserver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.d("TBBT", Helper.getVersion(this));

        // run updater
        Log.d("TBBT", "-- Initialize AutoUpdateApk --");
        updater = new AutoUpdateApk(getApplicationContext());
        updater.setUpdateInterval(2 * AutoUpdateApk.HOURS);

        appUpdateObserver = new AppUpdateObserver(this);
        updater.addObserver(appUpdateObserver);

        if(new ConfigurationManager(this).useMobileConnectionForAppUpdates()) {
            updater.enableMobileUpdates();
        } else {
            updater.disableMobileUpdates();
        }
        

		config = new ConfigurationManager(this);

		busStopList = new ArrayList<String>();
		favorites = new ArrayList<String>();

		// create the BusStop list and an index for later usage
		for (BusStop bs : BusStop.values()) {
			busStopList.add(bs.getName());
		}

		// initialize stuff
		busStopSpinner = (Spinner) this.findViewById(R.id.busStopSpinner);
		listView = (ListView) this.findViewById(R.id.favoritesListView);

		spinnerAdapter = new ArrayAdapter<String>(this,
				R.layout.list_item_busstop_spinner, R.id.spinner_busstop_name, busStopList);

		listAdapter = new FavoriteListAdapter(favorites, this);

		// set adapters
		busStopSpinner.setAdapter(spinnerAdapter);
		listView.setAdapter(listAdapter);

		// add OnItemClickListener to listView
		// TODO: Refactor this statement
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
                String busstopName = (String) parent.getItemAtPosition(position);

                startBusTimeActivity(busstopName);
			}

		});

		this.registerForContextMenu(listView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		// settings menu clicked
		case R.id.action_settings:
			intent = new Intent(this, SettingsActivity.class);
			this.startActivityForResult(intent,
					Identifier.SETTINGS_REQUEST_CODE);
			return true;

        case R.id.action_invite:
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.invite_text));
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.invite_text_title)));

            return true;

		// about menu clicked
		case R.id.action_about_tbbt:
			intent = new Intent(this, AboutActivity.class);
			this.startActivity(intent);
			return true;

        // check updates menu clicked
        case R.id.action_check_update:
            appUpdateObserver.showStatusOnce();
            updater.checkUpdatesManually();
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
		BusStop busStop = BusStop.getBusStopByName((String) busStopSpinner.getSelectedItem());

		startBusTimeActivity(busStop);
	}

    private void startBusTimeActivity(String busStopName) {
        startBusTimeActivity(BusStop.getBusStopByName(busStopName));
    }

	private void startBusTimeActivity(BusStop busStop) {
		if (Helper.isNetworkAvailable(this)) {
			Intent intent = new Intent(this, BusTimeActivity.class);

			intent.putExtra("BUS_TIME_CODE", busStop.getStopCode());

			this.startActivity(intent);
		} else {
			Log.d("NETWORK", "NO NETWORK CONNECTION");

			String noNetworkConnectionText = getResources().getString(
					R.string.no_network_connection_text);

			Toast toast = Toast.makeText(this.getApplicationContext(),
					noNetworkConnectionText, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		if (view.getId() == R.id.favoritesListView) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			
			menu.setHeaderTitle(favorites.get(info.position));

			String[] menuItems = getResources().getStringArray(
					R.array.favorites_menu);

			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		
		String selectedItem = favorites.get(info.position);

		switch(item.getItemId()) {
		// select
		case 0:
			contextMenuSelect(selectedItem);
			break;
        // add shortcut
        case 1:
            contextAddShortcutToHomescreen(selectedItem);
            break;
		// delete
		case 2:
			contextDeleteEntry(info.position);
			break;
		default:
			Log.d("ERROR", "Unknown Context menu option: " + item.getItemId());
			break;
		}
		
		
		
		return true;
	}
	
	private void contextMenuSelect(String busstopName) {
		startBusTimeActivity(busstopName);
	}

    private void contextAddShortcutToHomescreen(String itemName) {
        String code = BusStop.getBusStopByName(itemName).getStopCode();

        Intent shortcutIntent = new Intent(this, BusTimeActivity.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        shortcutIntent.putExtra("BUS_TIME_CODE", code);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, itemName);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        this.sendBroadcast(addIntent);

        if(Helper.getCurrentAPILevel() >= 11) {
            Toast.makeText(this, getString(R.string.shortcut_created, itemName), Toast.LENGTH_LONG).show();
        }
    }

	private void contextDeleteEntry(int position) {
		String item = favorites.get(position);
		
		favorites.remove(position);
		
		listAdapter.notifyDataSetChanged();
		
		String message = getResources().getString(R.string.favorite_item_deleted, item);
		
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle state) {
		state.putStringArrayList(
				Identifier.APP_PREFERENCES_FAVORITE_IDENTIFIER, this.favorites);
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		this.favorites = state
				.getStringArrayList(Identifier.APP_PREFERENCES_FAVORITE_IDENTIFIER);
		this.listAdapter.notifyDataSetChanged();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			// Got request code from settings
			if (requestCode == Identifier.SETTINGS_REQUEST_CODE) {
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
	@Override
	protected void onStart() {
		ArrayList<String> favorites = config.getFavorites();

		if (favorites != null) {

			this.favorites.clear();

			for (String f : favorites) {
				this.favorites.add(f);
			}
		}

		listAdapter.notifyDataSetChanged();

		super.onStart();
	}

	// This method saves the favorites list to a file
	@Override
	protected void onStop() {
		config.saveFavorites(favorites);

		super.onStop();
	}

	// delete ALL the settings!
	public void deleteAllSettings() {
		config.clear();
	}

    public void showNoUpdatesToast() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.no_update_text), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
