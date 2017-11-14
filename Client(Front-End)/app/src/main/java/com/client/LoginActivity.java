package com.ece651group8.uwaterloo.ca.ece_651_group8;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ece651group8.uwaterloo.ca.ece_651_group8.bean.Config;
import com.ece651group8.uwaterloo.ca.ece_651_group8.service.FallDetectorService;
import com.ece651group8.uwaterloo.ca.ece_651_group8.util.LoginUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.R.attr.type;

public class LoginActivity extends AppCompatActivity {


    @InjectView(R.id.user_name)
    public EditText mUserName;
    @InjectView(R.id.password)
    public EditText mPassword;
    @InjectView(R.id.sign_on_button)
    public ImageButton signOn;
    @InjectView(R.id.remember)
    public CheckBox mRemember;


    private UserLoginTask userLoginTask = null;
    private SharedPreferences sharedPreferences = null;


    public static final String PREFS_NAME = "prefsname";
    public static final String REMEMBER_USER_NAME_KEY = "remember"; //remember username
    public static final String USER_NAME_KEY = "userid"; //username flag


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

//        mPassword.setText("11111111");

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mRemember.setChecked(getRemember());
        mUserName.setText(getUserName());

        signOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remember();
                attemptLogin();
            }
        });
    }

    private void remember(){
        if (mRemember.isChecked()){
            saveRemember(true);
            saveUserName(mUserName.getText().toString());
        } else {
            saveRemember(false);
            saveUserName("");
        }
    }

    private void saveUserName(String userName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();// 获取编辑器
        editor.putString(USER_NAME_KEY, userName);
        editor.commit(); //save data
        //editor.clear(); //clear data
    }

    // 设置是否保存的用户名
    private void saveRemember(boolean remember) {
        SharedPreferences.Editor editor = sharedPreferences.edit();// 获取编辑器
        editor.putBoolean(REMEMBER_USER_NAME_KEY, remember);
        editor.commit();
    }

    private String getUserName() {
        return sharedPreferences.getString(USER_NAME_KEY, "");
    }

    // 获取是否保存的用户名
    private boolean getRemember() {
        return sharedPreferences.getBoolean(REMEMBER_USER_NAME_KEY, true);
    }


    //check the form of user and password
    private void attemptLogin() {
        // Reset errors.
        mUserName.setError(null);
        mPassword.setError(null);

        String userName = mUserName.getText().toString();
        String password = mPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPassword.setError("The Password is too short.");
            focusView = mPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(userName)) {
            mUserName.setError("Username is required.");
            focusView = mUserName;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        } else {

            userLoginTask = new UserLoginTask(userName, password);
            userLoginTask.execute((Void) null);
        }

    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }




    public class UserLoginTask extends AsyncTask<Void, Void, Map<String,Integer>> {

        private final String userName;
        private final String password;

        UserLoginTask(String u, String p) {
            userName = u;
            password = p;
        }

        private Config config = new Config();
        String url = "http://"+config.getIp()+":"+config.getPort()+"/api-token-auth/";


        //verified the user in the background
        //save and query token from db
        //close db
        @Override
        protected Map<String,Integer> doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Map<String,Integer> map;
            try{
                map = LoginUtils.login(LoginActivity.this,url,userName,password);

//                map = new HashMap<>();
//                map.put("code",200);
//                map.put("type",2);

                return map;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }


        //get Map from doInBackground
        //turn to the user activity depending on their identity
        @Override
        protected void onPostExecute(final Map<String,Integer> map) {

            if (map.get("code") == 200) {
                Log.i("=================",type+"");

                if (map.get("type") == 2) {

                    FallDetectorService.startActionFall(LoginActivity.this);
                    //turn to the patient interface
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();

                } else {
                    //turn to the relative interface
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, RelativeActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }

            } else if (map.get("code") == 404){
                Toast.makeText(LoginActivity.this,"Connection Failure",Toast.LENGTH_SHORT).show();
            } else {
                mPassword.setError("Incorrect Password or Username");
                mPassword.requestFocus();
            };
        }

        @Override
        protected void onCancelled() {
            userLoginTask = null;
        }
    }

}
