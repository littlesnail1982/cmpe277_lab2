package weatherServer;

import java.util.Date;

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
    public double getTempInK() {
        return tempInK;
    }
    public void setTempInK(double tempInK) {
        this.tempInK = tempInK;
    }
    public HourWeather(String weather, double tempInK, long timestamp){
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
    public Date getDate() {return date;}
    public void setDate(Date date) {this.date = date;}
    public int getHour() {return hour;}
    public void setHour(int hour) {this.hour = hour;}

    long timestamp;
    double tempInK;
    String weather;
    int hour;
    Date date;


}
