package com.example.covid19alerts.ui.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.covid19alerts.R;
import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private Context _context;
    private BroadcastReceiver broadcastReceiver;
    private FileCacher <String> home_loc;
    private String latest_loc;
    private Activity a;
    // correctly register and unregister listeners


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

//        _textView = root.findViewById(R.id.text_home);
        Button reg_home = root.findViewById(R.id.home);
        reg_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (home_loc.hasCache()) {
                    try {
                        home_loc.clearCache();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    home_loc.writeCache(latest_loc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        setup();
        return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        a = getActivity();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        _context = context;
        home_loc = new FileCacher<>(_context, "myloc.txt");
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        _context.unregisterReceiver(broadcastReceiver);
    }
    private void setup() {
        if (broadcastReceiver ==  null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //use coordinates
//                    _textView.append("\n" +intent.getExtras().get("coordinates"));
                    latest_loc = ""+ Objects.requireNonNull(intent.getExtras()).get("coordinates");
                    String exceeds = intent.getExtras().get("exceed").toString();
                    exceeds = "true";
                    if ("true" == exceeds) {
                        //alert
                        a.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(_context)
                                        .setTitle("Stay At Home!")
                                        .setMessage("Be smart, stay at home!")

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation
                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
//                                        .setNegativeButton(android.R.string.no, null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        });
                    }
                }
            };
        }
        _context.registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

//    private double distance(double lat1, double lat2, double lon1, double lon2)
//    {
//
//        // The math module contains a function
//        // named toRadians which converts from
//        // degrees to radians.
//        lon1 = Math.toRadians(lon1);
//        lon2 = Math.toRadians(lon2);
//        lat1 = Math.toRadians(lat1);
//        lat2 = Math.toRadians(lat2);
//
//        // Haversine formula
//        double dlon = lon2 - lon1;
//        double dlat = lat2 - lat1;
//        double a = Math.pow(Math.sin(dlat / 2), 2)
//                + Math.cos(lat1) * Math.cos(lat2)
//                * Math.pow(Math.sin(dlon / 2),2);
//
//        double c = 2 * Math.asin(Math.sqrt(a));
//
//        // Radius of earth in kilometers. Use 3956
//        // for miles
//        double r = 6371;
//
//        // calculate the result
//        return(c * r);
//    }

}