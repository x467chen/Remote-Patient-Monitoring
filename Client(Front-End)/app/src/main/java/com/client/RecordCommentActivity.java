package com.ece651group8.uwaterloo.ca.ece_651_group8;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.ece651group8.uwaterloo.ca.ece_651_group8.util.CommentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by chenxuanqi on 16/11/23.
 */

public class RecordCommentActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @InjectView(R.id.toolbar_comment)
    Toolbar toolbarr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_comment);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_comment);

        ButterKnife.inject(this);
        setSupportActionBar(toolbarr);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle bundle = getIntent().getBundleExtra("bundle");

        ArrayList<String> commenttime = bundle.getStringArrayList("commenttime");
        ArrayList<String> comment = bundle.getStringArrayList("comment");

        ArrayList<String> commentlist = new ArrayList<>();
        for(int i=0;i<comment.size();i++){
            String showtime=getDate(commenttime.get(i));
            String commentstring="Doctor comments:"+"\n"+showtime+"\n"+comment.get(i);
            commentlist.add(commentstring);
            Log.i("=========",commentlist+"");
        }

        mAdapter = new CommentAdapter(commentlist);
        mRecyclerView.addItemDecoration(new RecordCommentActivity.SimpleDividerItemDecoration(this));
        mRecyclerView.setAdapter(mAdapter);

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
