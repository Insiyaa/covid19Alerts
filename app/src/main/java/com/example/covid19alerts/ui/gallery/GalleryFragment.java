package com.example.covid19alerts.ui.gallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.covid19alerts.R;
import com.kosalgeek.android.caching.FileCacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class GalleryFragment extends Fragment {

    private ListView lv;
    private Context _context;
    private Activity a;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog pDialog;
    private ArrayList<HashMap<String, String>> DataList;
    private int mCurCheckPosition;
    private FileCacher<ArrayList<HashMap<String, String>> > cacher;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        swipeRefreshLayout = root.findViewById(R.id.pullToRefresh);
        lv = root.findViewById(R.id.list);
        DataList = new ArrayList<>();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetStats().execute();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        _context = context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        a = getActivity();
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }
        cacher = new FileCacher<>(_context, "datalist.txt");
        if (cacher.hasCache()) {
            try {
                DataList = cacher.readCache();
                updateView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateView() {
        ListAdapter adapter = new SimpleAdapter(
                _context, DataList,
                R.layout.list_item, new String[]{"location", "country",
                "confirmed", "deaths", "recovered"}, new int[]{R.id.loc,
                R.id.country, R.id.conf, R.id.deaths, R.id.rec});

        lv.setAdapter(adapter);
    }


    /**
     * Async task class to get json by making HTTP call
     */
    @SuppressLint("StaticFieldLeak")
    private class GetStats extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//             Showing progress dialog
            pDialog = new ProgressDialog(_context);
            pDialog.setMessage("Gathering Latest Data...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... args) {
//            Activity a = activities[0];
            OkHttpClient okHttpClient = new OkHttpClient();

            String url = "https://covid-19-coronavirus-statistics.p.rapidapi.com/v1/stats";
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("x-rapidapi-host", "covid-19-coronavirus-statistics.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "7514ae7d7fmsha3772bf56493c5dp146430jsnb86e68e1c037")
                    .build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                String jsonStr = response.body().string();
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONObject body = jsonObj.getJSONObject("data");
                    String lastChecked = body.getString("lastChecked");
                    Log.i(TAG, lastChecked);
                    JSONArray data = body.getJSONArray("covid19Stats");


                    // looping through All Contacts
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);

                        String location = c.getString("keyId");
                        String country = c.getString("country");
                        String confirmed = c.getString("confirmed");
                        String deaths = c.getString("deaths");
                        String recovered = c.getString("recovered");

                        // tmp hash map for single contact
                        HashMap<String, String> data1 = new HashMap<>();

                        // adding each child node to HashMap key => value
                        data1.put("location", location);
                        data1.put("country", country);
                        data1.put("confirmed", confirmed);
                        data1.put("deaths", deaths);
                        data1.put("recovered", recovered);

                        // adding contact to contact list
                        DataList.add(data1);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    a.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(_context,
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

//            Log.e(TAG, "Response from url: " + jsonStr);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            try {
                if (cacher.hasCache())
                    cacher.clearCache();
                cacher.writeCache(DataList);
            } catch (IOException e) {
                e.printStackTrace();
            }

            updateView();
        }

    }
}
    // TODO: After displaying stats, add sort feature
