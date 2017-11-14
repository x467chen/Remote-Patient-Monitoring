package com.ece651group8.uwaterloo.ca.ece_651_group8.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.ece651group8.uwaterloo.ca.ece_651_group8.CountDownActivity;
import com.ece651group8.uwaterloo.ca.ece_651_group8.util.AccelerometerSensorEventListener;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FallDetectorService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FALL = "com.ece651group8.uwaterloo.ca.ece_651_group8.service.action.FALL";


    public FallDetectorService() {
        super("FallDetectorService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFall(Context context) {
        Intent intent = new Intent(context, FallDetectorService.class);
        intent.setAction(ACTION_FALL);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FALL.equals(action)) {
                SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                SensorEventListener accelerometerListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent se) {
                        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                            float x = se.values[0];
                            float y = se.values[1];
                            float z = se.values[2];

//                            String s = String.format("(%f,%f,%f)", x, y, z);
//                            String maxS = String.format("(%f,%f,%f)",maxX,maxY,maxZ);
                            float vector = (float) Math.sqrt(x * x + y * y + z * z);
                            if (vector >= 9.8){
                                Context context = getBaseContext();
                                Intent intent = new Intent(context, CountDownActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplication().startActivity(intent);
                            }
                        }
                    }
                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
                };

                sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

}
