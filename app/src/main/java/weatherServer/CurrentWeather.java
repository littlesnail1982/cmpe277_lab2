package weatherServer;

/**
 * Created by tubaozi on 10/30/17.
 */
public class CurrentWeather {


    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    //String date;
    String cityName;
    double TemperatureInK;
    double min_Temperature; //the min temp for the location
    double max_Temperature; //max temp for the location
    String weather; //cloud or the others
    long timestamp;
    String weekOfDay;


    public String getWeekOfDay() {return weekOfDay;}
    public void setWeekOfDay(String weekOfDay) {this.weekOfDay = weekOfDay;}
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public CurrentWeather( ){}

    public CurrentWeather(String cityName,double TempeatureInK, double min_Temperature, double max_Temperature, String weather){
        this.cityName=cityName;
        this.TemperatureInK=TempeatureInK;
        this.min_Temperature=min_Temperature;
        this.max_Temperature=max_Temperature;
        this.weather=weather;
    }

    public double getTemperatureInK() {
        return TemperatureInK;
    }
    public void setTemperatureInK(double temperatureInK) {
        TemperatureInK = temperatureInK;
    }

    public double getMin_Temperature() {
        return min_Temperature;
    }

    public void setMin_Temperature(double min_Temperature) {this.min_Temperature = min_Temperature;}

    public double getMax_Temperature() {
        return max_Temperature;
    }

    public void setMax_Temperature(double max_Temperature) {this.max_Temperature = max_Temperature;}
    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
    public String getCityName() {
        return cityName;
    }


}
