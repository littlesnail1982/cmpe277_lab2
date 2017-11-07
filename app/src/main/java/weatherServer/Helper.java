package weatherServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by tubaozi on 10/30/17.
 */
public class Helper {
    static String stream=null;
    public Helper(){}

    public String getHTTPData(String url){
        try {
            URL u= new URL(url);
            HttpURLConnection httpURLConnection=(HttpURLConnection) u.openConnection();
            System.out.println("ddddd");
            if(httpURLConnection.getResponseCode()==200){
                System.out.println("Connect Successfully");
                BufferedReader rd = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), Charset.forName("UTF-8")),8);

                StringBuilder sb = new StringBuilder();
                String cp;
                while ((cp = rd.readLine()) != null) {
                    sb.append(cp);
                }

                stream=sb.toString();
                httpURLConnection.disconnect();
            }
            else{
                System.out.println("66666");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stream=stream;
        return stream;
    }



    private static String readAll(BufferedReader rd) throws IOException {

        StringBuilder sb = new StringBuilder();
        String cp;
        while ((cp = rd.readLine()) != null) {
            sb.append(cp);
        }
        return sb.toString();
    }


}
