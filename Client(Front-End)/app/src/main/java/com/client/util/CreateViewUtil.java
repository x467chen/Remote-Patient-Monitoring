package com.ece651group8.uwaterloo.ca.ece_651_group8.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ece651group8.uwaterloo.ca.ece_651_group8.R;

import java.util.List;

/**
 * Created by chenxuanqi on 16/11/15.
 */

public class CreateViewUtil {
    public static void createContentView(Context context,LinearLayout contentLayout,RowItem rowItem) {
        View childView;
        TextView keyTv;
        TextView valueTv;

        LayoutInflater layoutInflater;
        layoutInflater = LayoutInflater. from(context);

            childView = layoutInflater.inflate(R.layout.row_mid_item, null);
            keyTv = (TextView) childView.findViewById(R.id.tv_key);
            valueTv = (TextView) childView.findViewById(R.id.tv_value);

            keyTv.setText(rowItem.getKey());
            valueTv.setText(rowItem.getValue());
            contentLayout.addView(childView);

    }
}
