package com.example.jrmy.dmhours;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private class DownloadBusHoursTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
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
                json = json.getJSONObject("A");
                Iterator<String> it = json.keys();
                List<String> list = new ArrayList<String>();
                ArrayAdapter adapterTo = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item);
                ArrayAdapter adapterFrom = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item);
                while (it.hasNext()) {
                    String st = it.next();
                    st = st.substring(0, st.length() - 2);
                    String[] sp1 = st.split("\\|");
                    String id = sp1[0];
                    String[] sp2 = sp1[1].split(" : ");
                    String city = sp2[0];
                    String station = sp2[1];
                    list.add(id + "|" + station + " : " + city);
                }
                adapterTo.addAll(list);
                adapterFrom.addAll(list);
                toSpinner.setAdapter(adapterTo);
                fromSpinner.setAdapter(adapterFrom);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hours, container, false);
        hoursToListView = (ListView) view.findViewById(R.id.hoursToListView);
        hoursFromListView = (ListView) view.findViewById(R.id.hoursFromListView);
        toSpinner = (Spinner) view.findViewById(R.id.toSpinner);
        fromSpinner = (Spinner) view.findViewById(R.id.fromSpinner);
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
        DownloadBusHoursTask task = new DownloadBusHoursTask();
        task.execute(new String[]{url});
    }


    public interface HoursFragmentCallBack {
        public void onHoursFragmentInteraction();
    }

}
