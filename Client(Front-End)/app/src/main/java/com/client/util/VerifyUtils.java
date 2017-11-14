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

public class VerifyUtils {

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
                    int age = (int) jsonObject.get("age");
                    String sex = (String) jsonObject.get("sex");
                    int height = (int) jsonObject.get("height");
                    int weight = (int) jsonObject.get("weight");

                    map.put("age",String.valueOf(age));
                    map.put("sex",sex);
                    map.put("height",String.valueOf(height));
                    map.put("weight",String.valueOf(weight));

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

