package com.ece651group8.uwaterloo.ca.ece_651_group8;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ece651group8.uwaterloo.ca.ece_651_group8.bean.Config;
import com.ece651group8.uwaterloo.ca.ece_651_group8.bean.Pid;
import com.ece651group8.uwaterloo.ca.ece_651_group8.bean.Token;
import com.ece651group8.uwaterloo.ca.ece_651_group8.db.DatabaseHelper;
import com.ece651group8.uwaterloo.ca.ece_651_group8.util.SendSMSMessage;
import com.ece651group8.uwaterloo.ca.ece_651_group8.util.VerifyUtils;
import com.ece651group8.uwaterloo.ca.ece_651_group8.util.VerifyUtilsRecord;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.ece651group8.uwaterloo.ca.ece_651_group8.EditActivity.DOCTOR_PHONE_KEY;
import static com.ece651group8.uwaterloo.ca.ece_651_group8.EditActivity.PREFS_NAME;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.navigation_view)
    NavigationView navigationView;
    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @InjectView(R.id.heart_rate_card)
    CardView heartRateCard;
    @InjectView(R.id.blood_pressure_card)
    CardView bloodPressureCard;
    @InjectView(R.id.doctor_comment_card)
    CardView doctorCommentCard;


    @InjectView(R.id.emergency_btn)
    FloatingActionButton emergencyBtn;

    Vibrator vibrator;
    private SoundPool soundPool;
    private int hit;

    public String gpsMsg;
    public String phoneNo;
    public CountDownTimer mCountDownTimer;
    public LocationManager locMan;
    public LocationEventListener gpsLocListener;
    public LocationListener networkLocListener;


    private MainActivity.UserHealTask userHealTask = null;
    private MainActivity.UserRecordTask userRecordTask = null;
    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        //set toolbar
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);

        vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        hit = soundPool.load(MainActivity.this, R.raw.alert, 0);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        phoneNo = sharedPreferences.getString(DOCTOR_PHONE_KEY, "");
        Log.i("aaaaaaa",phoneNo);

        locMan = (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);
        gpsLocListener = new LocationEventListener();
        networkLocListener = new LocationEventListener();

        emergencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer = new CountDownTimer(10000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    }
                };
                mCountDownTimer.start();
            }
        });


        heartRateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptHealthRecord(0);
            }
        });
        bloodPressureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptHealthRecord(1);
            }
        });
        doctorCommentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptHealthRecord(2);
            }
        });
    }

