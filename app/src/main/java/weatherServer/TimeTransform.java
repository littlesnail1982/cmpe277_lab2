package weatherServer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by tubaozi on 11/3/17.
 */

public class TimeTransform {
    public Date getTimeForSepcificTimeZone(long timestamp, String timezone){
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

        Date date = new Date(timestamp*1000L);
        format.setTimeZone(TimeZone.getTimeZone(timezone));
        String date1=format.format(date);
        return date;
    }

}
