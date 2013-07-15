package de.kasoki.trierbustimetracker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import de.kasoki.swtrealtime.BusStop;
import de.kasoki.trierbustimetracker.adapter.BusTimeAdapter;
import de.kasoki.trierbustimetracker.tasks.ReloadTask;
import de.kasoki.trierbustimetracker.utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BusTimeActivity extends Activity {

	private volatile ArrayList<HashMap<String, String>> listViewContent;

	private volatile ListView busStopListView;
	private volatile BusTimeAdapter listAdapter;

	private String busTimeCode;

	private volatile boolean reloadActive = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bustimes);

		if(Helper.getCurrentAPILevel() >= 11) {
			this.addHomeAsUpButtonToActionBar();
		}
		
		// get selected bus code
		Intent intent = getIntent();
		busTimeCode = intent.getStringExtra("BUS_TIME_CODE");

		// init stuff
		busStopListView = (ListView) this.findViewById(R.id.busStopListView);
		listViewContent = new ArrayList<HashMap<String, String>>();

		listAdapter = new BusTimeAdapter(listViewContent, this);

		busStopListView.setAdapter(listAdapter);

		if(Helper.getCurrentAPILevel() >= 9) {
			this.setThreadPolicyToAllowAll();
		}

		// set title to bus stop
		this.setTitle(BusStop.getBusStopByStopCode(busTimeCode).getName());

		// get information
		reload();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void addHomeAsUpButtonToActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void setThreadPolicyToAllowAll() {
		// set thread policy to permit all
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bustimes_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// reload action pressed
		case R.id.action_reload:
			reload();
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// reload the bus times and set them to the busStopListView
	private synchronized void reload() {
		final String busTimeCode = this.busTimeCode;
		final BusTimeActivity activity = this;

		if (!this.reloadActive) {
			if (Helper.isNetworkAvailable(activity)) {
				new ReloadTask(activity, busTimeCode).execute(0);
			} else {
				// No connection
				Log.d("NETWORK", "NO NETWORK CONNECTION");

				String noNetworkConnectionText = activity.getResources()
						.getString(R.string.no_network_connection_text);

				Toast toast = Toast.makeText(activity.getApplicationContext(),
						noNetworkConnectionText, Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	public void setReloadActive(boolean bool) {
		this.reloadActive = bool;
	}

	public void notifyListViewDataSetChanged() {
		listAdapter.notifyDataSetChanged();
	}

	public void setListViewContent(List<HashMap<String, String>> content) {
		listViewContent.clear();

		for (HashMap<String, String> data : content) {
			listViewContent.add(data);
		}
	}

	public void onReloadTaskFinished(final List<HashMap<String, String>> content) {
		final BusTimeActivity activity = this;

		// run code in the main thread
		Handler mainHandler = new Handler(activity.getApplicationContext()
				.getMainLooper());

		Runnable r = new Runnable() {
			public void run() {
				activity.setListViewContent(content);

				activity.notifyListViewDataSetChanged();

			}
		};

		mainHandler.post(r);
	}

    public void onReloadTaskFailed() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(listViewContent.isEmpty()) {
                    HashMap<String, String> data = new HashMap<String, String>();

                    data.put("NUMBER", "");
                    data.put("DESTINATION", getString(R.string.connection_failed));
                    data.put("TIME", "");

                    listViewContent.add(data);

                    notifyListViewDataSetChanged();
                }
            }
        });
    }

    public void toastServerError(final int responseCode) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BusTimeActivity.this, getString(R.string.server_response_error, responseCode), Toast.LENGTH_SHORT).show();
            }
        });
    }

	@Override
	public void onResume() {
		reload();

		super.onResume();
	}

}
