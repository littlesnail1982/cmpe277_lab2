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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Xing on 11/3/2017.
 */

public class CityListAdapter extends BaseAdapter {

    private ArrayList<String> cityList;
    private ArrayList<String> localTimeList;
    private ArrayList<String> weatherList;

    private Context context;

    private LayoutInflater inflater = null;

    private SharedPreferences sp;

    private double timeStamp= 0;
    private boolean timeZoneTaskFinished = false;

    public CityListAdapter(ArrayList<String> list, Context context) {
        this.context = context;
        this.cityList = list;
        inflater = LayoutInflater.from(context);
        localTimeList = new ArrayList<>();
        weatherList = new ArrayList<>();
        sp = context.getSharedPreferences("data", MODE_PRIVATE);

        initData();
    }

    private void initData(){
        for(int i=0; i<cityList.size(); i++){
            String[] location = sp.getString(cityList.get(i),null).split(",");
            addCity(location[0],location[1]);
        }
    }

    private void addCity(String lat, String lng) {
/*        timeZoneTaskFinished = false;
        timeStamp = Calendar.getInstance().getTimeInMillis()/1000;
        URL timeZoneSearchUrl = DateTimeNetUtils.buildUrl(Double.toString(lat), Double.toString(lng), timeStamp);
        new TimeZoneQueryTask().execute(timeZoneSearchUrl);
        URL weatherSearchUrl = NetworkUtils.buildUrl(Double.toString(lat), Double.toString(lng));
        new WeatherQueryTask().execute(weatherSearchUrl); */


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CityListAdapter.ViewHolder holder = null;
        if (convertView == null) {

            holder = new CityListAdapter.ViewHolder();

            convertView = inflater.inflate(R.layout.list_view_item, null);
            holder.cityName = (TextView) convertView.findViewById(R.id.city_name);
            holder.localTime = (TextView) convertView.findViewById(R.id.local_time);
            holder.weather = (TextView) convertView.findViewById(R.id.weather);

            convertView.setTag(holder);
        } else {

            holder = (CityListAdapter.ViewHolder) convertView.getTag();
        }

        holder.cityName.setText(cityList.get(position));
        holder.localTime.setText(localTimeList.get(position));
        holder.weather.setText(weatherList.get(position));

        return convertView;
    }

    @Override
    public int getCount() {
        return cityList.size();
    }

    @Override
    public Object getItem(int position) {
        return cityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public static class ViewHolder {
        TextView cityName, localTime, weather;
    }

/*    public class TimeZoneQueryTask extends AsyncTask<URL, Void, String> {

        // Override the doInBackground method to perform the query. Return the results.
        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String tiumeZoneResults = null;
            try {
                tiumeZoneResults = DateTimeNetUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tiumeZoneResults;
        }

        // Override onPostExecute to display the results in the TextView
        @Override
        protected void onPostExecute(String timezoneResults) {
            if (timezoneResults != null && !timezoneResults.equals("")) {
                mTimeZone = TimeZoneJsonPaser.parseJson(timezoneResults);
                timeZoneTaskFinished = true;
            }
        }
    }

    public class WeatherQueryTask extends AsyncTask<URL, Void, String> {

        // Override the doInBackground method to perform the query. Return the results.
        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String weatherResults = null;
            try {
                weatherResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return weatherResults;
        }

        // Override onPostExecute to display the results in the TextView
        @Override
        protected void onPostExecute(String weatherResults) {
            if (weatherResults != null && !weatherResults.equals("")) {
                mWeather = WeatherJsonPaser.parseJson(weatherResults);
                while( timeZoneTaskFinished == false )
                {
                    try
                    {
                        Log.d("myApp", "Waiting for timeZone task finish");
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                //mWeatherResultsTextView.setText(weatherResults);

                City curCity = new City(mWeather, mCurrentCity, mLat, mLon, mTimeZone);
                mAdapter.addNewCitry(curCity);
                mAdapter.notifyDataSetChanged();
            }
        }
    } */
}
