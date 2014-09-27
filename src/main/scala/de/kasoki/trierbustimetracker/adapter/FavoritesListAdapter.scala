package de.kasoki.trierbustimetracker.adapter

import scala.collection.mutable.Buffer

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget._

import de.kasoki.trierbustimetracker.R;

class FavoritesListAdapter(val favorites:Buffer[String], val context:Context) extends BaseAdapter {
    override def getCount():Int = favorites.length
    override def getItem(position:Int):Object = favorites(position)
    override def getItemId(position:Int):Long = position

    override def getView(position:Int, convertView:View, parent:ViewGroup):View = {
        var view = convertView

        if(view == null) {
            val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

            view = vi.inflate(R.layout.list_item_favorites, null)
        }

        val busStopName = view.findViewById(R.id.busstop_name).asInstanceOf[TextView]

        busStopName.setText(favorites(position))

        return view
    }
}