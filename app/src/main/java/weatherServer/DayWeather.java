package weatherServer;

import java.util.Date;

/**
 * Created by tubaozi on 10/31/17.
 */
public class DayWeather {

    public DayWeather(Date date, double min_temp, double max_temp, String weather, String dateofWeek){
        this.min_temp=min_temp;
        this.max_temp=max_temp;
        this.weather=weather;
        this.date=date;
        this.dateofWeek=dateofWeek;
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




    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    Date date;
    double min_temp;
    double max_temp;
    String weather;

    public String getDateofWeek() {
        return dateofWeek;
    }

    public void setDateofWeek(String dateofWeek) {
        this.dateofWeek = dateofWeek;
    }

    String dateofWeek;


}
