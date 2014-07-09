package de.kasoki.trierbustimetracker;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import de.kasoki.trierbustimetracker.utils.Helper;

public class DonateActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        setupTheme();

        if(Helper.getCurrentAPILevel() >= 7) {
            this.addHomeAsUpButtonToActionBar();
        }

        WebView web = (WebView) this.findViewById(R.id.webview);
        web.loadUrl(this.getString(R.string.donate_url));
    }

    private void setupTheme() {
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor(MainActivity.ACTIONBAR_COLOR)));
    }

    private void addHomeAsUpButtonToActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
