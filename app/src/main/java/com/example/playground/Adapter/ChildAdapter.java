package com.example.playground.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private Location location;

    // data is passed into the constructor
    public ChildAdapter(Context context, List<Child> data, Location l) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.location = l;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.child_item, parent, false);
        return new ViewHolder(view);
    }

    public void setLoc(Location l){
        location = new Location(l);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        double distance =  mData.get(position).distanceBetween(location);
        Log.d("distance" , Double.toString(distance));
        if( distance <= 50){
            holder.child_layout.setBackgroundResource(R.color.colorClose);
            holder.child_name.setTextColor(Color.parseColor("#333333"));
            holder.child_distance.setTextColor(Color.parseColor("#333333"));
        }
        else {
            holder.child_layout.setBackgroundResource(R.color.colorFar);
            holder.child_name.setTextColor(Color.WHITE);
            holder.child_distance.setTextColor(Color.WHITE);
        }
        holder.child_distance.setText(String.format("%.2f", distance) +  " m");
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