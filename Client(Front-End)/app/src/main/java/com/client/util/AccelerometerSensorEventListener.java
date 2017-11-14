package com.ece651group8.uwaterloo.ca.ece_651_group8.util;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

/**
 * Created by liuyue on 2016-11-08.
 */
public class AccelerometerSensorEventListener implements SensorEventListener {
    TextView output;
    //float maxX = 0,maxY = 0,maxZ = 0;
    int flag = 0;
    float vector = 0;

    public void onAccuracyChanged(Sensor s, int i){}

    public void onSensorChanged(SensorEvent se){
        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            /*if (x > maxX){
                maxX = x;
            }
            if (y > maxY){
                maxY = y;
            }
            if (z > maxZ){
                maxZ = z;
            }*/
            String s = String.format("(%f,%f,%f)",x,y,z);
            //String maxS = String.format("(%f,%f,%f)",maxX,maxY,maxZ);
            vector = (float) Math.sqrt(x*x+y*y+z*z);

            output.setText("Accelerometer Sensor: " + s+"\nVector Data:"+vector+"\nFlag:"+flag);
        }
    }
}