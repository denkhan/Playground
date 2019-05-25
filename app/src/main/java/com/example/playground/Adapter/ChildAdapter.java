package com.example.playground.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.playground.Child;
import com.example.playground.MainActivity;
import com.example.playground.R;

import java.util.List;

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
        if (mData.get(position).isActive()) {
            holder.child_item.setAlpha(1f);
            if (mData.get(position).inRange(location)) {
                untriggeredActiveView(holder);
            } else {
                triggeredActiveView(holder);
            }
            holder.child_distance.setText(MainActivity.formatDistance(distance));
        } else {
            untriggeredActiveView(holder);
            holder.child_item.setAlpha(0.5f);
            holder.child_distance.setText("");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.child_layout.setTranslationZ(6f);
        }
        holder.child_picture.setImageResource(mData.get(position).getImage());
        holder.child_active.setChecked(mData.get(position).isActive());
        holder.child_name.setText(mData.get(position).getname());
        holder.child_id.setText(mData.get(position).getUsername());
    }

    private void triggeredActiveView(ViewHolder holder) {
        activeView(holder, R.color.colorFurther, Color.WHITE);
    }

    private void untriggeredActiveView(ViewHolder holder) {
        activeView(holder, R.color.colorClose, Color.parseColor("#333333"));
    }

    private void activeView(ViewHolder holder, int backgroundColor, int textColor) {
        //holder.child_item.setBackgroundResource(backgroundColor);
        holder.child_distance.setTextColor(textColor);
        holder.child_name.setTextColor(textColor);
        holder.child_layout.setBackgroundResource(backgroundColor);
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
        LinearLayout child_item;

        ViewHolder(View itemView) {
            super(itemView);
            child_layout = itemView.findViewById(R.id.child_layout);
            child_distance = itemView.findViewById(R.id.child_distance);
            child_name = itemView.findViewById(R.id.child_name);
            child_picture = itemView.findViewById(R.id.imageView2);
            child_active = itemView.findViewById(R.id.child_active);
            child_id = itemView.findViewById(R.id.child_id);
            child_item = itemView.findViewById(R.id.child_item);
        }
    }
}