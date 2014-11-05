package de.kasoki.trierbustimetracker

import org.scaloid.common._
import scala.collection.mutable.Buffer
import scala.collection.JavaConversions

import android.content.Intent
import android.os.Bundle
import android.app._
import android.view._
import android.widget._
import android.graphics._
import android.content.Context._
import android.util.Log

import android.provider.BaseColumns
import android.database.MatrixCursor

import de.kasoki.swtrealtime.BusTime
import de.kasoki.swtrealtime.BusStop

import de.kasoki.trierbustimetracker.adapter.FavoritesListAdapter

import de.kasoki.trierbustimetracker.utils.ActionBarHelper
import de.kasoki.trierbustimetracker.utils.AndroidHelper
import de.kasoki.trierbustimetracker.utils.FavoritesManager
import de.kasoki.trierbustimetracker.utils.Identifier
import de.kasoki.trierbustimetracker.utils.ShortcutManager
import de.kasoki.trierbustimetracker.utils.RateMyAppHelper

class MainActivity extends SActivity with SearchView.OnQueryTextListener with SearchView.OnSuggestionListener {

    private val favoritesListAdapter = new FavoritesListAdapter(this)
    private var adapter:SimpleCursorAdapter = null

    private var searchView:SearchView = null

    override def onCreate(bundle:Bundle) {
        super.onCreate(bundle);

        debug("Start... Version: " + AndroidHelper.version(this))

        this.setContentView(R.layout.activity_main)

        ActionBarHelper.colorActionBar(this)

        // init favorites list
        val listView = this.findViewById(R.id.favorites_list_view).asInstanceOf[ListView]

        listView.setAdapter(favoritesListAdapter)

        listView.onItemClick((parent:AdapterView[_], view:View, pos:Int, id:Long) => {
            val name = parent.getItemAtPosition(pos).asInstanceOf[String]

            startBusTimeActivity(name)
        })

        this.registerForContextMenu(listView)
    }

    def startBusTimeActivity(busStopName:String) {
        val busStop = BusStop.getBusStopByName(busStopName)

        val intent = new Intent(this, classOf[BusTimeActivity])

        intent.putExtra("BUS_TIME_CODE", busStop.code)
        intent.putExtra("FROM_MAIN_ACTIVITY", true)

        this.startActivity(intent)

        AndroidHelper.slideIn(this)
    }

    override def onCreateOptionsMenu(menu:Menu):Boolean = {
        this.getMenuInflater().inflate(R.menu.main, menu)

        val searchViewMenuItem = menu.findItem(R.id.action_search)

        searchView = searchViewMenuItem.getActionView().asInstanceOf[SearchView]

        if(searchView != null) {
            searchView.setIconifiedByDefault(true)
            searchView.setQueryHint(getString(R.string.search_hint))

            val from = Array("BusStopName")
            val to = Array(R.id.text1)

            adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item_searchview,
                null,
                from,
                to
            )

            searchView.setSuggestionsAdapter(adapter)

            searchView.setOnQueryTextListener(this)
            searchView.setOnSuggestionListener(this)
        } else {
            error("searchView was null?")
        }

        return true;
    }

    override def onOptionsItemSelected(item:MenuItem):Boolean = {
        item.getItemId() match {
            case R.id.action_rate => {
                RateMyAppHelper.openRateIntent(this)

                return true
            }

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
        if(view.getId() == R.id.favorites_list_view) {
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
        val busStop = BusStop.getBusStopByName(name)

        ShortcutManager.create(this, busStop)

        toast(getString(R.string.shortcut_created, name))
    }

    def contextDeleteEntry(position:Int) {
        val item = favoritesListAdapter.items(position)

        favoritesListAdapter.items.remove(position)

        favoritesListAdapter.items = favoritesListAdapter.items.sorted

        favoritesListAdapter.notifyDataSetChanged()

        val message = getResources().getString(R.string.favorite_item_deleted, item)

        checkIfThereAreStillFavorites()

        toast(message)
    }

    private def checkIfThereAreStillFavorites() {
        if(favoritesListAdapter.items.length > 0) {
            findViewById(R.id.no_favorites_overlay).setVisibility(View.INVISIBLE)
        } else {
            findViewById(R.id.no_favorites_overlay).setVisibility(View.VISIBLE)
        }
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

    override def onQueryTextChange(query:String):Boolean = {
        val c = new MatrixCursor(Array(BaseColumns._ID, "BusStopName"))

        var i = 0

        for(bs <- BusStop.values) {
            val name = bs.asInstanceOf[BusStop.BusStopType].name

            if(name.toLowerCase.startsWith(query.toLowerCase)) {
                c.addRow(Array[Object](i.asInstanceOf[Object], name.asInstanceOf[Object]))
            }

            i += 1
        }

        adapter.changeCursor(c)

        return false
    }

    override def onQueryTextSubmit(query:String):Boolean = {
        for(bs <- BusStop.values) {
            val name = bs.asInstanceOf[BusStop.BusStopType].name

            if(name.toLowerCase.startsWith(query.toLowerCase)) {
                startBusTimeActivity(name)

                return false
            }
        }

        return false
    }

    override def onSuggestionClick(position:Int):Boolean = {
        val name = adapter.getItem(position).asInstanceOf[MatrixCursor].getString(1)

        startBusTimeActivity(name)

        return true
    }

    override def onSuggestionSelect(position:Int):Boolean = {
        return false
    }

    override def onStart() {
        favoritesListAdapter.items = FavoritesManager.load(this)

        checkIfThereAreStillFavorites()

        favoritesListAdapter.notifyDataSetChanged()

        RateMyAppHelper.openAfterNCalls(this, 20)

        super.onStart()
    }

    override def onStop() {
        FavoritesManager.save(this, favoritesListAdapter.items)

        super.onStop()
    }
}
