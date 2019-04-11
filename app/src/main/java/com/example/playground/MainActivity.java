package com.example.playground;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.playground.Adapter.ChildAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ChildAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // data to populate the RecyclerView with
        ArrayList<Child> Children = new ArrayList<>();
        Children.add(new Child("Alice",50));
        Children.add(new Child("Bob",100));
        Children.add(new Child("Charlie",500));
        Children.add(new Child("Dennis",50));
        Children.add(new Child("Elsa",60));
        Children.add(new Child("Frank",50));
        Children.add(new Child("Greta",50));

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_child);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChildAdapter(this, Children);
        recyclerView.setAdapter(adapter);



        View view = findViewById(R.id.child_one);
        TextView t = (TextView)view.findViewById(R.id.child_name);
        t.setText("Alice");

        View view1 = findViewById(R.id.child_two);
        TextView t1 = (TextView)view1.findViewById(R.id.child_name);
        t1.setText("Bob");
    }

    public void openChild(View v){
        Intent intent = new Intent(getBaseContext(), ActivityChild.class);
        TextView t = (TextView)v.findViewById(R.id.child_name);
        intent.putExtra("NAME", t.getText().toString());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
