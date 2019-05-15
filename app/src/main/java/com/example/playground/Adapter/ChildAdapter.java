package com.example.playground.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.VibrationEffect;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.playground.Child;
import com.example.playground.MainActivity;
import com.example.playground.R;

import java.util.List;
import java.util.Random;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {

    private List<Child> mData;
    private LayoutInflater mInflater;
    private Location location;
    private Context context;

    // data is passed into the constructor
    public ChildAdapter(Context context, List<Child> data, Location l) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.location = l;
        this.context = context;
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
        /*int rand = new Random().nextInt(8)+1;
        if(rand == 1) holder.child_picture.setImageResource(R.drawable.one);
        else if(rand == 2) holder.child_picture.setImageResource(R.drawable.two);
        else if(rand == 3) holder.child_picture.setImageResource(R.drawable.three);
        else if(rand == 4) holder.child_picture.setImageResource(R.drawable.four);
        else if(rand == 5) holder.child_picture.setImageResource(R.drawable.five);
        else if(rand == 6) holder.child_picture.setImageResource(R.drawable.six);
        else if(rand == 7) holder.child_picture.setImageResource(R.drawable.seven);
        else if(rand == 8) holder.child_picture.setImageResource(R.drawable.eight);
        else if(rand == 9) holder.child_picture.setImageResource(R.drawable.nine);
        */

        if (mData.get(position).isActive()) {
            holder.child_layout.setAlpha(1f);
            if (mData.get(position).inRange(location)) {
                holder.child_layout.setBackgroundResource(R.color.colorClose);
                holder.child_name.setTextColor(Color.parseColor("#333333"));
                holder.child_distance.setTextColor(Color.parseColor("#333333"));
            } else {
                holder.child_layout.setBackgroundResource(R.color.colorFar);
                holder.child_name.setTextColor(Color.WHITE);
                holder.child_distance.setTextColor(Color.WHITE);
                ((MainActivity) context).warning();
            }
            holder.child_distance.setText((int) distance + " m");
        } else {
            holder.child_layout.setBackgroundResource(R.color.colorClose);
            holder.child_layout.setAlpha(0.5f);
            holder.child_name.setTextColor(Color.parseColor("#333333"));
            holder.child_distance.setTextColor(Color.parseColor("#333333"));
            holder.child_distance.setText("");
        }
        holder.child_picture.setImageResource(mData.get(position).getImage());
        holder.child_active.setChecked(mData.get(position).isActive());
        holder.child_name.setText(mData.get(position).getname());
        holder.child_id.setText(mData.get(position).getUsername());
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
        ImageView child_picture;
        ConstraintLayout child_layout;
        Switch child_active;
        TextView child_id;

        ViewHolder(View itemView) {
            super(itemView);
            child_layout = itemView.findViewById(R.id.child_layout);
            child_distance = itemView.findViewById(R.id.child_distance);
            child_name = itemView.findViewById(R.id.child_name);
            child_picture = itemView.findViewById(R.id.imageView2);
            child_active = itemView.findViewById(R.id.child_active);
            child_id = itemView.findViewById(R.id.child_id);
        }
    }
}