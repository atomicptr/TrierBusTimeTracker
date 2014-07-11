// Copyright (c) 2014 Christopher Kaster
//
// This file is part of Trier Bus Time Tracker <https://github.com/kasoki/TrierBusTimeTracker>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
package de.kasoki.trierbustimetracker.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import de.kasoki.swtrealtime.BusTime;
import de.kasoki.swtrealtime.exceptions.ServerResponseException;
import de.kasoki.trierbustimetracker.BusTimeActivity;
import de.kasoki.trierbustimetracker.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ReloadTask extends AsyncTask<Integer, Integer, Long> {

    private BusTimeActivity activity;
    private List<HashMap<String, String>> listViewContent;

    private String busTimeCode;

    private ProgressDialog progressDialog;

    public ReloadTask(BusTimeActivity activity, String busTimeCode) {
        this.activity = activity;
        this.listViewContent = new ArrayList<HashMap<String, String>>();
        this.busTimeCode = busTimeCode;
    }

    @Override
    protected Long doInBackground(Integer... arg0) {
        // disable reload actions
        activity.setReloadActive(true);

        // retrieve stuff from the SWT servers
        List<BusTime> busTimesList = null;

        try {
            busTimesList = BusTime.fromStopCode(busTimeCode);
        } catch (ServerResponseException e) {
            this.activity.toastServerError(e.getServerResponseCode());

            this.cancel(true);
        }

        if(!this.isCancelled()) {
            Collections.sort(busTimesList);

            for (BusTime b : busTimesList) {
                Log.d("BUSTIME RECIEVED", b.toString());

                final HashMap<String, String> data = new HashMap<String, String>();

                String delay = "";
                String operand = "";
                String suffix = "";

                if (b.getDelay() != 0) {
                    operand = b.getDelay() < 0 ? "-" : "+";

                    delay = Integer.toString(b.getDelay());
                    suffix = "m";
                }

                String arrivalTimeText = activity.getResources().getString(
                        R.string.bustime_arrival_text, b.getArrivalTimeAsString(), operand, delay, suffix);

                data.put("NUMBER", String.format("%02d", b.getNumber()));
                data.put("DESTINATION", b.getDestination());
                data.put("TIME", arrivalTimeText);

                listViewContent.add(data);
            }

            // when the list is empty show the user that there are
            // no buses atm
            if (listViewContent.isEmpty()) {
                final HashMap<String, String> data = new HashMap<String, String>();
                data.put("NUMBER", "");
                data.put("DESTINATION",
                        activity.getResources().getString(R.string.bustime_no_bus));
                data.put("TIME", "");

                listViewContent.add(data);
            }

            // enable reload actions
            activity.setReloadActive(false);

            // return finished listViewContent to BusTimeActivity
            activity.onReloadTaskFinished(listViewContent);
        } else {
            // reload task failed
            activity.setReloadActive(false);
            activity.onReloadTaskFailed();
        }

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
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onCancelled() {
        Log.d("CANCEL", "RELOAD TASK WAS CANCELLED");

        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}