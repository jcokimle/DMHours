package com.example.jrmy.dmhours;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class HoursFragment extends Fragment {

    private ListView hoursToListView;
    private ListView hoursFromListView;
    private Spinner toSpinner;
    private Spinner fromSpinner;

    private HoursFragmentCallBack mListener;

    private class SpecialAdapter<String> extends ArrayAdapter<String> {
        private int[] colors = new int[] { Color.GRAY, Color.LTGRAY };

        public SpecialAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            int colorPos = position % colors.length;
            view.setBackgroundColor(colors[colorPos]);
            return view;
        }
    }

    private class DownloadBusHoursTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                Log.d("[GET URL]", url);
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("[GET REQUEST]", result);
            try {
                JSONObject json = new JSONObject(result);
                JSONObject to = json.getJSONObject("A");
                JSONObject from = json.getJSONObject("R");
                Iterator<?> toKeys = to.keys();
                List<Station> listTo = new ArrayList<Station>();
                List<Station> listFrom = new ArrayList<Station>();
                while (toKeys.hasNext()) {
                    String toKey = (String) toKeys.next();
                    String id = toKey.split("\\|")[0];
                    String city = toKey.split("\\|")[1].split(":")[0].trim();
                    String station = toKey.split("\\|")[1].split(":")[1].trim();
                    String something = toKey.split("\\|")[1].split(":")[2].trim();
                    List<Timing> timings = new ArrayList<Timing>();
                    if (to.get(toKey) instanceof JSONObject) {
                        JSONObject stationJSON = (JSONObject) to.get(toKey);
                        Iterator<?> toSubkeys = stationJSON.keys();
                        while (toSubkeys.hasNext()) {
                            String c = (String) toSubkeys.next();
                            String h = (String) stationJSON.get(c);
                            if (!"-".equals(h)) {
                                timings.add(new Timing(c, h, "A"));
                            }
                        }
                    }
                    Iterator<String> fromKeys = from.keys();
                    String fromKey = "";
                    while (fromKeys.hasNext()) {
                        String tmp = fromKeys.next();
                        if (tmp.split("\\|")[1].equals(toKey.split("\\|")[1])) {
                            fromKey = tmp;
                            break;
                        }
                    }
                    if (!fromKey.isEmpty() && from.get(fromKey) instanceof JSONObject) {
                        JSONObject stationJSON = (JSONObject) from.get(fromKey);
                        Iterator<?> fromSubkeys = stationJSON.keys();
                        while (fromSubkeys.hasNext()) {
                            String c = (String) fromSubkeys.next();
                            String h = (String) stationJSON.get(c);
                            if (!"-".equals(h)) {
                                timings.add(new Timing(c, h, "R"));
                            }
                        }
                    }
                    listFrom.add(new Station(id, city, station, something, timings));
                    listTo.add(new Station(id, city, station, something, timings));
                }
                Collections.sort(listFrom);
                Collections.sort(listTo);
                ArrayAdapter adapterTo = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item);
                ArrayAdapter adapterFrom = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item);
                adapterTo.addAll(listTo);
                adapterFrom.addAll(listFrom);
                toSpinner.setAdapter(adapterTo);
                fromSpinner.setAdapter(adapterFrom);
                displayTimings();
            } catch (JSONException e) {
                Log.e("[JSON]", "Error parsing JSON", e);
            }
        }
    }

    public HoursFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hours, container, false);
        hoursToListView = (ListView) view.findViewById(R.id.hoursToListView);
        hoursFromListView = (ListView) view.findViewById(R.id.hoursFromListView);
        toSpinner = (Spinner) view.findViewById(R.id.toSpinner);
        fromSpinner = (Spinner) view.findViewById(R.id.fromSpinner);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                displayTimings();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                displayTimings();
            }

        public void onNothingSelected(AdapterView<?> adapterView) {
            return;
        }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (HoursFragmentCallBack) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void getHours(String url) {
        Log.d("[GET HOURS]", "getHours()");
        DownloadBusHoursTask task = new DownloadBusHoursTask();
        task.execute(new String[]{url});
    }


    public interface HoursFragmentCallBack {
        public void onHoursFragmentInteraction();
    }

    private void displayTimings() {
        Log.d("[DISPLAY TIMINGS]", "displayTimings()");
        Station toStation = (Station) toSpinner.getSelectedItem();
        Station fromStation = (Station) fromSpinner.getSelectedItem();
        if (toStation == null || fromStation == null ) return;
        List<String> left = new ArrayList<String>();
        List<String> right = new ArrayList<String>();
        for (Timing t1 : toStation.getTimings()) {
            for (Timing t2 : fromStation.getTimings()) {
                if (t1.getC().equals(t2.getC())){
                    t2.setH(("00".equals(t2.getH().substring(0,2)))?("24:"+t2.getH().substring(3, t2.getH().length())):(t2.getH()));
                    t1.setH(("00".equals(t1.getH().substring(0,2)))?("24:"+t1.getH().substring(3,t1.getH().length())):(t1.getH()));
                    if (t2.getH().compareTo(t1.getH()) > 0) {
                        left.add(t1.getH() + " " + t2.getH());// + "|" + t2.getC());
                    }
                    else {
                        right.add(t2.getH() + " " + t1.getH());// + "|" + t2.getC());
                    }
                }
            }
        }
        Collections.sort(left);
        Collections.sort(right);
        SpecialAdapter<String> adapter = new SpecialAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        SpecialAdapter<String> adapter2 = new SpecialAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        adapter.addAll(right);
        adapter2.addAll(left);
        hoursToListView.setAdapter(adapter);
        hoursFromListView.setAdapter(adapter2);
    }

}
