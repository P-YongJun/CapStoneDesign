package com.example.capstone.alert;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.capstone.Search.Rain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.capstone.R;

import java.util.ArrayList;
import java.util.List;

public class BackgroundWorkRain extends Worker {
    private static final String TAG = "MyWorker";
    private static final String CHANNEL_ID = "RainLevel";
    private final Context context;

    public BackgroundWorkRain(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context= context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("YourTag", "호출은 성공");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String formattedDate = "2024-05-26"; // formattedDate 값을 설정합니다.

        db.collection("rain")
                .document(formattedDate)
                .collection("data")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Rain> bridgeList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Rain Rain = document.toObject(Rain.class);
                                String fludLevelString = Rain.getLevel6();
                                Log.d("YourTag", "accRain level string: " + fludLevelString);
                                try {
                                    float Level6 = Float.parseFloat(fludLevelString);
                                    Log.d("YourTag", "accRain Level6:"+ Level6);
                                    if (Level6 >= 6.0) {
                                        showNotification(Rain);
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

    private void showNotification(Rain rain) {
        Log.d("Notification", "showNotification method called");
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Rain WARNING")
                .setContentText("Rain Level is " + rain.getLevel6() + " at " + rain.getLevel6())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }



    private void createNotificationChannel() {
        Log.d("Notification", "Creating notification channel");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Rain ";
            String description = "Notifications for high accRain";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
