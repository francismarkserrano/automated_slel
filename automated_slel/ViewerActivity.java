package com.example.francismark.automated_slel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class ViewerActivity extends AppCompatActivity {

    // Declare UI variables
    private TextView type_tv;
    private TextView team_tv;
    private TextView name_tv;
    private TextView ref_num_tv;
    private TextView reason_tv;
    private TextView date_from_tv;
    private TextView date_to_tv;
    private TextView date_filed_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        String type =  "";
        String team =  "";
        String name =  "";
        String ref_num =  "";
        String reason =  "";
        String date_from =  "";
        String date_to = "";
        String date_filed = new Date().toString();

        if (intent != null) {

            type = intent.getStringExtra("type");
            team = intent.getStringExtra("team");
            name = intent.getStringExtra("name");
            ref_num = intent.getStringExtra("ref_num");
            reason = intent.getStringExtra("reason");
            date_from = intent.getStringExtra("date_from");
            date_to = intent.getStringExtra("date_to");
        }


        setContentView(R.layout.activity_viewer);

        // Initialize UI variables
        name_tv = findViewById(R.id.view_name_field);
        type_tv = findViewById(R.id.view_type_field);
        team_tv = findViewById(R.id.view_team_field);
        ref_num_tv = findViewById(R.id.view_ref_num_field);
        reason_tv = findViewById(R.id.view_reason_field);
        date_from_tv = findViewById(R.id.view_date_from_field);
        date_to_tv = findViewById(R.id.view_date_to_field);
        date_filed_tv = findViewById(R.id.view_date_filed_field);

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//
//            type = extras.getString("type");
//            team = extras.getString("team");
//            name = extras.getString("name");
//            ref_num = extras.getString("ref_num");
//            reason = extras.getString("reason");
//            date_from = extras.getString("date_from");
//            date_to = extras.getString("date_to");
//        }

        System.out.println("****VIEWER***type: " + type + "***team: " + team + "***name: " + name + "***ref_num: " + ref_num
                + "***reason: " + reason + "***date_from: " + date_from + "***date_to: " + date_to);

        name_tv.setText(name);
        type_tv.setText(type);
        team_tv.setText(team);
        ref_num_tv.setText(ref_num);
        reason_tv.setText(reason);
        date_from_tv.setText(date_from);
        date_to_tv.setText(date_to);
        date_filed_tv.setText(date_filed);
    }

    public void Home(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
