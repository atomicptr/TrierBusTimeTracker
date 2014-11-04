package de.kasoki.trierbustimetracker

import org.scaloid.common._
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView

import de.kasoki.trierbustimetracker.utils.ActionBarHelper

class DonateActivity extends SActivity {

    override def onCreate(bundle:Bundle) {
        super.onCreate(bundle)

        this.setContentView(R.layout.activity_donate)

        ActionBarHelper.colorActionBar(this)
        ActionBarHelper.enableHomeAsUp(this)

        val webView = this.findViewById(R.id.webview).asInstanceOf[WebView]
        webView.loadUrl(this.getString(R.string.donate_url))
    }

    override def onOptionsItemSelected(item:MenuItem):Boolean = {
        item.getItemId() match {
            case android.R.id.home => {
                finish();

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}