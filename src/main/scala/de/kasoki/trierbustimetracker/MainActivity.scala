package de.kasoki.trierbustimetracker

import org.scaloid.common._
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import de.kasoki.trierbustimetracker.utils.ActionBarHelper

class MainActivity extends SActivity {

    override def onCreate(bundle:Bundle) {
        super.onCreate(bundle);

        ActionBarHelper.colorActionBar(this)

        this.setContentView(R.layout.activity_main)
    }

    override def onCreateOptionsMenu(menu:Menu):Boolean = {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.main, menu)
        return true;
    }

    override def onOptionsItemSelected(item:MenuItem):Boolean = {
        item.getItemId() match {
            case R.id.action_donate => {
                val intent = new Intent(this, classOf[DonateActivity])
                this.startActivity(intent)

                return true
            }

            case R.id.action_about_tbbt => {
                val intent = new Intent(this, classOf[AboutActivity])
                this.startActivity(intent)

                return true
            }
        }

        return super.onOptionsItemSelected(item);
    }
}