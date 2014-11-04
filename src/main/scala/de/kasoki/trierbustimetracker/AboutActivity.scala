package de.kasoki.trierbustimetracker

import org.scaloid.common._
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

import de.kasoki.trierbustimetracker.utils.ActionBarHelper
import de.kasoki.trierbustimetracker.utils.AndroidHelper

class AboutActivity extends SActivity {

    override def onCreate(bundle:Bundle) {
        super.onCreate(bundle)

        this.setContentView(R.layout.activity_about)

        ActionBarHelper.colorActionBar(this)
        ActionBarHelper.enableHomeAsUp(this)

        val versionText = this.findViewById(R.id.versionTextView).asInstanceOf[TextView]

        versionText.setText(AndroidHelper.version(this))
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
