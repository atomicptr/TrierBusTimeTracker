package de.kasoki.trierbustimetracker;

import de.kasoki.trierbustimetracker.utils.Helper;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		if(Helper.getCurrentAPILevel() >= 11) {
			this.addHomeAsUpButtonToActionBar();
		}

		TextView versionTextView = (TextView) this.findViewById(R.id.versionTextView);
		versionTextView.setText(Helper.getVersion(this));
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void addHomeAsUpButtonToActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
}
