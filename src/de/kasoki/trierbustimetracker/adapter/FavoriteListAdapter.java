package de.kasoki.trierbustimetracker.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import de.kasoki.trierbustimetracker.R;

public class FavoriteListAdapter extends BaseAdapter {

	private ArrayList<String> favorites;
	private Context context;

	public FavoriteListAdapter(ArrayList<String> favorites, Context context) {
		this.favorites = favorites;
		this.context = context;
	}

	@Override
	public int getCount() {
		return favorites.size();
	}

	@Override
	public Object getItem(int position) {
		return favorites.get(position);
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
			view = vi.inflate(R.layout.list_item_favorites, null);
		}

		TextView busStopName = (TextView) view.findViewById(R.id.busstop_name);

		busStopName.setText(favorites.get(position));

		return view;
	}

}
