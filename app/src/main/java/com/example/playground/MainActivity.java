package com.example.playground;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.playground.Adapter.ChildAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ChildAdapter adapter;
    ArrayList<Child> Children = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // data to populate the RecyclerView with

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

    }
        // Action when clicking on a child
    public void openChild(View v){
        Intent intent = new Intent(getBaseContext(), ActivityChild.class);
        TextView t = (TextView)v.findViewById(R.id.child_name);
        intent.putExtra("NAME", t.getText().toString());
        startActivity(intent);
    }

        // Action of ADD CHILD button
    public void addChild(View v){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Add child");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_child, null);
        alertDialog.setView(dialogView);

        final EditText eT1 = (EditText) dialogView.findViewById(R.id.edit_text_child_name);
        final EditText eT2 = (EditText) dialogView.findViewById(R.id.edit_text_child_distance);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String m = eT1.getText().toString();
                        int temp = Integer.parseInt(eT2.getText().toString());
                        Children.add(new Child(m, temp));
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
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
