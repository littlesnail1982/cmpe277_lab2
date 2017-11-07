package weatherServer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by tubaozi on 11/3/17.
 */

public class TimeTransform {
    public Date getDate(long timestamp, String timezone){
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Date date = new Date(timestamp*1000L);
        format.setTimeZone(TimeZone.getTimeZone(timezone));
        return date;
    }

    public int getHour(long timestamp, String timezone){
        Date todayDate=getDate(timestamp,timezone);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(todayDate);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
       return hour;

    }

    public int getDay(long timestamp, String timezone){
        Date todayDate=getDate(timestamp,timezone);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(todayDate);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        return hour;

    }


}
