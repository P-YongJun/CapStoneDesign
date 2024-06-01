package com.example.capstone.Search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.capstone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BridgeActivity extends AppCompatActivity {

    private static final String TAG = "FirestoreExample";
    LocalDate currentDate = LocalDate.now();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    String formattedDate = currentDate.format(formatter);

    private TableLayout tableLayout;
    private EditText searchInput;
    private Button searchButton;
    private Button toSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_bridge); // 이 부분은 레이아웃 파일에 맞게 수정해야 합니다.

        tableLayout = findViewById(R.id.tableLayout);
        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);

        // 검색 버튼 클릭 리스너 설정
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchInput.getText().toString();
                Log.d(TAG, "검색어: " + searchText);

                // 원하는 검색 로직 추가
                fetchAndDisplayData(searchText);
            }
        });

        Button toSearchButton = findViewById(R.id.toSearchButton);
        toSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(BridgeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                                bridgeList.add(bridge);
                            }
                            addRowsToTable(bridgeList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void fetchAndDisplayData(String searchText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bridge")
                .document(searchText)
                .collection("data")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Bridge> bridgeList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Bridge bridge = document.toObject(Bridge.class);
                                bridgeList.add(bridge);
                            }
                            addRowsToTable(bridgeList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }


    private void addRowsToTable(List<Bridge> bridgeList) {
        tableLayout.removeAllViews();  // 기존 행 제거

        // 테이블 헤더 추가
        TableRow headerRow = new TableRow(this);

        TextView headerSiteName = new TextView(this);
        headerSiteName.setText("위치");
        headerSiteName.setPadding(8, 8, 8, 8);
        headerRow.addView(headerSiteName);

        TextView headerFludLevel = new TextView(this);
        headerFludLevel.setText("강수량");
        headerFludLevel.setPadding(8, 8, 8, 8);
        headerRow.addView(headerFludLevel);

        TextView headerObsrTime = new TextView(this);
        headerObsrTime.setText("측정 시간");
        headerObsrTime.setPadding(8, 8, 8, 8);
        headerRow.addView(headerObsrTime);

        tableLayout.addView(headerRow);

        // 데이터 행 추가
        for (Bridge bridge : bridgeList) {
            TableRow tableRow = new TableRow(this);

            // 모든 필드를 표시하는 TextView 추가
            TextView siteName = new TextView(this);
            siteName.setText(bridge.getSiteName());
            siteName.setPadding(8, 8, 8, 8);
            tableRow.addView(siteName);

            TextView fludLevel = new TextView(this);
            fludLevel.setText(bridge.getFludLevel());
            fludLevel.setPadding(8, 8, 8, 8);
            tableRow.addView(fludLevel);

            TextView obsrTime = new TextView(this);
            obsrTime.setText(bridge.getObsrTime());
            obsrTime.setPadding(8, 8, 8, 8);
            tableRow.addView(obsrTime);

            tableLayout.addView(tableRow);
        }
    }


}
