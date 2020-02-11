package com.example.bunprofunpro;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.example.bunprofunpro.MainActivity.CHANNEL_ID;

public class ReviewCheckWorker extends Worker {

    public ReviewCheckWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        Utilities.StudyQueue current;
        try {
            current = Utilities.buildStudyQueue();
        }
        catch (Exception e) {
            return Result.failure();
        }
        if (current != null) {
            Context context = getApplicationContext();
            if (current.reviewsAvailable != 0) {
                String title = context.getString(R.string.noty_title);
                String text;
                if (current.reviewsAvailable == 1) {
                    text = context.getString(R.string.noty_text1 + R.string.noty_text3);
                } else {
                    String reviewsAvailable = String.valueOf(current.reviewsAvailable);
                    text = context.getString(R.string.noty_text1)  + reviewsAvailable + context.getString(R.string.noty_text2);
                }
                showNotification(context, title, text);
            }
            return Result.success();

        } else {
            return Result.failure();
        }
    }



    public void showNotification(Context context, String title, String text){
// This method works so far.
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Integer notificationId = 12345;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(notificationId, builder.build());
    }
}