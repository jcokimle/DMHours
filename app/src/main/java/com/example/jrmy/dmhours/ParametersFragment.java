package com.example.jrmy.dmhours;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;


public class ParametersFragment extends Fragment implements View.OnClickListener {

    private Spinner busSpinner;
    private EditText dateEditText;

    private ParametersFragmentCallBack mListener;

    private class DownloadBusLinesTask extends AsyncTask<String, Void, String> {
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
                JSONArray json = new JSONArray(result);
                String[] array_spinner = new String[json.length()];
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item);
                for (int i = 0 ; i < json.length() - 1 ; ++i) {
                    adapter.add(json.getJSONObject(i).getString("libelle"));
                }
                if (busSpinner != null)
                busSpinner.setAdapter(adapter);
            } catch (JSONException e) {
                Log.e("[JSON]", "Error parsing JSON", e);
            }
        }
    }

    public ParametersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ParametersFragmentCallBack) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parameters, container, false);
        busSpinner = (Spinner) view.findViewById(R.id.busSpinner);
        dateEditText = (EditText) view.findViewById(R.id.dateEditText);
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        dateEditText.setText(String.format("%02d", c.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d", c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR));
        Button b = (Button) view.findViewById(R.id.buttonGo);
        b.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        String url = "http://www.transports-daniel-meyer.fr/zf/json/lignes/id_commune/0";
        DownloadBusLinesTask task = new DownloadBusLinesTask();
        task.execute(new String[]{url});
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public interface ParametersFragmentCallBack {
        public void onParametersFragmentInteraction(String url);
    }

    @Override
    public void onClick(View v) {
        String date = dateEditText.getText().toString().replace('/', '-');
        String ligne = (String) busSpinner.getSelectedItem();
        String bus = ligne.split(" ")[0];
        String url = "http://www.transports-daniel-meyer.fr/zf/json/horaires/date/" + date + "/num/" + bus;
        mListener.onParametersFragmentInteraction(url);
    }

}
