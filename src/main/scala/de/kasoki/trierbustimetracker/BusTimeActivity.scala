package de.kasoki.trierbustimetracker

import org.scaloid.common._
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import de.kasoki.trierbustimetracker.utils.ActionBarHelper
import de.kasoki.trierbustimetracker.utils.AndroidHelper

class BusTimeActivity extends SActivity {

    override def onCreate(bundle:Bundle) {
        super.onCreate(bundle)

        this.setContentView(R.layout.activity_bustimes)

        ActionBarHelper.colorActionBar(this)
        ActionBarHelper.enableHomeAsUp(this)

        val intent = getIntent()

        val code = intent.getStringExtra("BUS_TIME_CODE")

        println(code)
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