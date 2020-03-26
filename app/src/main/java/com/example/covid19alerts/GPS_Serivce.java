package com.example.covid19alerts;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;

import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;

public class GPS_Serivce extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    private FileCacher<String> home_loc;
    private double thresh = 0.02;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // sending data to main activity
                // broadcasting
                Intent i = new Intent("location_update");
                i.putExtra("coordinates", location.getLongitude() + " " + location.getLatitude());
                home_loc = new FileCacher<>(getApplicationContext(), "myloc.txt");
                sendBroadcast(i);
                if (home_loc.hasCache()) {
                    try {
                        String loc = home_loc.readCache();
                        String[] coors1 = loc.split(" ");
                        double lat1 = location.getLatitude();
                        double long1 = location.getLongitude();
                        double lat2 = Double.parseDouble(coors1[1]);
                        double long2 = Double.parseDouble(coors1[0]);
                        double dist = distance(lat1, lat2, long1, long2);
                        if (dist > thresh) {
                            i.putExtra("exceed", "true");
                        } else {
                            i.putExtra("exceed", "false");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,listener);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(listener);
        }
    }

    private double distance(double lat1, double lat2, double lon1, double lon2)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }
}
