package com.example.francismark.automated_slel;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.internal.IdTokenListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.InternalTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    // Firebase services
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static ValueEventListener postListener;
    //private FirebaseAnalytics mFirebaseAnalytics;

    // Declare UI variables
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mUserStatusField;

    // Initialize final variables
    private static final String TAG = "LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().getRoot();
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // VIEWS
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mUserStatusField = findViewById(R.id.status_field);

        // Auth user listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    System.out.println("****USER UID***" + mAuth.getCurrentUser().getUid() + "***");
                    userOnline();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    userOffline();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        //mDatabase.addValueEventListener(postListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
//        if (postListener != null) {
//            mDatabase.removeEventListener(postListener);
//        }
    }

    public void emailPasswordLogin(View view) {
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        Log.d(TAG, "Email: " + email);
        Log.d(TAG, "Password:" + password);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseMessaging.getInstance().subscribeToTopic("automated_slel");

                            userOnline();

//                            // Modify UI fields
//                            mUserStatusField.setText("User is logged in");
//                            mEmailField.setText("");;
//                            mPasswordField.setText("");
//
//                            // Hide fields
//                            findViewById(R.id.email).setVisibility(View.GONE);
//                            findViewById(R.id.password).setVisibility(View.GONE);
//                            findViewById(R.id.login_btn).setVisibility(View.GONE);

                            // Analytics for Email and Pass
//                            mFirebaseAnalytics.setUserId(user.getUid());
//
//                            Bundle params = new Bundle();
//                            params.putString("auth_method", "email password sign-in");
//                            params.putString("user_name", user.getDisplayName());
//                            mFirebaseAnalytics.logEvent("custom_event", params);
//
//                            Bundle bundle = new Bundle();
//                            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, user.getUid());
//                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, user.getDisplayName());
//                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "email password");
//                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void userOnline(){

        // Modify UI fields
        mUserStatusField.setText("User is logged in");
        mEmailField.setText("");;
        mPasswordField.setText("");

        // Hide fields
        findViewById(R.id.email).setVisibility(View.GONE);
        findViewById(R.id.password).setVisibility(View.GONE);
        findViewById(R.id.login_btn).setVisibility(View.GONE);

    }

    private void userOffline(){

        // Show fields
        mUserStatusField.setText("");
        findViewById(R.id.email).setVisibility(View.VISIBLE);
        findViewById(R.id.password).setVisibility(View.VISIBLE);
        findViewById(R.id.login_btn).setVisibility(View.VISIBLE);

    }



    public void logout(View view) {
        System.out.println("****Logout Method***");
        FirebaseMessaging.getInstance().unsubscribeFromTopic("automated_slel");
        mAuth.signOut();
        userOffline();
//        mUserStatusField.setText("");
//
//        // Show fields
//        findViewById(R.id.email).setVisibility(View.VISIBLE);
//        findViewById(R.id.password).setVisibility(View.VISIBLE);
//        findViewById(R.id.login_btn).setVisibility(View.VISIBLE);
    }

    public void createForm(View view) {
        Intent intent = new Intent(this, SlelFormActivity.class);
        startActivity(intent);
    }

    // Validate if email/password parameters have values
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }
}
