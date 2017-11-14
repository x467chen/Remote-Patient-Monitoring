package com.ece651group8.uwaterloo.ca.ece_651_group8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.ece651group8.uwaterloo.ca.ece_651_group8.util.CreateViewUtil;
import com.ece651group8.uwaterloo.ca.ece_651_group8.util.RowItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileActivity extends AppCompatActivity {


    @InjectView(R.id.ll_content_name)
    public LinearLayout contentName;
    @InjectView(R.id.ll_content_sex)
    public LinearLayout contentSex;
    @InjectView(R.id.ll_content_age)
    public LinearLayout contentAge;
    @InjectView(R.id.ll_content_height)
    public LinearLayout contentHeight;
    @InjectView(R.id.ll_content_weight)
    public LinearLayout contentWeight;
    @InjectView(R.id.ll_content_email)
    public LinearLayout contentEmail;
    @InjectView(R.id.ll_content_phone)
    public LinearLayout contentPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.inject(this);


        Bundle bundle = getIntent().getBundleExtra("bundle");

//        mListMid.add( new RowItem( "Heart Rate" , bundle.getString("heart")));
//        mListMid.add( new RowItem( "Blood Pressure" , bundle.getString("blood")));
//        mListBottom.add( new RowItem( "Comments" , bundle.getString("comments")));

        CreateViewUtil.createContentView (this,contentName,new RowItem( "Name" , bundle.getString("name")));
        CreateViewUtil.createContentView (this,contentSex,new RowItem( "Sex" , bundle.getString("sex")));
        CreateViewUtil.createContentView (this,contentAge,new RowItem( "Age" , bundle.getString("age")));
        CreateViewUtil.createContentView (this,contentHeight,new RowItem( "Height" ,bundle.getString("height")));
        CreateViewUtil.createContentView (this,contentWeight,new RowItem( "Weight" , bundle.getString("weight")));
        CreateViewUtil.createContentView (this,contentEmail,new RowItem( "Email" , bundle.getString("email")));
        CreateViewUtil.createContentView (this,contentPhone,new RowItem( "Phone" , bundle.getString("phone")));
    }
}
