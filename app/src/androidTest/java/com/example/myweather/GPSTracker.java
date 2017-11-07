package com.example.myweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/**
 * Created by tubaozi on 11/3/17.
 */

public class GPSTracker implements LocationListener {
    Context context;
    int MY_PERMISSION_REQUEST_LOCATION=10;
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

    public GPSTracker(Context c) {
        context = c;
    }

    public Location getLocation() {

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //ActivityCompat.requestPermissions(context., new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                       // Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, MY_PERMISSION_REQUEST_LOCATION);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10, this);
            Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return location;

        }else{

        }
        return null;
    }




    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
