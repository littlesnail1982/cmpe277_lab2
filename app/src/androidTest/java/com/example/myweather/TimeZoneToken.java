package com.example.myweather;

/**
 * Created by tubaozi on 11/3/17.
 */

public class TimeZoneToken {

    static final String BASE_GOOGLE_MAPS_URI = "https://maps.googleapis.com/maps/api/timezone/json?";
    static final String BASE_API_KEY = "AIzaSyAD9FgyCQ1jwNyAXjSAr7rd3ZabGTs3bxo";

    public  static String currentTimeZonerApiRequest(double lat, double lon, long timestamp){
        StringBuilder sb=new StringBuilder(BASE_GOOGLE_MAPS_URI);
        sb.append(String.format("location=%s,%s&timestamp=%s&key=%s",lat,lon,timestamp, BASE_API_KEY));
        return sb.toString();
    }
}
