package de.kasoki.trierbustimetracker

import org.scaloid.common._
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class MainActivity extends SActivity {

    override def onCreate(bundle:Bundle) {
        super.onCreate(bundle);

        this.setContentView(R.layout.activity_main)
    }

    override def onCreateOptionsMenu(menu:Menu):Boolean = {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.main, menu)
        return true;
    }

    override def onOptionsItemSelected(item:MenuItem):Boolean = {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()

        if(id == R.id.action_donate) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}