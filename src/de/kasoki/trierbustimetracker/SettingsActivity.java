package de.kasoki.trierbustimetracker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ToggleButton;
import de.kasoki.trierbustimetracker.utils.ConfigurationManager;
import de.kasoki.trierbustimetracker.utils.Helper;

public class SettingsActivity extends Activity {

	private ConfigurationManager config;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		if(Helper.getCurrentAPILevel() >= 11) {
			this.addHomeAsUpButtonToActionBar();
		}
		
		config = new ConfigurationManager(this);
		
		toggleDeleteEverything(false);
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
	
	@Override
	protected void onStart() {
		// load settings

		CheckBox autoReloadCheckbox = (CheckBox) this.findViewById(R.id.autoReloadCheckbox);
		CheckBox useNotificationsForReloadCheckbox = (CheckBox) this.findViewById(R.id.use_notifications_for_reload_checkbox);
        CheckBox useMobileConnectionForAppUpdatesCheckbox = (CheckBox) this.findViewById(R.id.use_mobile_conn_for_app_update_checkbox);

		config.loadSettingsActivity();
		
		try {
			autoReloadCheckbox.setChecked(config.useAutoReload());
			useNotificationsForReloadCheckbox.setChecked(config.useNotifications());
            useMobileConnectionForAppUpdatesCheckbox.setChecked(config.useMobileConnectionForAppUpdates());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onStart();
	}

	@Override
	protected void onStop() {
		// save settings
		
		CheckBox autoReloadCheckbox = (CheckBox) this.findViewById(R.id.autoReloadCheckbox);
		CheckBox useNotificationsForReloadCheckbox = (CheckBox) this.findViewById(R.id.use_notifications_for_reload_checkbox);
        CheckBox useMobileConnectionForAppUpdatesCheckbox = (CheckBox) this.findViewById(R.id.use_mobile_conn_for_app_update_checkbox);

		boolean useAutoReload = autoReloadCheckbox.isChecked();
		boolean useNotifications = useNotificationsForReloadCheckbox.isChecked();
        boolean useMobileConnectionForAppUpdates = useMobileConnectionForAppUpdatesCheckbox.isChecked();
		
		config.saveSettingsActivity(useAutoReload, useNotifications, useMobileConnectionForAppUpdates);

		super.onStop();
	}
}
