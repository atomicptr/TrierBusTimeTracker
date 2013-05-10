package de.kasoki.trierbustimetracker;

import de.kasoki.trierbustimetracker.utils.Helper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		if(Helper.getCurrentAPILevel() >= 16) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		toggleDeleteEverything(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void toggleDeleteEverything(boolean bool) {
		((ToggleButton) this.findViewById(R.id.deleteAllConfigurationFilesToggleButton)).setChecked(bool);
		this.findViewById(R.id.deleteAllConfigurationFilesButton).setEnabled(bool);
	}
	
	public void onDeleteAllConfigurationFilesToggleButtonClicked(View view) {
		this.findViewById(R.id.deleteAllConfigurationFilesButton).setEnabled(((ToggleButton) view).isChecked());
	}
	
	public void onDeleteAllConfigurationFilesButtonClicked(View view) {
		
		Intent intent = getIntent();
		
		intent.putExtra("DELETE_SETTINGS", true);
		
		this.setResult(RESULT_OK, intent);
		
		toggleDeleteEverything(false);
		finish();
	}
}
