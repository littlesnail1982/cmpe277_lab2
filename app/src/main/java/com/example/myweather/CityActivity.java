package com.example.myweather;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import weatherServer.Coord;
import weatherServer.CurrentWeather;
import weatherServer.DayWeather;
import weatherServer.GetTimeZoneTask;
import weatherServer.Helper;
import weatherServer.HourWeather;
import weatherServer.HourWeatherToDayWeather;
import weatherServer.TimeTransform;
import weatherServer.TimeZoneToken;
import weatherServer.UnitConverter;
import weatherServer.WeatherTask;
import weatherServer.WeatherToken;

public class CityActivity extends AppCompatActivity {
    List<DayWeather> daysWeather;
    CurrentWeather currentWeather;
    GridView gridView;
    ListView listView;
    TextView cityNameView;
    TextView weatherView;
    TextView temperatureView;
    TextView todayDateView;
    TextView currentMinView;
    TextView currentMaxView;
    TextView whetherHereView;
    TextView nowView;
    SharedPreferences sp;
    WeatherTask weatherTask;
    Context context;
    private List<HourWeather> hoursWeather;  //the weather information for the next 5days
    private float x1, x2;
    static final int MIN_DISTANCE = 150;
    private UnitConverter unitConverter = new UnitConverter();
    private String unit;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 1;
    private boolean gpsStatus;
    private LocationListener locationListener = null;
    private LocationManager locationManager = null;
    private boolean timeZoneTaskFinished = false; //mark whether the task to get the time zone is finished
    private TimeTransform timeTransform;
    private int cityNumber; //how many cities users have set
    private int number; //the position of the city
    private Coord coord; //the coordination of the city
    private String timezoneId; //the timezone id of the city
    private String cityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //the up button
        listView = (ListView) findViewById(R.id.day_weather_list);
        cityNameView = (TextView) findViewById(R.id.cityName);
        weatherView = (TextView) findViewById(R.id.weather);
        temperatureView = (TextView) findViewById(R.id.temperature);
        gridView = (GridView) findViewById(R.id.hour_weather_list);
        todayDateView = (TextView) findViewById(R.id.today_date);
        currentMinView = (TextView) findViewById(R.id.currentMin);
        currentMaxView = (TextView) findViewById(R.id.currentMax);
        nowView = (TextView) findViewById(R.id.now);
        context = this;
        whetherHereView = (TextView) findViewById(R.id.whether_here);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                //Intent intent=new Intent(Settings.ACTION_LOCALE_SETTINGS);
                //intent.putExtra("number", number);
                //startActivity(intent);
            }
        };


        number = getIntent().getIntExtra("number", 0); //the position of the targed city
        getEnvironment(); //get the values in sharedPreference
        getLocation();


        timeTransform = new TimeTransform();


        //getCurrentByGPS(); //To verify the current location of the user now
        //coord=new Coord(5,10);

        weatherTask = new WeatherTask();
        //get the the time zone of the local city
        new GetTimeZone().execute(TimeZoneToken.currentTimeZonerApiRequest(coord.getLat(), coord.getLon(), 0));
        //Get hour weather
        new GetHoursWeather().execute(WeatherToken.forcustWeatherApiRequest(coord.getLat(), coord.getLon()));
        //set the current weather
        new GetCurrentWeather().execute(WeatherToken.currentWeatherApiRequest(coord.getLat(), coord.getLon()));
    }


    public void getLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, MY_PERMISSION_REQUEST_LOCATION);
                return;
            }else{

                getCurrentLocation();
            }
        }else{
            getCurrentLocation();
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // Toast.makeText(CityActivity.this, "PERMISSION granted", Toast.LENGTH_SHORT).show();
                    /*
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getCurrentLocation();
                    }*/
                    getLocation();
                }
                return;
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /*
    For Gps get the current location
     */

    //@RequiresApi(api = Build.VERSION_CODES.M)
    public void getCurrentLocation() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}, MY_PERMISSION_REQUEST_LOCATION);
                return;
            }
        }
        locationManager.requestLocationUpdates("gps", 5000, 500, locationListener); //every 5000millison，500m change will be detected
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        try {
            currentCity(location.getLatitude(), location.getLongitude());
           // Toast.makeText(CityActivity.this, "Found Location" + currentCity(location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(CityActivity.this, "GPS NOT FOUND", Toast.LENGTH_SHORT).show();
        }
    }

    /*1: get the coord of the desired unit*/
    public void getEnvironment() {
        //check whether there is the value
        /*
        number = sp.getInt("number", -1);
        if (number == -1) {
            number = 0;
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("number", 0);
            editor.commit();
        }
        */
        try{
            sp = getSharedPreferences("data", MODE_PRIVATE);
            Set<String> cities = sp.getStringSet("cities", null);
            String[] coordArr = new String[]{"0", "0"};
            if (cities != null) {
                String[] cityData = cities.toArray(new String[0]);
                cityNumber=cityData.length;
                if (number < cityData.length && number>=0) { //the unit Number
                    cityName=cityData[number];
                    String coordStr = sp.getString(cityData[number], null);
                    coordArr = coordStr.split(",");
                }
            }
            coord = new Coord(Double.parseDouble(coordArr[0]), Double.parseDouble(coordArr[1]));

            //
            unit = sp.getString("unit", "K");
            if (unit.equals("K")) {
                unit = "C";
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("unit", unit);
                editor.commit();
            }

        }catch (Exception e){
            Log.d("SharedPreference","Missed Date in SharedPreference");
        }
    }




    /* 2: Get the timezone of the city */
    private class GetTimeZone extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            String stream = null;
            String url = params[0];
            Helper helper = new Helper();
            stream = helper.getHTTPData(url);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            super.onPostExecute(s);
            GetTimeZoneTask gtzt=new GetTimeZoneTask();
            timezoneId=gtzt.getTimeZone(s);
            timeZoneTaskFinished=true;
        }
    }


    /*3: Get hour info for 5 days*/
    private class GetHoursWeather extends AsyncTask<String, Void, String> {
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
            try {
                hoursWeather = weatherTask.getWeatherForcastPer3HoursNext5ddaysByCoordinates(s);
                while(timeZoneTaskFinished==false){
                    try{
                        Log.d("Hours Weather","Waiting for the task to get the time zone");
                        Thread.sleep(100);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }

                //Transform the time to local time for hour weather
                if(hoursWeather!=null && hoursWeather.size()>0){
                    //change the time format
                    for (int i=0;i<hoursWeather.size();i++){
                        long timestamp=hoursWeather.get(i).getTimestamp();
                        int hour=timeTransform.getHour(timestamp,timezoneId);
                        Date date=timeTransform.getDate(timestamp,timezoneId);
                        hoursWeather.get(i).setDate(date);
                        hoursWeather.get(i).setHour(hour);
                    }
                }

                //put into the adapter
                HourWeatherAdapter hourWeatherAdapter=new HourWeatherAdapter();
                gridView.setAdapter(hourWeatherAdapter);  //show the hour weather
                hourWeatherAdapter.notifyDataSetChanged();
                daysWeather=new HourWeatherToDayWeather().GetDayWeather(hoursWeather);
                DayWeatherAdapter dayWeatherAdapter=new DayWeatherAdapter();
                listView.setAdapter(dayWeatherAdapter);  //show the day weather
                dayWeatherAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /* 4: Hour Show in front task */
    public class HourWeatherAdapter extends BaseAdapter {

        @Override
        //public int getCount() {return days.length;}
        public int getCount() {
            return hoursWeather.size()>8?8:hoursWeather.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View v, ViewGroup parent) {
            v = getLayoutInflater().inflate(R.layout.hourweatherlistlayout, null);
            TextView timeView = (TextView) v.findViewById(R.id.time);
            TextView temperatureView = (TextView) v.findViewById(R.id.temperature);
            TextView weatherView = (TextView) v.findViewById(R.id.weather);

            timeView.setText("" + hoursWeather.get(i).getHour());
            weatherView.setText(hoursWeather.get(i).getWeather());
            double temperature = hoursWeather.get(i).getTempInK();
            if (unit.equals("C")) {
                temperatureView.setText("" + (int) unitConverter.Kelvin2Celsius(temperature) + (char) 0x00B0); ////Unit of Temper is + (char) 0x00B0
            } else {
                temperatureView.setText("" + (int) unitConverter.Kelvin2Farenheit(temperature) + (char) 0x00B0); ////Unit of Temper is + (char) 0x00B0
            }
            return v;
        }
    }

    /*5: DayWeather Adapter*/

    class DayWeatherAdapter extends BaseAdapter {
        @Override
        //public int getCount() {return days.length;}
        public int getCount() {
            return daysWeather.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.dayweatherlistlayout, null);
            TextView day = (TextView) convertView.findViewById(R.id.day);
            TextView weather = (TextView) convertView.findViewById(R.id.weather);
            TextView minTemp = (TextView) convertView.findViewById(R.id.minTemp);
            TextView maxTemp = (TextView) convertView.findViewById(R.id.maxTemp);

            day.setText(daysWeather.get(i).getDateofWeek());
            weather.setText(daysWeather.get(i).getWeather());
            double minTempInK = daysWeather.get(i).getMin_temp();
            double maxTempInK = daysWeather.get(i).getMax_temp();
            if (unit.equals("C")) {
                minTemp.setText("" + (int) unitConverter.Kelvin2Celsius(minTempInK) + (char) 0x00B0); ////Unit of Temper is + (char) 0x00B0
                maxTemp.setText("" + (int) unitConverter.Kelvin2Celsius(maxTempInK) + (char) 0x00B0);
            } else {
                minTemp.setText("" + (int) unitConverter.Kelvin2Farenheit(minTempInK) + (char) 0x00B0); ////Unit of Temper is + (char) 0x00B0
                maxTemp.setText("" + (int) unitConverter.Kelvin2Farenheit(maxTempInK) + (char) 0x00B0);
            }
            return convertView;
        }
    }

    /* 6: Get current weather */
    private class GetCurrentWeather extends AsyncTask<String, Void, String> {
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
            try {
                currentWeather = weatherTask.getCurrentWeatherByCoordinates(s);
                while(timeZoneTaskFinished==false){
                    try{
                        Log.d("Hours Weather","Waiting for the task to get the time zone");
                        Thread.sleep(100);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }

                //Transform the time to local time for current weather and put into Adapter
                if(currentWeather!=null){
                    long timestamp=currentWeather.getTimestamp();
                    Date date=timeTransform.getDate(timestamp,timezoneId);
                    DateFormat format=new SimpleDateFormat("EEEE MMM d");
                    todayDateView.setText(format.format(date));
                    weatherView.setText(currentWeather.getWeather());
                    cityNameView.setText(cityName);
                    nowView.setText("now");

                    double tempInK = currentWeather.getTemperatureInK();
                    double minTempInK = currentWeather.getMin_Temperature();
                    double maxTempInk = currentWeather.getMax_Temperature();

                    if (unit.equals("C")) {
                        temperatureView.setText("" + (int) unitConverter.Kelvin2Celsius(tempInK) + (char) 0x00B0);
                        currentMinView.setText("" + (int) unitConverter.Kelvin2Celsius(minTempInK) + (char) 0x00B0);
                        currentMaxView.setText("" + (int) unitConverter.Kelvin2Celsius(maxTempInk) + (char) 0x00B0);
                    } else {
                        temperatureView.setText("" + (int) unitConverter.Kelvin2Farenheit(tempInK) + (char) 0x00B0);
                        currentMinView.setText("" + (int) unitConverter.Kelvin2Farenheit(minTempInK) + (char) 0x00B0);
                        currentMaxView.setText("" + (int) unitConverter.Kelvin2Farenheit(maxTempInk) + (char) 0x00B0);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //change the unit of temperature
    public String changeUnit() {
        if (unit.equals("C")) {
            unit = "F";
        } else {
            unit = "C";
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("unit", unit);
        editor.commit();
        return unit;
    }

    /*
    //Change the number of city location
    public int changNumber(int i) {
        sp = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Set<String> cities = sp.getStringSet("cities", null);
        number = sp.getInt("number", -1);
        if (number == -1) {
            number = 0;
            editor.putInt("number", 0);
            editor.commit();
        } else {
            if (cities != null && cities.size() > 0) {
                int pos = number + i;
                if (pos >= 0 && pos < cities.size()) {
                    editor.putInt("number", number + i);
                    editor.commit();
                    return number + i;
                }
            }
        }
        return 0;
    }

*/

/* Change the position of the city */
    public int changeNumber(int i){
        number+=i;
        if (number<0 || number>=cityNumber) {  //when the number of the cities is out of range
            number-=i;
        }
        return number;
    }

    /*When user swipe left/right, change the city*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    if (deltaX > 0) changeNumber(1); //left to right
                    else changeNumber(-1); ////right to left
                    changeCity();
                } else {
                    // consider as something else - a screen tap for example
                    Log.d("No Change","The movement is too slight");
                }
                break;
        }
        return super.onTouchEvent(event);
    }



    public void changeCity(){
        getEnvironment(); //get the values in sharedPreference
        getLocation();
        weatherTask = new WeatherTask();
        //get the the time zone of the local city
        new GetTimeZone().execute(TimeZoneToken.currentTimeZonerApiRequest(coord.getLat(),coord.getLon(),0));
        //Get hour weather
        new GetHoursWeather().execute(WeatherToken.forcustWeatherApiRequest(coord.getLat(), coord.getLon()));
        //set the current weather
        new GetCurrentWeather().execute(WeatherToken.currentWeatherApiRequest(coord.getLat(), coord.getLon()));
    }



    //get current city name using geocode according to the Coordinates
    public String currentCity(double lat, double lon){
        String curCity="";
        Geocoder geocoder=new Geocoder(CityActivity.this, Locale.getDefault());
        List<Address> addressList;
        try{
            addressList=geocoder.getFromLocation(lat,lon,1);
            if(addressList.size()>0) curCity=addressList.get(0).getLocality();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        if(cityName.equals(curCity)){
            whetherHereView.setText("You are here now");
        }else{
            whetherHereView.setText("");
        }
        return curCity;
    }

    //Get current Location ---Finish

}
