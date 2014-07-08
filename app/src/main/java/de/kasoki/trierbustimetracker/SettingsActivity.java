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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;
import de.kasoki.trierbustimetracker.utils.Helper;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupTheme();

        if(Helper.getCurrentAPILevel() >= 7) {
            this.addHomeAsUpButtonToActionBar();
        }

        toggleDeleteEverything(false);
    }

    private void setupTheme() {
        this.getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor(MainActivity.ACTIONBAR_COLOR)));
    }

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

        super.onStart();
    }

    @Override
    protected void onStop() {
        // save settings
        super.onStop();
    }
}
