package de.kasoki.trierbustimetracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import de.kasoki.trierbustimetracker.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BusTimeAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, String>> data;
    private Context context;

    public BusTimeAdapter(ArrayList<HashMap<String, String>> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.list_item_bustimes, null);
        }

        TextView numberText = (TextView) view.findViewById(R.id.number_text);
        TextView destinationText = (TextView) view.findViewById(R.id.destination_text);
        TextView timeText = (TextView) view.findViewById(R.id.time_text);

        numberText.setText(data.get(position).get("NUMBER"));
        destinationText.setText(data.get(position).get("DESTINATION"));
        timeText.setText(data.get(position).get("TIME"));

        return view;
    }
}
