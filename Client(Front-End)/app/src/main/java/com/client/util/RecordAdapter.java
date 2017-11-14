package com.ece651group8.uwaterloo.ca.ece_651_group8.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ece651group8.uwaterloo.ca.ece_651_group8.R;

import java.util.ArrayList;

/**
 * Created by chenxuanqi on 16/11/22.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private ArrayList<String> mRateset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mRateView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mRateView = (TextView) itemView.findViewById(R.id.text_view);
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecordAdapter(ArrayList<String> myRateset) {
        mRateset = myRateset;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mRateView.setText(mRateset.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mRateset.size();
    }
}



