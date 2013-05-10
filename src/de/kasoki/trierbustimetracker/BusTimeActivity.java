package de.kasoki.trierbustimetracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import de.kasoki.swtrealtime.BusStop;
import de.kasoki.trierbustimetracker.tasks.ReloadTask;

public class BusTimeActivity extends Activity {

	private volatile List<Map<String, String>> listViewContent;

	private volatile ListView busStopListView;
	private volatile SimpleAdapter listAdapter;

	private String busTimeCode;

	private volatile boolean reloadActive = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bustimes);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		// get selected bus code
		Intent intent = getIntent();
		busTimeCode = intent.getStringExtra("BUS_TIME_CODE");

		// init stuff
		busStopListView = (ListView) this.findViewById(R.id.busStopListView);
		listViewContent = new ArrayList<Map<String, String>>();

		listAdapter = new SimpleAdapter(this, listViewContent,
				android.R.layout.simple_list_item_2, new String[] {
						"FIRST_LINE", "SECOND_LINE" }, new int[] {
						android.R.id.text1, android.R.id.text2 });

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
			new ReloadTask(activity, busTimeCode).execute(0);
		}
	}

	public void setReloadActive(boolean bool) {
		this.reloadActive = bool;
	}
	
	public void notifyListViewDataSetChanged() {
		listAdapter.notifyDataSetChanged();
	}
	
	public void setListViewContent(List<Map<String, String>> content) {
		this.listViewContent = content;
	}
	
	public void resetListAdapter() {
		listAdapter = new SimpleAdapter(this, listViewContent,
				android.R.layout.simple_list_item_2, new String[] {
						"FIRST_LINE", "SECOND_LINE" }, new int[] {
						android.R.id.text1, android.R.id.text2 });
		
		this.busStopListView.setAdapter(listAdapter);
	}

	public void onReloadTaskFinished(final List<Map<String, String>> content) {
		final BusTimeActivity activity = this;
		
		Handler mainHandler = new Handler(activity.getApplicationContext().getMainLooper());
		
		Runnable r = new Runnable() {
		    public void run() {
		    	activity.setListViewContent(content);
		    	
		    	activity.resetListAdapter();
		    	
		    	activity.notifyListViewDataSetChanged();
		    	
		    }
		};
		
		mainHandler.post(r);
	}
	
	@Override
	public void onResume() {
		reload();

		super.onResume();
	}

}
