package de.kasoki.trierbustimetracker.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import de.kasoki.swtrealtime.BusTime;
import de.kasoki.trierbustimetracker.BusTimeActivity;
import de.kasoki.trierbustimetracker.R;

public class ReloadTask extends AsyncTask<Integer, Integer, Long> {

	private BusTimeActivity activity;
	private List<Map<String, String>> listViewContent;

	private String busTimeCode;

	private ProgressDialog progressDialog;

	public ReloadTask(BusTimeActivity activity, String busTimeCode) {
		this.activity = activity;
		this.listViewContent = new ArrayList<Map<String, String>>();
		this.busTimeCode = busTimeCode;
	}

	@Override
	protected Long doInBackground(Integer... arg0) {
		// disable reload actions
		activity.setReloadActive(true);

		// retrieve stuff from the SWT servers
		List<BusTime> busTimesList = BusTime.fromStopCode(busTimeCode);

		Collections.sort(busTimesList);

		for (BusTime b : busTimesList) {
			Log.d("BUSTIME RECIEVED", b.toString());

			final Map<String, String> data = new HashMap<String, String>(2);

			String delay = "";

			if (b.getDelay() != 0) {
				String operand = b.getDelay() < 0 ? "-" : "+";
				delay = String.format(" %s %d%s", operand, b.getDelay(), "m");
			}

			String arrivalTimeText = activity.getResources().getString(
					R.string.bustime_arrival_text);

			data.put("FIRST_LINE",
					String.format("(%d) %s", b.getNumber(), b.getDestination()));
			data.put(
					"SECOND_LINE",
					String.format("%s: %s%s", arrivalTimeText,
							b.getArrivalTimeAsString(), delay));

			listViewContent.add(data);

		}

		// when the list is empty show the user that there are
		// no buses atm
		if (listViewContent.isEmpty()) {
			final Map<String, String> data = new HashMap<String, String>(2);
			data.put("FIRST_LINE",
					activity.getResources().getString(R.string.bustime_no_bus));
			data.put("SECOND_LINE", "");

			listViewContent.add(data);
		}

		// enable reload actions
		activity.setReloadActive(false);

		// return finished listViewContent to BusTimeActivity
		activity.onReloadTaskFinished(listViewContent);

		return 0L;
	}

	@Override
	protected void onPreExecute() {
		String loadingDialogText = activity.getResources().getString(
				R.string.loading_dialog_text);

		progressDialog = ProgressDialog.show(activity, "", loadingDialogText);
	}

	@Override
	protected void onPostExecute(Long result) {
		progressDialog.dismiss();
	}

	@Override
	public void onCancelled() {
		Log.d("CANCEL", "RELOAD TASK WAS CANCELLED");
	}

}
