package com.ece651group8.uwaterloo.ca.ece_651_group8.util;


import android.app.DownloadManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ece651group8.uwaterloo.ca.ece_651_group8.bean.Pid;
import com.ece651group8.uwaterloo.ca.ece_651_group8.bean.Token;
import com.ece651group8.uwaterloo.ca.ece_651_group8.db.DatabaseHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


//vertify the user
//If valid, save token in db and return case 200 and token


public class LoginUtils {

    public static Map<String,Integer> login(Context context, String urlString, String username, String password) throws UnsupportedEncodingException {
        Map<String,Integer> map = new HashMap<>();
        String urlParameters = "username="+ URLEncoder.encode(username,"utf-8")+"&password="+URLEncoder.encode(password,"utf-8");
        HttpURLConnection connection =null;




        try{
            URL url = new URL(urlString);
            connection = (HttpURLConnection)url.openConnection();

            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",Integer.toString(urlParameters.getBytes().length));
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(urlParameters);
            dataOutputStream.flush();
            dataOutputStream.close();

            int code = connection.getResponseCode();
            switch (code){
                case 200:
                    map.put("code",200);

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
                    String tokenValue = (String) jsonObject.get("token");


                    JSONObject personalDetails = jsonObject.getJSONObject("personal_information");
                    int type = (Integer) personalDetails.get("type");
                    int pidvalue = (Integer) personalDetails.get("pid");


                    map.put("type",type);


                    Token token = new Token(tokenValue);
                    DatabaseHelper databaseHelper = new DatabaseHelper(context);
                    RuntimeExceptionDao<Token,Integer> tokenDao = databaseHelper.getTokenDao();
                    tokenDao.createOrUpdate(token);

                    Pid pid = new Pid(pidvalue);
                    RuntimeExceptionDao<Pid,Integer> pidDao = databaseHelper.getPidDao();
                    pidDao.createOrUpdate(pid);

                    break;
                case 400:
                    map.put("code",400);
                    break;
           }
           return map;
       }catch(Exception e){
            e.printStackTrace();
           map.put("code",404);
           return map;
       }finally {
           if (connection!=null){
               connection.disconnect();
           }
       }
    }
}
