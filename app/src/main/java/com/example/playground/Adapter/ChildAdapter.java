package com.example.playground.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.playground.Child;
import com.example.playground.R;

import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {

    private List<Child> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public ChildAdapter(Context context, List<Child> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.child_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int distance =  mData.get(position).getDist();
        if( distance <= 50) holder.child_layout.setBackgroundResource(R.color.colorClose);
        holder.child_name.setText(mData.get(position).getname());

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView child_name;
        TextView child_distance;
        ConstraintLayout child_layout;

        ViewHolder(View itemView) {
            super(itemView);
            child_layout = itemView.findViewById(R.id.child_layout);
            child_distance = itemView.findViewById(R.id.child_distance);
            child_name = itemView.findViewById(R.id.child_name);
        }
    }
}