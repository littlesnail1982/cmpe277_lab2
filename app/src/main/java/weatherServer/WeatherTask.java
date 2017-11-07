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
    List<DayWeather> daysWeather=null;
    List<HourWeather> hoursWeather=null;
    String timeZoneId=null;

    // get current temp by Coordinates
    public CurrentWeather getCurrentWeatherByCoordinates(String stream) throws IOException, JSONException {
        /*
        WeatherToken wk = new WeatherToken();
        String url = wk.currentWeatherApiRequest(coord.getLat(), coord.getLon());
        Helper helper = new Helper();
        String stream = helper.getHTTPData(url);
        */
        return getCurrentWeather(stream);
    }


    // Get weather information for next 24 hours per 3hours by Coordinates
    public List<HourWeather> getWeatherForcastPer3HoursNext24HoursByCoordinates(String stream) throws IOException, JSONException {
        /*
        WeatherToken wk = new WeatherToken();
        String url = wk.forcustWeatherApiRequest(coord.getLat(), coord.getLon());
        Helper helper = new Helper();
        String stream = helper.getHTTPData(url);
        */
        return getWeatherInfoPer3HoursNext24Hours(stream);
    }

    // Get weather information for next 5 days
    public List<DayWeather> getNext5DaysWeatherForcastByCoordinates(String stream) throws IOException, JSONException {
        /*
        WeatherToken wk = new WeatherToken();
        String url = wk.forcustWeatherApiRequest(coord.getLat(), coord.getLon());

        Helper helper = new Helper();
        String stream = helper.getHTTPData(url);
        */
        return getWeatherInfoNext5Days(stream);
    }

    //Get current information
    private CurrentWeather getCurrentWeather(String stream) {
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


            //Get timezone with coordinates
            JSONObject coordObject=json.getJSONObject("coord");
            Double lat=coordObject.getDouble("lat");
            Double lon=coordObject.getDouble("lon");
            Coord coord=new Coord(lat,lon);

           // new GetTimeZoneIdTask().execute(TimeZoneToken.currentTimeZonerApiRequest(lat,lon,timestamp));

            //currentWeather.setTimezoneId(timeZoneId);

            this.currentWeather=currentWeather;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return currentWeather;
    }

/*

    private class GetTimeZoneIdTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String url = params[0];
            Helper helper = new Helper();
            stream = helper.getHTTPData(url);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GetTimeZoneTask gtzt=new GetTimeZoneTask();
            timeZoneId=gtzt.getTimeZone(s);
        }
    }
*/


    // Get weather information for next 24 hours per 3hours
    private List<HourWeather> getWeatherInfoPer3HoursNext24Hours(String stream)  {
        List<HourWeather> hw=null;
        JSONObject json = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        try {
            json = new JSONObject(stream);
            JSONArray jsonArray = new JSONArray();
            JSONArray infoList = json.getJSONArray("list");
            int len=infoList.length()>=8? 8: infoList.length();
            hw=new ArrayList<HourWeather>(len);
            for (int i = 0; i < len; i++) { //get weather info for next 24 hours
                JSONObject obj = new JSONObject();
                JSONObject info = infoList.getJSONObject(i);
                String time=info.getString("dt_txt");
                long timestamp=info.getLong("dt");
                int hour=0;
                try {
                    calendar.setTime(simpleDateFormat.parse(time));
                    hour=calendar.get(Calendar.HOUR_OF_DAY);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


                //temperature info
                JSONObject temperatureList = info.getJSONObject("main");
                double temperatureK = temperatureList.getDouble("temp");

                //weather info
                JSONObject weatherList = info.getJSONArray("weather").getJSONObject(0);
                String weather = weatherList.getString("main");
                hw.add(new HourWeather(time,weather,temperatureK,timestamp));
            }
            this.hoursWeather=hw;
            return hw;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return hw;
    }


    //Get the weather info for next 5 days
    /*
    date:
    temperature around noon of each day
    weather status around noon of each day
     */
    private List<DayWeather> getWeatherInfoNext5Days(String stream)  {
        List<DayWeather> result=new ArrayList<>();
        JSONObject json = null;
        //format of info
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1); //tomorrow
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int j=0;
        try {
            json = new JSONObject(stream);

            //json=new JSONObject(stream.substring(stream.indexOf("{"), stream.lastIndexOf("}") + 1));
            JSONArray infoList = json.getJSONArray("list");
            double minTemp = Double.MAX_VALUE;
            double maxTemp = Double.MIN_VALUE;
            for (int i = 0; i < infoList.length(); i++) {
                JSONObject obj = new JSONObject();
                JSONObject info = infoList.getJSONObject(i);
                //update the temperature
                String time = info.getString("dt_txt");
                JSONObject temperatureList = info.getJSONObject("main");
                double temperatureK = temperatureList.getDouble("temp");
                minTemp = Math.min(minTemp, temperatureK);
                maxTemp = Math.max(maxTemp, temperatureK);
                //Store the info of the next 5 days at noon
                String temp1=sdf.format(calendar.getTime());
                if (time.equals(sdf.format(calendar.getTime()) + " 12:00:00") && j<5) {
                    JSONObject weatherList = info.getJSONArray("weather").getJSONObject(0);

                    SimpleDateFormat weekDayFormat = new SimpleDateFormat("EEEE"); //Day of week, such as Monday
                    String weekDay=weekDayFormat.format(calendar.getTime());
                    String weather = weatherList.getString("main");
                    DayWeather temp=new DayWeather(sdf.format(calendar.getTime()),minTemp,maxTemp,weather); //不是最高最低
                    result.add(temp);
                    calendar.add(Calendar.DAY_OF_YEAR, 1); //plus one day
                    j++;
                }
            }

            this.daysWeather=result;
            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


}
