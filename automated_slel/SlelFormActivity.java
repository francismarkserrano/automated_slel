package com.example.francismark.automated_slel;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SlelFormActivity extends AppCompatActivity {

    // Firebase services
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static ValueEventListener postListener;
    private FirebaseAnalytics mFirebaseAnalytics;

    // Declare UI variables
    private TextView date_from_tv;
    private TextView date_to_tv;
    private EditText formName;
    private EditText formRefNum;
    private EditText formReason;
    private Button date_from_btn;
    private Button date_to_btn;
    private RadioGroup radioGroup;
    private Spinner teamList;
    private Calendar cal_from;
    private Calendar cal_to;
    private DatePickerDialog.OnDateSetListener date_from;
    private DatePickerDialog.OnDateSetListener date_to;
    private Date current_date;

    private String cal_from_str;
    private String cal_to_str;
    private String type = "";
    private String team = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slel_form);

        // Initialize UI variables
        date_from_tv = findViewById(R.id.form_date_from);
        date_to_tv = findViewById(R.id.form_date_to);
        formName = findViewById(R.id.form_name_field);
        formRefNum = findViewById(R.id.form_ref_num_field);
        formReason = findViewById(R.id.form_reason_field);
        radioGroup = findViewById(R.id.form_radio_group);
        teamList = findViewById(R.id.form_team_list);
        date_from_btn = findViewById(R.id.date_from_btn);
        date_to_btn = findViewById(R.id.date_to_btn);
        cal_from = Calendar.getInstance();
        cal_to = Calendar.getInstance();
        current_date = new Date();
        System.out.println("****CURRENT DATE***" + current_date);

        mDatabase = FirebaseDatabase.getInstance().getReference().getRoot();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //mDatabase.addValueEventListener(postListener);

        // DATE FROM
        date_from = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal_from.set(Calendar.YEAR, year);
                cal_from.set(Calendar.MONTH, month);
                cal_from.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //Applies the set date
                cal_from_str = getBirthdateString(cal_from.getTime());
                date_from_tv.setText(cal_from_str);
            }
        };

        date_from_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Makes the date picker dialog appear
                DatePickerDialog datePicker = new DatePickerDialog(SlelFormActivity.this, date_from, cal_from.get(Calendar.YEAR),
                        cal_from.get(Calendar.MONTH), cal_from.get(Calendar.DAY_OF_MONTH));
                datePicker.setTitle("Choose your birth date");
                datePicker.show();
            }
        });

        // DATE TO
        date_to = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal_to.set(Calendar.YEAR, year);
                cal_to.set(Calendar.MONTH, month);
                cal_to.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //Applies the set date
                cal_to_str = getBirthdateString(cal_to.getTime());
                date_to_tv.setText(cal_to_str);
            }
        };

        date_to_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Makes the date picker dialog appear
                DatePickerDialog datePicker = new DatePickerDialog(SlelFormActivity.this, date_to, cal_to.get(Calendar.YEAR),
                        cal_to.get(Calendar.MONTH), cal_to.get(Calendar.DAY_OF_MONTH));
                datePicker.setTitle("Choose your birth date");
                datePicker.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //mDatabase.addValueEventListener(postListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (postListener != null) {
//            mDatabase.removeEventListener(postListener);
//        }
    }

    public void cancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void submit(View view) {

        // Validate all records
        if (!validateForm()) {
            return;
        }

        // Set variable and get values from UI
        final String name = formName.getText().toString();
        String refNum = formRefNum.getText().toString();
        String reason = formReason.getText().toString();
        team = teamList.getSelectedItem().toString();
        Date curr_date = new Date();

        // Get selected radio button from radioGroup
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedType = findViewById(selectedId);
        if (selectedType != null) {
            type = selectedType.getText().toString();
        }

        // Collect all values before saving to database
        Map<String,String> slel_form = new HashMap<>();
        slel_form.put("name", name);
        slel_form.put("type", type);
        slel_form.put("team", team);
        slel_form.put("ref_num", refNum);
        slel_form.put("reason", reason);
        slel_form.put("date_from", cal_from_str);
        slel_form.put("date_to", cal_to_str);
        slel_form.put("date_filed", curr_date.toString());

        mDatabase.getRoot().child("SLEL").child(type).child(team).child("content").setValue(slel_form)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SlelFormActivity.this, "Request Sent",
                                Toast.LENGTH_LONG).show();

                        // Analytics for SLEL
                        Bundle params = new Bundle();
                        params.putString("TYPE", type);
                        params.putString("TEAM", team);
                        mFirebaseAnalytics.logEvent("automated_slel", params);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, team);
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                        Intent intent = new Intent(SlelFormActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SlelFormActivity.this, "Request Failed",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String getBirthdateString(Date pickedDate){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(pickedDate);
    }

    // Validate if email/password parameters have values
    private boolean validateForm() {
        boolean valid = true;

        if (formName.getText() == null || TextUtils.isEmpty(formName.getText().toString())) {
            formName.setError("Required.");
            valid = false;
        } else {
            formName.setError(null);
        }

//        if (TextUtils.isEmpty(formRefNum.getText().toString())) {
//            formRefNum.setError("Required.");
//            valid = false;
//        } else {
//            formRefNum.setError(null);
//        }

        if (formReason.getText() == null || TextUtils.isEmpty(formReason.getText().toString())) {
            formReason.setError("Required.");
            valid = false;
        } else {
            formReason.setError(null);
        }

        // Get selected radio button from radioGroup
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedType = findViewById(selectedId);

        if (selectedType == null) {
            Toast.makeText(SlelFormActivity.this, " Please choose a Type",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (date_from_tv.getText() == null || TextUtils.isEmpty(date_from_tv.getText().toString())) {
            date_from_tv.setError("Required.");
            valid = false;
        } else {
            date_from_tv.setError(null);
        }

        if (date_to_tv.getText() == null || TextUtils.isEmpty(date_to_tv.getText().toString())) {
            date_to_tv.setError("Required.");
            valid = false;
        } else {
            date_to_tv.setError(null);
        }

        if (date_from_tv.getText().toString().isEmpty() || date_to_tv.getText().toString().isEmpty()) {
            Toast.makeText(SlelFormActivity.this, " Please choose your Dates",
                    Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            Date dateFrom = cal_from.getTime();
            Date dateTo = cal_to.getTime();

            long minusOneHour = (1000 * 60 * 60);
            current_date.setTime(current_date.getTime() - minusOneHour);

            System.out.println("****CURR DATE***" + current_date);
            System.out.println("****DATE FROM***" + dateFrom);
            System.out.println("****DATE TO***" + dateTo);

            if (dateFrom.before(current_date)) {
                Toast.makeText(SlelFormActivity.this, " Date from should not be in the past",
                        Toast.LENGTH_LONG).show();
                valid = false;
            }

            if (dateTo.before(current_date)) {
                Toast.makeText(SlelFormActivity.this, " Date to should not be in the past",
                        Toast.LENGTH_LONG).show();
                valid = false;
            }

            if (dateTo.before(dateFrom)) {
                Toast.makeText(SlelFormActivity.this, " Date from and to is pointing backwards",
                        Toast.LENGTH_LONG).show();
                valid = false;
            }
        }

        return valid;
    }
}
