package com.example.myweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import weatherServer.CurrentWeather;
import weatherServer.GetTimeZoneTask;
import weatherServer.Helper;
import weatherServer.TimeTransform;
import weatherServer.TimeZoneToken;
import weatherServer.UnitConverter;
import weatherServer.WeatherTask;
import weatherServer.WeatherToken;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Xing on 11/3/2017.
 */

public class CityListAdapter extends BaseAdapter {

    private String[] cityList;
    private ArrayList<String> localTimeList;
    private ArrayList<String> weatherList;

    private Map<String, String> timeZoneMap;
    private Map<Integer, Boolean> cityWeatherInfo;

    private Context context;

    private LayoutInflater inflater = null;

    private SharedPreferences sp;

  //  private long timeStamp= 0;

    private WeatherTask weatherTask;
    private TimeTransform timeTransform;
    private UnitConverter unitConverter;
    private String unit;


    public CityListAdapter(String[] list, Context context) {
        this.context = context;
        this.cityList = list;
        inflater = LayoutInflater.from(context);
        weatherTask = new WeatherTask();
        timeTransform=new TimeTransform();
        unitConverter = new UnitConverter();

        localTimeList = new ArrayList<>();
        weatherList = new ArrayList<>();
        timeZoneMap = new HashMap<>();
        cityWeatherInfo = new HashMap<>();

        sp = context.getSharedPreferences("data", MODE_PRIVATE);
        unit = sp.getString("unit",null);

        initData();
    }

    private void initData(){

        for(int i = 0; i< cityList.length; i++){
            String[] location = sp.getString(cityList[i],null).split(",");
            addCity(i,location[0],location[1]);
        }
    }

    private void addCity(int pos, String lat, String lng) {

        new GetTimeZone().execute(TimeZoneToken.currentTimeZonerApiRequest(Double.valueOf(lat),Double.valueOf(lng),0),String.valueOf(pos));
        new GetCurrentWeather().execute(WeatherToken.currentWeatherApiRequest(Double.valueOf(lat),Double.valueOf(lng)),String.valueOf(pos));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CityListAdapter.ViewHolder holder = null;
        if (convertView == null) {

            holder = new CityListAdapter.ViewHolder();

            convertView = inflater.inflate(R.layout.city_list_item, null);
            holder.cityName = (TextView) convertView.findViewById(R.id.city_name);
            holder.localTime = (TextView) convertView.findViewById(R.id.local_time);
            holder.weather = (TextView) convertView.findViewById(R.id.weather);

            convertView.setTag(holder);
        } else {

            holder = (CityListAdapter.ViewHolder) convertView.getTag();
        }

        while(cityWeatherInfo.size() != cityList.length)
        {
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        holder.cityName.setText(cityList[position]);
        holder.localTime.setText(localTimeList.get(position));
        holder.weather.setText(weatherList.get(position));

        return convertView;
    }

    @Override
    public int getCount() {
        return cityList.length;
    }

    @Override
    public Object getItem(int position) {
        return cityList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public static class ViewHolder {
        TextView cityName, localTime, weather;
    }

    private class GetTimeZone extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            String stream = null;
            String url = params[0];
            Helper helper = new Helper();
            stream = helper.getHTTPData(url);
            GetTimeZoneTask gtzt=new GetTimeZoneTask();
//            timeZoneMap.put(params[1], gtzt.getTimeZoneOffset(stream));
            timeZoneMap.put(params[1], gtzt.getTimeZone(stream));
            return  null;
        }

/*        @Override
        protected void onPostExecute(String s) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
//            String[] temp = s.split("`");
            GetTimeZoneTask gtzt=new GetTimeZoneTask();
//            timeZoneMap.put(temp[0],gtzt.getTimeZone(temp[1]));
            timeZoneMap.put("0", gtzt.getTimeZone(s));
        } */
    }

    private class GetCurrentWeather extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            String stream = null;
            String url = params[0];
            Helper helper = new Helper();
            stream = helper.getHTTPData(url);
            try {
                CurrentWeather currentWeather = weatherTask.getCurrentWeatherByCoordinates(stream);
                while(timeZoneMap.get(params[1]).isEmpty()){
                    try{
                        Log.d("Hours Weather","Waiting for the task to get the time zone");
                        Thread.sleep(100);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }

                //Transform the time to local time for current weather and put into Adapter
                if(currentWeather!=null){

                    Long timestamp = Calendar.getInstance().getTimeInMillis()/1000;
                    Date date=timeTransform.getDate(timestamp,timeZoneMap.get(params[1]));
                    String DATE_FORMAT = "MM/dd/yyyy HH:mm";
                    DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
                    String localtime = formatter.format(date);

                    double tempInK = currentWeather.getTemperatureInK();
                    String temperature;
                    if (unit.equals("C")) {
                        temperature = Integer.toString((int)unitConverter.Kelvin2Celsius(tempInK)) + (char) 0x00B0;
                    } else {
                        temperature = Integer.toString((int) unitConverter.Kelvin2Farenheit(tempInK)) + (char) 0x00B0;
                    }
                    int pos = Integer.valueOf(params[1]);
                    localTimeList.add(pos, localtime);
                    weatherList.add(pos, temperature);
                    cityWeatherInfo.put(pos, true);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

/*        @Override
        protected void onPostExecute(String s) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            try {
                String[] temp = s.split("`");
                CurrentWeather currentWeather = weatherTask.getCurrentWeatherByCoordinates(temp[1]);
                while(timeZoneMap.get(temp[0]).isEmpty()){
                    try{
                        Log.d("Hours Weather","Waiting for the task to get the time zone");
                        Thread.sleep(50);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }

                //Transform the time to local time for current weather and put into Adapter
                if(currentWeather!=null){
                    long timestamp = Calendar.getInstance().getTimeInMillis()/1000;
                    Date date=timeTransform.getDate(timestamp,timeZoneMap.get(temp[0]));
                    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM); //显示日期，时间（精确到分）
                    String localtime = df.format(date);

                    double tempInK = currentWeather.getTemperatureInK();
                    String temperature;
                    if (unit.equals("C")) {
                        temperature = Integer.toString((int)unitConverter.Kelvin2Celsius(tempInK));
                    } else {
                        temperature = Integer.toString((int) unitConverter.Kelvin2Farenheit(tempInK));
                    }
                    int pos = Integer.valueOf(temp[0]);
                    localTimeList.add(pos, localtime);
                    weatherList.add(pos, temperature);
                    cityWeatherInfo.put(pos, true);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }
}
