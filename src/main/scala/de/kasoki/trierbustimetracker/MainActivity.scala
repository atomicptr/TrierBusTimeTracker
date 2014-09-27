package de.kasoki.trierbustimetracker

import org.scaloid.common._
import scala.collection.mutable.Buffer
import scala.collection.JavaConversions

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget._
import android.util.Log;

import de.kasoki.swtrealtime.BusTime
import de.kasoki.swtrealtime.BusStop

import de.kasoki.trierbustimetracker.adapter.FavoritesListAdapter

import de.kasoki.trierbustimetracker.utils.ActionBarHelper
import de.kasoki.trierbustimetracker.utils.AndroidHelper

class MainActivity extends SActivity {

    private val favorites:Buffer[String] = Buffer[String]()
    private val favoritesListAdapter = new FavoritesListAdapter(favorites, this)

    override def onCreate(bundle:Bundle) {
        super.onCreate(bundle);

        Log.d("TrierBusTimeTracker", "Start... Version: " + AndroidHelper.version(this))

        ActionBarHelper.colorActionBar(this)

        this.setContentView(R.layout.activity_main)

        // init bus stop spinner
        val busStopSpinner = this.findViewById(R.id.busStopSpinner).asInstanceOf[Spinner]

        val busStops = Buffer[String]()

        for(name <- BusStop.names) {
            busStops += name
        }

        val spinnerAdapter = new ArrayAdapter[String](
            this,
            R.layout.list_item_busstop_spinner,
            R.id.spinner_busstop_name,
            JavaConversions.bufferAsJavaList(busStops)
        )

        busStopSpinner.setAdapter(spinnerAdapter)

        // init favorites list
        val listView = this.findViewById(R.id.favoritesListView).asInstanceOf[ListView]

        listView.setAdapter(favoritesListAdapter)
    }

    def onAddToFavoritesButtonClicked(view:View) {
        val busStopSpinner = this.findViewById(R.id.busStopSpinner).asInstanceOf[Spinner]

        val item = busStopSpinner.getSelectedItem().asInstanceOf[String]

        if(!this.favorites.contains(item)) {
            runOnUiThread({
                favorites += item

                favoritesListAdapter.notifyDataSetChanged()
            })
        } else {
            val itemAlreadyOnFavoritesText = getResources().getString(
                R.string.item_already_on_favorites_list_text)

            toast(itemAlreadyOnFavoritesText)
        }
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