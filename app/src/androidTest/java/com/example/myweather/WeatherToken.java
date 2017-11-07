package com.example.myweather;

import android.util.Log;

/**
 * Created by tubaozi on 10/30/17.
 */
public class WeatherToken {
    static final String BASE_FORCUST_WEATHER_URI = "http://api.openweathermap.org/data/2.5/forecast?";
    static final String BASE_CURRENT_WEATHER_URI = "http://api.openweathermap.org/data/2.5/weather?";
    static final String WEATHER_CREDENTIAL = "1a3aff4a56d078c49614644ba6f7ea37";

    public static String currentWeatherApiRequest(double lat, double lon){
        StringBuilder sb=new StringBuilder(BASE_CURRENT_WEATHER_URI);
        sb.append(String.format("lat=%s&lon=%s&appid=%s",lat,lon,WEATHER_CREDENTIAL));
        return sb.toString();
    }

    public static String forcustWeatherApiRequest(double lat, double lon){
        StringBuilder sb=new StringBuilder(BASE_FORCUST_WEATHER_URI);
        sb.append(String.format("lat=%s&lon=%s&appid=%s",lat,lon,WEATHER_CREDENTIAL));
        Log.e("URL Added",sb.toString());
        System.out.println(sb.toString());
        return sb.toString();
    }


}
