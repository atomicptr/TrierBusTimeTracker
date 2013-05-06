package de.kasoki.trierbustimetracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import de.kasoki.swtrealtime.BusStop;
import de.kasoki.swtrealtime.BusTime;

public class BusTimeActivity extends Activity {

	private List<BusTime> busTimesList;
	private List<Map<String, String>> listViewContent;
	
	private ListView busStopListView;
	private SimpleAdapter listAdapter;
	
	private String busTimeCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bustimes);

		// get selected bus code
		Intent intent = getIntent();
		busTimeCode = intent.getStringExtra("BUS_TIME_CODE");
		
		// init stuff
		busStopListView = (ListView) this.findViewById(R.id.busStopListView);
		listViewContent = new ArrayList<Map<String, String>>();
		
		listAdapter = new SimpleAdapter(this, listViewContent,
                android.R.layout.simple_list_item_2, 
                new String[] {"FIRST_LINE", "SECOND_LINE" }, 
                new int[] {android.R.id.text1, android.R.id.text2 });
		
		busStopListView.setAdapter(listAdapter);

		// set thread policy to permit all
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// set title to bus stop
		this.setTitle(BusStop.getBusStopByStopCode(busTimeCode).getName());
		
		// get information
		reload();
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// reload the bus times and set them to the busStopListView
	private synchronized void reload() {
		final String busTimeCode = this.busTimeCode;
		final Activity activity = this;
		
		// maybe is should refactor the code below
		Handler handler = new Handler();

		final Runnable r = new Runnable() {
			@Override
			public void run() {
				if (isNetworkAvailable()) {
					// delete the content of the old list, we don't them anymore ;)
					listViewContent.clear();

					// retrieve stuff from the SWT servers
					busTimesList = BusTime.fromStopCode(busTimeCode);

					Collections.sort(busTimesList);
					
					for (BusTime b : busTimesList) {
						Log.d("BUSTIME RECIEVED", b.toString());
						
						final Map<String, String> data = new HashMap<String, String>(2);
						
						String delay = "";
						
						if(b.getDelay() != 0) {
							String operand = b.getDelay() < 0 ? "-" : "+";
							delay = String.format(" %s %d%s", operand, b.getDelay(), "m");
						}
						
						String arrivalTimeText = getResources().getString(R.string.bustime_arrival_text);
						
				        data.put("FIRST_LINE", String.format("(%d) %s", b.getNumber(), b.getDestination()));
				        data.put("SECOND_LINE", String.format("%s: %s%s", arrivalTimeText, b.getArrivalTimeAsString(), delay));
				        
				        activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								listViewContent.add(data);
							}
				        	
				        });
				       
					}
					
					// when the list is empty show the user that there are no buses atm
					if(listViewContent.isEmpty()) {
						final Map<String, String> data = new HashMap<String, String>(2);
						data.put("FIRST_LINE", getResources().getString(R.string.bustime_no_bus));
						data.put("SECOND_LINE", "");
						
						listViewContent.add(data);
					}
					
					listAdapter.notifyDataSetChanged();
				} else {
					// No connection
					Log.d("NETWORK", "NO NETWORK CONNECTION");
					
					String noNetworkConnectionText = getResources().getString(R.string.no_network_connection_text);
					
					Toast toast = Toast.makeText(activity.getApplicationContext(), noNetworkConnectionText, Toast.LENGTH_SHORT);
					toast.show();
					
					activity.finish();
				}
			}
		};

		handler.post(r);
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
	
	@Override
	public void onResume() {
		reload();
		
		super.onResume();
	}

}
