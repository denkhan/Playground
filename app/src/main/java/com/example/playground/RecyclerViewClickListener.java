package com.example.playground;

import android.view.View;

//source: http://www.androidtutorialshub.com/android-recyclerview-click-listener-tutorial/
public interface RecyclerViewClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
