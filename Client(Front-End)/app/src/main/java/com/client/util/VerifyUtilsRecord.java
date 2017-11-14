package com.ece651group8.uwaterloo.ca.ece_651_group8.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxuanqi on 16/11/16.
 */

public class VerifyUtilsRecord {

    public static Map<String,String> verify(Context context, String urlString, String authorization) throws UnsupportedEncodingException {
        Map<String,String> map = new HashMap<>();
        HttpURLConnection connection =null;

        try{

            URL url = new URL(urlString);
            connection = (HttpURLConnection)url.openConnection();

            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestProperty("Authorization","token "+authorization);

            int code = connection.getResponseCode();
            switch (code){
                case 200:
                    map.put("code","200");

                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);


                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    String jsonString = stringBuffer.toString();


                    JSONObject jsonObject = new JSONObject(jsonString);

                    JSONArray bloodAddress= (JSONArray)jsonObject.get("blood_pressure_records");
                    List<Integer> highBloodPressure = new ArrayList<>();
                    List<Integer> lowBloodPressure = new ArrayList<>();
                    List<String> datetimePressure = new ArrayList<>();
                    map.put("bloodlength",String.valueOf(bloodAddress.length()));

                    for(int i=0; i<bloodAddress.length();i++) {
                        String string = (String) bloodAddress.opt(i);
                        JSONObject bloodGet = AddressUtil.address(string,authorization);

                        highBloodPressure.add(bloodGet.getInt("high"));
                        lowBloodPressure.add(bloodGet.getInt("low"));
                        datetimePressure.add(bloodGet.getString("time"));

                        map.put("highBloodPressure"+i,String.valueOf(highBloodPressure.get(i)));
                        map.put("lowBloodPressure"+i,String.valueOf(lowBloodPressure.get(i)));
                        map.put("datePressure"+i,String.valueOf(datetimePressure.get(i)));

                    }



                    JSONArray rateAddress= (JSONArray)jsonObject.get("heart_rates");
                    List<Integer> heartRate = new ArrayList<>();
                    List<String> datetimeHeartRate = new ArrayList<>();
                    map.put("heartlength",String.valueOf(rateAddress.length()));

                    for(int i=0; i<rateAddress.length();i++) {
                        String string = (String) rateAddress.opt(i);
                        JSONObject rateGet = AddressUtil.address(string,authorization);
                        heartRate.add(rateGet.getInt("rate"));
                        datetimeHeartRate.add(rateGet.getString("time"));

                        map.put("heartRate"+i,String.valueOf(heartRate.get(i)));
                        map.put("datetimeHeartRate"+i,String.valueOf(datetimeHeartRate.get(i)));


                    }

                    JSONArray commentAddress= (JSONArray)jsonObject.get("comments");
                    List<String> comments = new ArrayList<>();
                    List<String> datetimeComments = new ArrayList<>();
                    map.put("commentlength",String.valueOf(commentAddress.length()));

                    for(int i=0; i<commentAddress.length();i++) {
                        String string = (String) commentAddress.opt(i);
                        JSONObject commentGet = AddressUtil.address(string,authorization);
                        comments.add(commentGet.getString("comment"));
                        datetimeComments.add(commentGet.getString("time"));

                        map.put("comments"+i,String.valueOf(comments.get(i)));
                        map.put("datetimeComments"+i,String.valueOf(datetimeComments.get(i)));
                    }
                break;
                case 400:
                    map.put("code","400");
                    break;
            }
            return map;
        }catch(Exception e){
            e.printStackTrace();
            map.put("code","404");
            return map;
        }finally {
            if (connection!=null){
                connection.disconnect();
            }
        }
    }
}

