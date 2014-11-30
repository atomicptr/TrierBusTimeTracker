package de.kasoki.trierbustimetracker2.adapter

import scala.collection.mutable.Buffer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import de.kasoki.swtrealtime.BusTime

import de.kasoki.trierbustimetracker2.R

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

        var op = "+"
        var delay = time.delay.toString
        var minute = "m"

        if(time.delay == 0) {
            op = ""
            delay = ""
            minute = ""
        }

        numberText.setText("%02d".format(time.number))
        destinationText.setText(time.destination)
        timeText.setText(context.getResources().getString(R.string.bustime_arrival_text,
            time.arrivalTimeAsString, op, delay, minute))

        return view
    }
}
