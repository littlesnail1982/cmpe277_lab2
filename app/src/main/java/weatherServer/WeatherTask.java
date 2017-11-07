package weatherServer;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by tubaozi on 10/30/17.
 */
public class WeatherTask{
    CurrentWeather currentWeather=null;
    List<HourWeather> hoursWeather=null;

    // get current temp by Coordinates
    public CurrentWeather getCurrentWeatherByCoordinates(String stream) throws IOException, JSONException {
        CurrentWeather currentWeather = new CurrentWeather();
        JSONObject json = null;

        try {
            json = new JSONObject(stream);
            //get temprature info
            String cityName=json.getString("name");
            currentWeather.setCityName(cityName);

            long timestamp=json.getLong("dt");
            currentWeather.setTimestamp(timestamp);
            JSONObject item = json.getJSONObject("main");
            if (!item.equals(null)) {
                if (!item.get("temp").equals(null)) {
                    double currentTempK = item.getDouble("temp");
                    double minTempK = item.getDouble("temp_min");
                    double maxTempK = item.getDouble("temp_max");
                    currentWeather.setTemperatureInK(currentTempK);
                    currentWeather.setMin_Temperature(minTempK);
                    currentWeather.setMax_Temperature(maxTempK);
                }
            }
            //get Weather Info
            JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
            if (!weather.equals(null)) {
                if (!weather.getString("main").equals(null)) {
                    String weather_main = weather.getString("main");
                    currentWeather.setWeather(weather_main);

                }
            }
            this.currentWeather=currentWeather;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return currentWeather;
    }


    // Get weather information for next 5 days per 3hours by Coordinates
    public List<HourWeather> getWeatherForcastPer3HoursNext5ddaysByCoordinates(String stream) throws IOException, JSONException {
        List<HourWeather> hw=null;
        JSONObject json = null;
        try {
            json = new JSONObject(stream);
            JSONArray infoList = json.getJSONArray("list");
            int len=infoList.length();
            hw=new ArrayList<HourWeather>(len);
            for (int i = 0; i <len ; i++) {
                JSONObject info = infoList.getJSONObject(i);
                long timestamp=info.getLong("dt");
                JSONObject temperatureList = info.getJSONObject("main");  //main weather
                double temperatureK = temperatureList.getDouble("temp");   //temperatuere
                JSONObject weatherInfo = info.getJSONArray("weather").getJSONObject(0);
                String weather = weatherInfo.getString("main");
                hw.add(new HourWeather(weather,temperatureK,timestamp));
            }
            this.hoursWeather=hw;
            return hw;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hw;
    }

}
