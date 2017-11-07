package com.example.myweather;

/**
 * Created by tubaozi on 10/31/17.
 */
public class HourWeather {

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }



    public double getTempInK() {
        return tempInK;
    }

    public void setTempInK(double tempInK) {
        this.tempInK = tempInK;
    }


    public HourWeather(String dateTime, String weather, double tempInK, long timestamp){
        this.dateTime=dateTime;
        this.weather=weather;
        this.tempInK=tempInK;
        this.timestamp=timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    long timestamp;
    double tempInK;
    String weather;
    String dateTime;
    int hour=0;
}
