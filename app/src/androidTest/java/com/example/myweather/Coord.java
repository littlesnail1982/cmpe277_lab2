package com.example.myweather;

/**
 * Created by tubaozi on 10/31/17.
 */

public class Coord {
    double lat;
    double lon;
    public Coord(double lat, double lon){
        this.lat=lat;
        this.lon=lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

}
