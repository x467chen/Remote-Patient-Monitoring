package com.ece651group8.uwaterloo.ca.ece_651_group8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ece651group8.uwaterloo.ca.ece_651_group8.util.RecordAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chenxuanqi on 16/11/22.
 */

public class RecordHeartActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @InjectView(R.id.toolbar_r)
    Toolbar toolbarr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_record);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        ButterKnife.inject(this);
        setSupportActionBar(toolbarr);



        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle bundle = getIntent().getBundleExtra("bundle");

        ArrayList<String> hearttimeList  = bundle.getStringArrayList("hearttime");
        ArrayList<String> heartRate = bundle.getStringArrayList("heartRate");
        ArrayList<String> heartRatelist = new ArrayList<String>();
        for(int i=0;i<heartRate.size();i++){

            String showtime=getDate(hearttimeList.get(i));
            String heartrate="Your heart rate is "+heartRate.get(i)+" BPM"+"\n"+showtime;
            heartRatelist.add(heartrate);

        }
        mAdapter = new RecordAdapter(heartRatelist);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mRecyclerView.setAdapter(mAdapter);



//        ArrayList<String> heartRate = new ArrayList<String>();
//        heartRate.add("Your heart rate is \n50 BMP");
//        heartRate.add("50");
//        heartRate.add("50");
//        heartRate.add("50");
//        heartRate.add("50");
//        heartRate.add("50");
//        heartRate.add("50");
//        heartRate.add("50");
//        heartRate.add("50");
    }

    public String getDate(String timesplit) {

        String[] dateAndTime = timesplit.split("T");

        String[] date = dateAndTime[0].split("-");
        String[] time = dateAndTime[1].split(":");

        Map<String,String> map = new HashMap<>();
        map.put("01","Jan");
        map.put("02","Feb");
        map.put("03","Mar");
        map.put("04","Apr");
        map.put("05","May");
        map.put("06","Jun");
        map.put("07","Jul");
        map.put("08","Aug");
        map.put("09","Sep");
        map.put("10","Oct");
        map.put("11","Nov");
        map.put("12","Dec");

        String showtime= map.get(date[1])+"."+ date[2]+" "+time[0]+":"+time[1];
        return showtime;

    }

        public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
