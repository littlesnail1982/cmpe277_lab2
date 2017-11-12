package weatherServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by tubaozi on 11/6/17.
 */

public class HourWeatherToDayWeather {
    SimpleDateFormat weekDayFormat = new SimpleDateFormat("EEEE");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    TimeTransform timeTransform=new TimeTransform();

    public List<DayWeather> GetDayWeather(List<HourWeather> hourWeatherList,String timezoneId){
        List<DayWeather> result=new ArrayList<>();
        if(hourWeatherList==null || hourWeatherList.size()==0) return result;
        //Get currentTime
        Long timestamp = Calendar.getInstance().getTimeInMillis()/1000;
        Date date=timeTransform.getDate(timestamp,timezoneId);
        //Date date=hourWeatherList.get(0).getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1); //tomorrow
        double minTemp = Double.MAX_VALUE;
        double maxTemp = Double.MIN_VALUE;
        String weather=null;
        int hourOfDate=0;


        for(int i=0;i<hourWeatherList.size();i++){
            String temp=sdf.format(calendar.getTime()); //
            Date tempDate=hourWeatherList.get(i).getDate();
            Calendar calendarTemp = Calendar.getInstance();
            calendarTemp.setTime(tempDate);
            String dateCur=sdf.format(calendarTemp.getTime()); //compare date

            double temperatureK=hourWeatherList.get(i).getTempInK();
            if(dateCur.equals(temp)){
                minTemp = Math.min(minTemp, temperatureK);
                maxTemp = Math.max(maxTemp, temperatureK);
                String timeCur=simpleDateFormat.format(calendarTemp.getTime());
                if(weather==null){
                    weather=hourWeatherList.get(i).getWeather();
                    hourOfDate=calendarTemp.get(Calendar.HOUR_OF_DAY);
                }else{
                    int hourOfDateTemp=calendarTemp.get(Calendar.HOUR_OF_DAY);
                    if(Math.abs(hourOfDate-12)>=Math.abs(hourOfDateTemp-12)){
                        hourOfDate=hourOfDateTemp;
                        weather=hourWeatherList.get(i).getWeather();
                    }
                }
            }else{
                if(calendar.getTime().compareTo(calendarTemp.getTime())<0){
                    String weekDay=weekDayFormat.format(calendar.getTime());
                    result.add(new DayWeather(calendar.getTime(),minTemp,maxTemp,weather,weekDay));
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    minTemp = temperatureK;
                    maxTemp = temperatureK;
                    weather=null;
                }
            }
        }

        //For the day without the full time
        if(weather!=null) {
            String weekDay=weekDayFormat.format(calendar.getTime());
            result.add(new DayWeather(calendar.getTime(),minTemp,maxTemp,weather,weekDay));
        }
        return result;
    }
}