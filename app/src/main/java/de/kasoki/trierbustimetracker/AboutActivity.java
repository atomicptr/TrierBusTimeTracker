package de.kasoki.trierbustimetracker;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import de.kasoki.trierbustimetracker.utils.Helper;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupTheme();

        if(Helper.getCurrentAPILevel() >= 7) {
            this.addHomeAsUpButtonToActionBar();
        }

        TextView versionTextView = (TextView) this.findViewById(R.id.versionTextView);
        versionTextView.setText(Helper.getVersion(this));
    }

    private void setupTheme() {
        getActionBar().setBackgroundDrawable(
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
}
