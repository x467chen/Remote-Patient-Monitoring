package com.ece651group8.uwaterloo.ca.ece_651_group8.util;

import android.content.Context;
import android.util.Log;

import com.ece651group8.uwaterloo.ca.ece_651_group8.bean.Pid;
import com.ece651group8.uwaterloo.ca.ece_651_group8.bean.Token;
import com.ece651group8.uwaterloo.ca.ece_651_group8.db.DatabaseHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenxuanqi on 16/11/19.
 */

public class AddressUtil {

    public static JSONObject address(String path,String authorization) throws UnsupportedEncodingException {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestProperty("Authorization", "token " + authorization);
            //int code = connection.getResponseCode();

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
            return jsonObject;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }
}
