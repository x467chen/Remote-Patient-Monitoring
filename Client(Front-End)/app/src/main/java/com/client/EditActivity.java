package com.ece651group8.uwaterloo.ca.ece_651_group8;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ece651group8.uwaterloo.ca.ece_651_group8.util.CreateViewUtil;
import com.ece651group8.uwaterloo.ca.ece_651_group8.util.RowItem;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.ece651group8.uwaterloo.ca.ece_651_group8.LoginActivity.PREFS_NAME;
import static com.ece651group8.uwaterloo.ca.ece_651_group8.LoginActivity.USER_NAME_KEY;

/**
 * Created by chenxuanqi on 16/11/23.
 */

public class EditActivity extends AppCompatActivity {

    @InjectView(R.id.relative_phone_edit)
    public EditText relativePhone;
    @InjectView(R.id.doctor_phone_edit)
    public EditText doctorPhone;
    @InjectView(R.id.submit_button)
    public Button submit;

    private SharedPreferences sharedPreferences = null;
    public static final String PREFS_NAME = "prefsname";
    public static final String RELATIVE_PHONE_KEY = "relativePhone";
    public static final String DOCTOR_PHONE_KEY = "doctorPhone";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.inject(this);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        relativePhone.setText(getRelativePhoneNumber());
        doctorPhone.setText(getDoctorPhoneNumber());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remember();
                Toast.makeText(EditActivity.this,"Save phone number successful!",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void remember(){
        saveRelativePhoneNumber(relativePhone.getText().toString());
        saveDoctorPhoneNumber(doctorPhone.getText().toString());
    }

    private void saveRelativePhoneNumber(String userName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();// 获取编辑器
        editor.putString(RELATIVE_PHONE_KEY, userName);
        editor.commit();
    }

    private void saveDoctorPhoneNumber(String userName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();// 获取编辑器
        editor.putString(DOCTOR_PHONE_KEY, userName);
        editor.commit();
    }

    private String getRelativePhoneNumber() {
        return sharedPreferences.getString(RELATIVE_PHONE_KEY, "");
    }

    private String getDoctorPhoneNumber() {
        return sharedPreferences.getString(DOCTOR_PHONE_KEY, "");
    }


}
