/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.francismark.automated_slel.Notification;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.francismark.automated_slel.R;
import com.example.francismark.automated_slel.ViewerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";

    // Variables
    public String email;
    public String type;
    public String team;
    public String name;
    public String ref_num;
    public String reason;
    public String date_from;
    public String date_to;

    // Firebase variables
    private FirebaseAuth mAuth;

    private static MyFirebaseMessagingService instance = null;
    private static Activity newActivity;

    private MyFirebaseMessagingService(Activity activity) {
        newActivity = activity;
    }
    public MyFirebaseMessagingService() {
        newActivity = null;
    }

    static public MyFirebaseMessagingService getInstance(Activity activity) {
        if (instance == null) {
            instance = new MyFirebaseMessagingService(activity);
            return instance;
        } else {
            return instance;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("***FirebaseMessagingService created****");
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        FirebaseUser user = mAuth.getCurrentUser();

        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " +
                remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());

        Map<String,String> map = remoteMessage.getData();
        email = map.get("email");
        type = map.get("type");
        team = map.get("team");
        name = map.get("name");
        ref_num = map.get("ref_num");
        reason = map.get("reason");
        date_from = map.get("date_from");
        date_to = map.get("date_to");

        System.out.println("Email: " + email + "***type: " + type + "***team: " + team + "***name: " + name + "***ref_num: " + ref_num
                + "***reason: " + reason + "***date_from: " + date_from + "***date_to: " + date_to);

        System.out.println("****isUser***" + (user != null));

        if(user != null) {
            System.out.println("****isEmail***" + email.contains(user.getEmail()));
            if (email.contains(user.getEmail())){
                createNotification();
            }
        }
    }

    // Notification
    public void createNotification(){

        // Variables
        NotificationManager mgr;
        PendingIntent pendingInt;

        if(newActivity == null){
            mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }else {
            mgr = (NotificationManager) newActivity.getSystemService(NOTIFICATION_SERVICE);
        }
        String CHANNEL_ID = "1234";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            mgr.createNotificationChannel(mChannel);
        }

        // Create builder
        NotificationCompat.Builder notifBuild;

        // Create intent the notif is tapped
        if(newActivity == null){
            // Create builder
            notifBuild =  new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

            Intent intent = new Intent(this, ViewerActivity.class);
            intent.putExtra("type", type );
            intent.putExtra("team", team);
            intent.putExtra("name", name);
            intent.putExtra("ref_num", ref_num);
            intent.putExtra("reason", reason);
            intent.putExtra("date_from", date_from);
            intent.putExtra("date_to", date_to);
            System.out.println("***NOTIF***type: " + type + "***team: " + team + "***name: " + name + "***ref_num: " + ref_num
                    + "***reason: " + reason + "***date_from: " + date_from + "***date_to: " + date_to);
            pendingInt = PendingIntent.getActivity(this, 1234, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else {
            // Create builder
            notifBuild =  new NotificationCompat.Builder(newActivity.getApplicationContext(), CHANNEL_ID);

            Intent intent = new Intent(newActivity.getApplicationContext(), ViewerActivity.class);
            intent.putExtra("type", type );
            intent.putExtra("team", team);
            intent.putExtra("name", name);
            intent.putExtra("ref_num", ref_num);
            intent.putExtra("reason", reason);
            intent.putExtra("date_from", date_from);
            intent.putExtra("date_to", date_to);
            pendingInt = PendingIntent.getActivity(newActivity.getApplicationContext(), 1234, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Set builder required items
        notifBuild.setSmallIcon(R.drawable.ic_sample_notification);
        notifBuild.setContentTitle("Autamated-SLEL");
        notifBuild.setContentText("A resource from " + team + " has filed an " + type + ". Click to view details");

        // Set cancel when user taps it
        notifBuild.setAutoCancel(true);

        // Set pending intent
        notifBuild.setContentIntent(pendingInt);

        // Set visibility
        notifBuild.setVisibility(Notification.VISIBILITY_PUBLIC);

        // Call build function
        Notification notification = notifBuild.build();
        //NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mgr.notify(1234, notification);

    }
}
