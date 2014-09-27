package de.kasoki.trierbustimetracker.adapter

import scala.collection.mutable.Buffer

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import de.kasoki.swtrealtime.BusTime

import de.kasoki.trierbustimetracker.R;

class BusTimeAdapter(val context:Context) extends BaseAdapter {
    var items:List[BusTime] = List[BusTime]()

    override def getCount():Int = items.length
    override def getItem(position:Int):Object = items(position)
    override def getItemId(position:Int):Long = position

    override def getView(position:Int, convertView:View, parent:ViewGroup):View = {
        var view = convertView

        if(view == null) {
            val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

            view = vi.inflate(R.layout.list_item_bustimes, null)
        }

        val numberText = view.findViewById(R.id.number_text).asInstanceOf[TextView]
        val destinationText = view.findViewById(R.id.destination_text).asInstanceOf[TextView]
        val timeText = view.findViewById(R.id.time_text).asInstanceOf[TextView]

        val time = items(position)

        numberText.setText(time.number.toString)
        destinationText.setText(time.destination)

        if(time.delay > 0) {
            timeText.setText(time.arrivalTimeAsString + " + " + time.delay + "m")
        } else {
            timeText.setText(time.arrivalTimeAsString)
        }

        return view
    }
}