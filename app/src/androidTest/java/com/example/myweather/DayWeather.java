package com.example.myweather;

/**
 * Created by tubaozi on 10/31/17.
 */
public class DayWeather {

    public DayWeather(String datetime, double min_temp, double max_temp, String weather){
        this.min_temp=min_temp;
        this.max_temp=max_temp;
        this.weather=weather;
    }
    public double getMin_temp() {
        return min_temp;
    }

    public void setMin_temp(double min_temp) {
        this.min_temp = min_temp;
    }

    public double getMax_temp() {
        return max_temp;
    }

    public void setMax_temp(double max_temp) {
        this.max_temp = max_temp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }



    public String getDaytime() {
        return daytime;
    }

    public void setDaytime(String daytime) {
        this.daytime = daytime;
    }

    String daytime;
    double min_temp;
    double max_temp;
    String weather;
    long timestamp;


}
