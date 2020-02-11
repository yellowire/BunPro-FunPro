package com.example.bunprofunpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.work.Configuration;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static String apiKey;
    public static String CHANNEL_ID = "bunprofunprointhesunpro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Boolean keyEmpty = needKeyCheck();
        if (!keyEmpty) {  // Do we still need the user's API key?

            InitializeWorkerDebug(this);
            setContentView(R.layout.activity_main);
            setValues(); // Load and display user-specific info from API

            createNotificationChannel();
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.cancelAll(); // Remove any notifications that were from FunPro
            SetupWorker(this); // Turn on background service for push notifications
        }
    }


    public void InitializeWorkerDebug(Context context) {
        Configuration.Builder MyConfigBuilder = new Configuration.Builder();
        WorkManager.initialize(context,
                MyConfigBuilder.setMinimumLoggingLevel(android.util.Log.DEBUG)
                        .build()
        );
    }


    public boolean needKeyCheck() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        apiKey = pref.getString("apiKey", null);
        if (apiKey == null) {
            Intent intent = new Intent(getApplicationContext(), GetApiKey.class);
            startActivity(intent);
            return true;
        }
        return false;
    }


    public void setValues() {
        Utilities.StudyQueue studyQueue;

        try {
            studyQueue = Utilities.buildStudyQueue();
        } catch (Exception e) {
            Log.e(e.getClass().getName(), e.getMessage(), e.getCause());
            return;
        }

        String username = studyQueue.username;
        Integer revavail = studyQueue.reviewsAvailable;
        String nextrevs = studyQueue.nextReviewDate;
        Integer nexthour = studyQueue.reviewsNextHour - revavail;
        Integer nextday = studyQueue.reviewsNextDay - revavail;

        TextView userName = findViewById(R.id.userName);
        userName.setText(username);
        TextView nextHour = findViewById(R.id.hourValue);
        nextHour.setText(String.valueOf(nexthour));
        TextView nextDay = findViewById(R.id.dayValue);
        nextDay.setText(String.valueOf(nextday));
        TextView availRevs = findViewById(R.id.revNum);
        availRevs.setText(String.valueOf(revavail) + " Reviews Available");

        String nextreview;
        if (revavail != 0) {
            nextreview = "Now!";

            ConstraintLayout constraints = findViewById(R.id.hidden);
            constraints.setVisibility(1);
        } else {
            long dv = Long.valueOf(nextrevs) * 1000;
            Date df = new java.util.Date(dv);
            String nextdate = new SimpleDateFormat("MMM d").format(df);
            String nexttime = new SimpleDateFormat("h:mma").format(df);
            nextreview = nextdate + ", " + nexttime;
        }
        TextView availValue = findViewById(R.id.availValue);
        availValue.setText(nextreview);
    }


    public void onReviews(View v) {
        Intent intent = new Intent(this, Reviews.class);
        startActivity(intent);
    }


    public static void SetupWorker(Context context) {
        androidx.work.Constraints constraints = new androidx.work.Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(ReviewCheckWorker.class, 1, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .setInitialDelay(1, TimeUnit.HOURS)
                        .addTag("ReviewCheckWorker")
                        .build();

        WorkManager.getInstance(context)
                .enqueue(saveRequest);
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}