package com.example.capstone.alert;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.capstone.Search.Bridge;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.capstone.R;

import java.util.ArrayList;
import java.util.List;

public class BackgroundWorkBridge extends Worker {
    private static final String TAG = "MyWorker";
    private static final String CHANNEL_ID = "bridge_alerts";
    private final Context context;

    public BackgroundWorkBridge(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String formattedDate = "2024-05-26"; // formattedDate 값을 설정합니다.

        db.collection("bridge")
                .document(formattedDate)
                .collection("data")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Bridge> bridgeList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Bridge bridge = document.toObject(Bridge.class);
                                String fludLevelString = bridge.getFludLevel();
                                try {
                                    float fludLevel = Float.parseFloat(fludLevelString);
                                    Log.d(TAG, String.valueOf(fludLevel));
                                    if (fludLevel >= 6.0) { // 조건 확인
                                        showNotification(bridge); // 조건 만족 시 알림 트리거
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e(TAG, "Error parsing flood level", e);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return Result.success();
    }

    private void showNotification(Bridge bridge) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Flood Alert")
                .setContentText("Flood level is " + bridge.getFludLevel() + " at " + bridge.getSiteName())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(10, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Bridge Alerts";
            String description = "Notifications for high flood levels";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
