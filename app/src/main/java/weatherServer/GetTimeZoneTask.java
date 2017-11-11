package weatherServer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tubaozi on 11/3/17.
 */

public class GetTimeZoneTask {

    public String getTimeZone(String stream){
        String timeZoneId = null;
        try {
            JSONObject json = new JSONObject(stream);
            timeZoneId=json.getString("timeZoneId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return timeZoneId;
    }

    public String getTimeZoneOffset(String stream){
        String timeZoneOffset = null;
        try {
            JSONObject json = new JSONObject(stream);
            timeZoneOffset=json.getString("rawOffset");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return timeZoneOffset;
    }
}