//    public void GetLocation(){
//
//
//    }

    public class LocationEventListener implements LocationListener {

        private String msg;

        @Override
        public void onLocationChanged(Location location) {

            msg = "Need help! Location: http://maps.google.com/?q="+ location.getLatitude()+","+location.getLongitude();

            String outputMessage = SendSMSMessage.sendSMSMessage(phoneNo, msg);
            Toast.makeText(MainActivity.this, outputMessage, Toast.LENGTH_SHORT).show();
//            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String provider) {

            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            MainActivity.this.startActivity(intent);
            Toast.makeText(MainActivity.this, "Gps is turned off!! ",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {

            Toast.makeText(MainActivity.this, "Gps is turned on!! ",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()){
            case R.id.profile:
                attemptHealthInfo();
                break;

            case R.id.update_data:
                vibrator.vibrate(200);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Are you sure to update?");
                builder.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        Toast.makeText(getApplicationContext(), "Update successful!", Toast.LENGTH_LONG).show();
                    }
                });

                builder.setNegativeButton("No",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.edit:
                Intent intentEdit = new Intent();
                intentEdit.setClass(MainActivity.this, EditActivity.class);
                MainActivity.this.startActivity(intentEdit);
                break;
            case R.id.health_record:
                attemptHealthRecord(0);
                break;

            case R.id.location:
                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Config config = new Config();
                String url = "http://"+config.getIp()+":"+config.getPort();
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(Intent.createChooser(intent, getString(R.string.choose_browser)));
                break;

            case R.id.contact_doctor:
                //String docPhoneNumber = sharedPreferences.getString(DOCTOR_PHONE_KEY, "");

                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phoneNo));
                smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(smsIntent);
                break;

            case R.id.share:
                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "My heart rate is 62 now!");
                //Uri uri = Uri.fromFile(getFileStreamPath("shared.png"));
                //intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(intent, getString(R.string.choose_app)));
                break;

            case R.id.log_out:
                AlertDialog.Builder builderlog = new AlertDialog.Builder(MainActivity.this);
                builderlog.setTitle("Are you sure to log out?");
                builderlog.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,LoginActivity.class);
                        MainActivity.this.startActivity(intent);
                        MainActivity.this.finish();
                    }
                });

                builderlog.setNegativeButton("No",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){

                    }
                });
                AlertDialog dialoglog = builderlog.create();
                dialoglog.show();
                break;

        }
        return true;
    }

    private void attemptHealthInfo(){
        //query token form db
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        RuntimeExceptionDao<Token,Integer> tokenDao = databaseHelper.getTokenDao();
        List<Token> list = tokenDao.queryForEq("id","token");
        RuntimeExceptionDao<Pid,Integer> pidDao = databaseHelper.getPidDao();
        List<Pid> list2 = pidDao.queryForEq("id","pid");


        if (list.size()>0 && list2.size()>0){
            String tokenValue = list.get(0).getValue();
            int pidValue = list2.get(0).getValue();

            userHealTask = new MainActivity.UserHealTask(tokenValue,pidValue);
            userHealTask.execute((Void) null);
        }
    }

    private void attemptHealthRecord(int type){
        //query token form db
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        RuntimeExceptionDao<Token,Integer> tokenDao = databaseHelper.getTokenDao();
        List<Token> list = tokenDao.queryForEq("id","token");
        RuntimeExceptionDao<Pid,Integer> pidDao = databaseHelper.getPidDao();
        List<Pid> list2 = pidDao.queryForEq("id","pid");


        if (list.size()>0 && list2.size()>0){
            String tokenValue = list.get(0).getValue();
            int pidValue = list2.get(0).getValue();

            userRecordTask = new MainActivity.UserRecordTask(tokenValue,pidValue,type);
            userRecordTask.execute((Void) null);
        }

    }

    public class UserRecordTask extends AsyncTask<Void, Void, Map<String,String>> {

        private final String authorization;
        private final int pidValue;
        private int type;

        UserRecordTask(String t, int p, int type) {
            authorization = t;
            pidValue=p;
            this.type = type;
        }

        @Override
        protected Map<String,String> doInBackground(Void... params) {
            Map<String,String> map;


            try{
                Config config = new Config();
                String url = "http://"+config.getIp()+":"+config.getPort()+"/health-records/"+pidValue+"/";
                map = VerifyUtilsRecord.verify(MainActivity.this,url,authorization);
                return map;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Map<String,String> map) {
            if ("200".equals(map.get("code"))) {
                Bundle bundle=new Bundle();

                //heart
                ArrayList<String> hearttimeList  = new ArrayList<>();
                ArrayList<String> heartRate = new ArrayList<>();

                for(int i=0;i<Integer.valueOf(map.get("heartlength"));i++) {
                    hearttimeList.add(map.get("datetimeHeartRate"+i));
                    heartRate.add(map.get("heartRate"+i));
                    //Log.i("===========test",heartRate+"");
                }
                bundle.putStringArrayList("hearttime",hearttimeList);
                bundle.putStringArrayList("heartRate",heartRate);

                //blood pressure
                ArrayList<String> pressuretimeList  = new ArrayList<>();
                ArrayList<String> lowpressure = new ArrayList<>();
                ArrayList<String> highpressure = new ArrayList<>();

                for(int i=0;i<Integer.valueOf(map.get("bloodlength"));i++) {
                    pressuretimeList.add(map.get("datePressure"+i));
                    lowpressure.add(map.get("lowBloodPressure"+i));
                    highpressure.add(map.get("highBloodPressure"+i));
                }
                bundle.putStringArrayList("pressuretime",pressuretimeList);
                bundle.putStringArrayList("lowpressure",lowpressure);
                bundle.putStringArrayList("highpressure",highpressure);


                //comment
                ArrayList<String> commenttimeList  = new ArrayList<>();
                ArrayList<String> comment = new ArrayList<>();
                for(int i=0;i<Integer.valueOf(map.get("commentlength"));i++) {
                    commenttimeList.add(map.get("datetimeComments"+i));
                    comment.add(map.get("comments"+i));
                }
                bundle.putStringArrayList("commenttime",commenttimeList);
                bundle.putStringArrayList("comment",comment);

                if(type==0){
                    Intent intent = new Intent();
                    intent.putExtra("bundle",bundle);
                    intent.setClass(MainActivity.this,RecordHeartActivity.class);
                    MainActivity.this.startActivity(intent);
                }else if(type==1){
                    Intent intent = new Intent();
                    intent.putExtra("bundle", bundle);
                    intent.setClass(MainActivity.this, RecordBloodActivity.class);
                    MainActivity.this.startActivity(intent);
                }else{

                    Intent intent = new Intent();
                    intent.putExtra("bundle", bundle);
                    intent.setClass(MainActivity.this, RecordCommentActivity.class);
                    MainActivity.this.startActivity(intent);

                }

            } else {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,LoginActivity.class);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();

                Toast toast = Toast.makeText(getApplicationContext(), "Connection time out. Please login again.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {
            userHealTask = null;
        }
    }




    //profile
    public class UserHealTask extends AsyncTask<Void, Void, Map<String,String>> {

        private final String authorization;
        private final int pidValue;

        UserHealTask(String t, int p) {
            authorization = t;
            pidValue=p;
        }

        @Override
        protected Map<String,String> doInBackground(Void... params) {
            Map<String,String> map;
            Map<String,String> map2;
            try{
                Config config = new Config();
                String url = "http://"+config.getIp()+":"+config.getPort()+"/health-records/"+pidValue+"/";
                //String url2 = "http://"+config.getIp()+":"+config.getPort()+"/patients/"+pidValue+"/";
                map = VerifyUtils.verify(MainActivity.this,url,authorization);
                return map;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        //get Map from doInBackground
        //turn to the user activity depending on their identity
        @Override
        protected void onPostExecute(final Map<String,String> map) {
            if ("200".equals(map.get("code"))) {
                Bundle bundle=new Bundle();

                bundle.putString("name","Marry");

                bundle.putString("age",map.get("age"));
                bundle.putString("height",map.get("height"));
                bundle.putString("weight",map.get("weight"));
                bundle.putString("sex",map.get("sex"));

                bundle.putString("email","patient@gmail.com");
                bundle.putString("phone","5197817899");


                Intent intent = new Intent();
                intent.putExtra("bundle",bundle);
                intent.setClass(MainActivity.this,ProfileActivity.class);
                MainActivity.this.startActivity(intent);

            } else {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,LoginActivity.class);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();

                Toast toast = Toast.makeText(getApplicationContext(), "Connection time out. Please login again.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {
            userHealTask = null;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Are you sure to log out?");
            builder.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int whichButton){
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,LoginActivity.class);
                    MainActivity.this.startActivity(intent);
                    MainActivity.this.finish();
                }
            });

            builder.setNegativeButton("No",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int whichButton){

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }
}
