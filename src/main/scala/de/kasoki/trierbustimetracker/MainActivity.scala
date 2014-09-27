package de.kasoki.trierbustimetracker

import org.scaloid.common._
import scala.collection.mutable.Buffer
import scala.collection.JavaConversions

import android.content.Intent
import android.os.Bundle
import android.view._
import android.widget._
import android.content.Context._
import android.util.Log

import de.kasoki.swtrealtime.BusTime
import de.kasoki.swtrealtime.BusStop

import de.kasoki.trierbustimetracker.adapter.FavoritesListAdapter

import de.kasoki.trierbustimetracker.utils.ActionBarHelper
import de.kasoki.trierbustimetracker.utils.AndroidHelper
import de.kasoki.trierbustimetracker.utils.Identifier

class MainActivity extends SActivity {

    private val favoritesListAdapter = new FavoritesListAdapter(this)

    override def onCreate(bundle:Bundle) {
        super.onCreate(bundle);

        Log.d("TrierBusTimeTracker", "Start... Version: " + AndroidHelper.version(this))

        ActionBarHelper.colorActionBar(this)

        this.setContentView(R.layout.activity_main)

        // init bus stop spinner
        val busStopSpinner = spinner()

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

        listView.onItemClick((parent:AdapterView[_], view:View, pos:Int, id:Long) => {
            val name = parent.getItemAtPosition(pos).asInstanceOf[String]

            startBusTimeActivity(name)
        })

        this.registerForContextMenu(listView)
    }

    def onAddToFavoritesButtonClicked(view:View) {
        val busStopSpinner = spinner()

        val item = busStopSpinner.getSelectedItem().asInstanceOf[String]

        if(!this.favoritesListAdapter.items.contains(item)) {
            runOnUiThread({
                favoritesListAdapter.items += item
                favoritesListAdapter.items = favoritesListAdapter.items.sorted

                favoritesListAdapter.notifyDataSetChanged()
            })
        } else {
            val itemAlreadyOnFavoritesText = getResources().getString(
                R.string.item_already_on_favorites_list_text)

            toast(itemAlreadyOnFavoritesText)
        }
    }

    def onActionSelectedButtonClicked(view:View) {
        val busStopSpinner = spinner()

        val name = busStopSpinner.getSelectedItem().asInstanceOf[String]

        startBusTimeActivity(name)
    }

    def startBusTimeActivity(busStopName:String) {
        val busStop = BusStop.getBusStopByName(busStopName)

        if(AndroidHelper.isNetworkAvailable(this)) {
            /*val intent = new Intent(this, classOf[BusTimeActivity])

            intent.putExtra("BUS_TIME_CODE", busStop.code)

            this.startActivity(intent)*/

            println(busStop.code)
        } else {
            Log.d("TrierBusTimeTracker", "NO NETWORK CONNECTION")

            val noNetworkText = getResources().getString(
                R.string.no_network_connection_text)

            toast(noNetworkText)
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

    override def onCreateContextMenu(menu:ContextMenu, view:View, menuInfo:ContextMenu.ContextMenuInfo) {
        if(view.getId() == R.id.favoritesListView) {
            val info = menuInfo.asInstanceOf[AdapterView.AdapterContextMenuInfo]

            menu.setHeaderTitle(favoritesListAdapter.items(info.position))

            val menuItems = getResources().getStringArray(R.array.favorites_menu)

            var i = 0

            for(item <- menuItems) {
                menu.add(Menu.NONE, i, i, item)

                i += 1
            }
        }
    }

    override def onContextItemSelected(item:android.view.MenuItem):Boolean = {
        val info = item.getMenuInfo().asInstanceOf[AdapterView.AdapterContextMenuInfo]

        val selectedItem = favoritesListAdapter.items(info.position)

        item.getItemId() match {
            case 0 => startBusTimeActivity(selectedItem)
            case 1 => contextAddShortcutToHomescreen(selectedItem)
            case 2 => contextDeleteEntry(info.position)
        }

        return true
    }

    def contextAddShortcutToHomescreen(name:String) {
        val code = BusStop.getBusStopByName(name).code

        /*val shortcutIntent = new Intent(this, classOf[BusTimeActivity])

        shortcutIntent.setAction(Intent.ACTION_MAIN)

        shortcutIntent.putExtra("BUS_TIME_CODE", code)

        val addIntent = new Intent()
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher))

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT")

        this.sendBroadcast(addIntent)

        toast(getString(R.string.shortcut_created, name))*/
    }

    def contextDeleteEntry(position:Int) {
        val item = favoritesListAdapter.items(position)

        favoritesListAdapter.items.remove(position)

        favoritesListAdapter.items = favoritesListAdapter.items.sorted

        favoritesListAdapter.notifyDataSetChanged()

        val message = getResources().getString(R.string.favorite_item_deleted, item)

        toast(message)
    }

    override def onSaveInstanceState(state:Bundle) {
        state.putStringArray(Identifier.APP_PREFERENCES_FAVORITE_IDENTIFIER,
            this.favoritesListAdapter.items.toArray)
    }

    override def onRestoreInstanceState(state:Bundle) {
        this.favoritesListAdapter.items = state.getStringArray(
            Identifier.APP_PREFERENCES_FAVORITE_IDENTIFIER).toList.toBuffer

        this.favoritesListAdapter.notifyDataSetChanged()
    }

    override def onStart() {
        val prefs = getSharedPreferences(Identifier.APP_FAVORITE_FILE_IDENTIFIER, MODE_PRIVATE)

        val length = prefs.getInt("favorites_length", 0)

        favoritesListAdapter.items.clear()

        for(i <- 0 until length) {
            favoritesListAdapter.items += prefs.getString("favorites" + i, null)
        }

        favoritesListAdapter.items = favoritesListAdapter.items.sorted

        favoritesListAdapter.notifyDataSetChanged()

        super.onStart()
    }

    override def onStop() {
        val prefs = getSharedPreferences(Identifier.APP_FAVORITE_FILE_IDENTIFIER, MODE_PRIVATE)
        val editor = prefs.edit()

        val length = favoritesListAdapter.getCount()

        editor.putInt("favorites_length", length)

        for(i <- 0 until length) {
            editor.putString("favorites" + i, favoritesListAdapter.items(i))
        }

        editor.commit()

        super.onStop()
    }

    def spinner():Spinner = this.findViewById(R.id.busStopSpinner).asInstanceOf[Spinner]
}