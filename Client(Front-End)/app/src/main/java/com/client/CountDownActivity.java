package com.ece651group8.uwaterloo.ca.ece_651_group8;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ece651group8.uwaterloo.ca.ece_651_group8.service.FallDetectorService;
import com.ece651group8.uwaterloo.ca.ece_651_group8.util.SendSMSMessage;

import java.util.ArrayList;
import java.util.Locale;

import static com.ece651group8.uwaterloo.ca.ece_651_group8.EditActivity.DOCTOR_PHONE_KEY;
import static com.ece651group8.uwaterloo.ca.ece_651_group8.EditActivity.PREFS_NAME;

public class CountDownActivity extends AppCompatActivity {


    private CountDownTimer mCountDownTimer;
    private CountDownTimer gpsCountDownTimer;
    private TextView mCountDownNumber;
    private String countDownNumber;
    public String phoneNo;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    public LocationManager locMan;
    public LocationEventListener gpsLocListener;
    public LocationListener networkLocListener;

    public Button yesButton;
    public Button noButton;

    Vibrator vibrator;
    private SharedPreferences sharedPreferences = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        phoneNo = sharedPreferences.getString(DOCTOR_PHONE_KEY, "");

        vibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);

        yesButton = (Button) findViewById(R.id.Button_yes);
        noButton = (Button) findViewById(R.id.Button_no);

        locMan = (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);
        gpsLocListener = new LocationEventListener();
        networkLocListener = new LocationEventListener();

        mCountDownNumber = (TextView)findViewById(R.id.countDownNumber);
        promptSpeechInput();
        mCountDownTimer = new CountDownTimer(20000,1000) {

            @Override
            public void onTick(final long millisUntilFinished) {

                countDownNumber = String.valueOf(millisUntilFinished/1000);
                mCountDownNumber.setText(countDownNumber);
                vibrator.vibrate(100);
            }

            @Override
            public void onFinish() {
                //Do what you want
                vibrator.cancel();
                countDownNumber = String.valueOf(0);
                mCountDownNumber.setText(countDownNumber);
                Confirm();
                mCountDownNumber.setText("Wait for Help");
            }
        };

        mCountDownTimer.start();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.cancel();
                vibrator.cancel();
                Confirm();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.cancel();
                vibrator.cancel();
                Cancel();
            }
        });


    }

    public void Confirm(){

        gpsCountDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (ActivityCompat.checkSelfPermission(CountDownActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CountDownActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locMan.requestSingleUpdate(locMan.GPS_PROVIDER, gpsLocListener, null);
            }
            @Override
            public void onFinish() {
                //Do what you want
                if (ActivityCompat.checkSelfPermission(CountDownActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CountDownActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    // ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locMan.requestSingleUpdate(locMan.NETWORK_PROVIDER, networkLocListener, null);
                locMan.removeUpdates(gpsLocListener);
                FallDetectorService.startActionFall(CountDownActivity.this);
                Intent intent = new Intent();
                intent.setClass(CountDownActivity.this,MainActivity.class);
                CountDownActivity.this.startActivity(intent);
                CountDownActivity.this.finish();
            }
        };
        gpsCountDownTimer.start();
    }

    public void Cancel(){

        AlertDialog.Builder builder = new AlertDialog.Builder(CountDownActivity.this);
        builder.setTitle("Are you sure?");
        builder.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){
                mCountDownTimer.cancel();
                FallDetectorService.startActionFall(CountDownActivity.this);
                Intent intent = new Intent();
                intent.setClass(CountDownActivity.this,MainActivity.class);
                CountDownActivity.this.startActivity(intent);
                CountDownActivity.this.finish();
            }
        });

        builder.setNegativeButton("No",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton){

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class LocationEventListener implements LocationListener {

        private String msg;

        @Override
        public void onLocationChanged(Location location) {

            msg = "Need help! Location: http://maps.google.com/?q="+ location.getLatitude()+","+location.getLongitude();

            String outputMessage = SendSMSMessage.sendSMSMessage(phoneNo, msg);
            Toast.makeText(CountDownActivity.this, outputMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            CountDownActivity.this.startActivity(intent);
            Toast.makeText(CountDownActivity.this, "Gps is turned off!! ",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {

            Toast.makeText(CountDownActivity.this, "Gps is turned on!! ",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }



    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if("yes".equals(result.get(0))){
                        mCountDownTimer.cancel();
                        Confirm();
                    }
                    else if("no".equals(result.get(0))){
                        mCountDownTimer.cancel();
                        FallDetectorService.startActionFall(CountDownActivity.this);
                        Intent intent = new Intent();
                        intent.setClass(CountDownActivity.this,MainActivity.class);
                        CountDownActivity.this.startActivity(intent);
                        CountDownActivity.this.finish();
                    }

                }
                break;
            }

        }
    }

}
