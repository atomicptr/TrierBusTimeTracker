// Copyright (c) 2014 Christopher Kaster
//
// This file is part of Trier Bus Time Tracker <https://github.com/kasoki/TrierBusTimeTracker>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
package de.kasoki.trierbustimetracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import de.kasoki.swtrealtime.BusStop;
import de.kasoki.trierbustimetracker.adapter.FavoriteListAdapter;
import de.kasoki.trierbustimetracker.utils.ConfigurationManager;
import de.kasoki.trierbustimetracker.utils.Helper;
import de.kasoki.trierbustimetracker.utils.Identifier;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {

    private ArrayAdapter<String> spinnerAdapter;
    private Spinner busStopSpinner;

    private FavoriteListAdapter listAdapter;
    private ListView listView;

    private ArrayList<String> busStopList;
    private ArrayList<String> favorites;

    private ConfigurationManager config;

    public static final String ACTIONBAR_COLOR = "#0356b9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupTheme();

        Log.d("TBBT", Helper.getVersion(this));

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

    private void setupTheme() {
        this.getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor(MainActivity.ACTIONBAR_COLOR)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        if(resultCode == RESULT_OK) {

            // Got request code from settings
            if(requestCode == Identifier.SETTINGS_REQUEST_CODE) {
                boolean deleteSettings = data.getExtras().getBoolean(
                        "DELETE_SETTINGS", false);

                if(deleteSettings) {
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

        if(favorites != null) {

            this.favorites.clear();

            for(String f : favorites) {
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
