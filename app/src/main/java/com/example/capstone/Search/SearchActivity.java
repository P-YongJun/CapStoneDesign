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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "FirestoreExample";
    private TableLayout tableLayout;
    private EditText searchInput;
    private Button searchButton;
    private Button toBridgeButton;

    LocalDate currentDate = LocalDate.now();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    String formattedDate = currentDate.format(formatter);

    private List<Rain> previousRainList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_information);

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

        Button toBridgeButton = findViewById(R.id.toBridgeButton);
        toBridgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, BridgeActivity.class);
                startActivity(intent);
                //Toast.makeText(SearchActivity.this, "test test test tesdt testasrsfaesfafeeeeeeaeffffffffffffffffff", Toast.LENGTH_LONG).show();
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("rain")
                .document(formattedDate)
                .collection("data")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Rain> rainList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Rain rain = document.toObject(Rain.class);
                                rainList.add(rain);
                            }
                            addRowsToTable(rainList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void fetchAndDisplayData(String searchText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        db.collection("rain")
                .document(searchText)
                .collection("data")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Rain> rainList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Rain rain = document.toObject(Rain.class);
                                rainList.add(rain);
                            }
                            addRowsToTable(rainList);
                            compareAndDisplayChanges(rainList); // Ensure comparison is done after data fetch
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void compareAndDisplayChanges(List<Rain> newRainList) {
        boolean isLevel6Changed = false;
        boolean isLevel12Changed = false;
        boolean isaccRainChanged = false;

        for (Rain newRain : newRainList) {
            for (Rain oldRain : previousRainList) {
                if (newRain.getClientId().equals(oldRain.getClientId())) {
                    if (!newRain.getLevel6().equals(oldRain.getLevel6())) { // Corrected comparison
                        isLevel6Changed = true; // Corrected flag setting
                    }
                    if (!newRain.getLevel12().equals(oldRain.getLevel12())) {
                        isLevel12Changed = true;
                    }
                    if (!newRain.getAccRain().equals(oldRain.getAccRain())) {
                        isaccRainChanged = true;
                    }
                }
            }
        }

        if (isLevel6Changed && isLevel12Changed) {
            showAlert("위험", "비가 위험 수준으로 많이 오니 사용자 여러분들은 주의 바랍니다.");
        } else if (isaccRainChanged) {
            showAlert("경고", "비가 경고 수준으로 오는 중이니 사용자 여러분들은 주의 바랍니다.");
        }
    }

    private void showAlert(String title, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                builder.setTitle("accrain")
                        .setMessage("강수량 변경")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private void addRowsToTable(List<Rain> rainList) {
        tableLayout.removeAllViews();  // 기존 행 제거

        TableRow headerRow = new TableRow(this);

        TextView headerclientName = new TextView(this);
        headerclientName.setText("지역");
        headerclientName.setPadding(8, 8, 8, 8);
        headerRow.addView(headerclientName);

        TextView headeraccRain = new TextView(this);
        headeraccRain.setText("강수량");
        headeraccRain.setPadding(8, 8, 8, 8);
        headerRow.addView(headeraccRain);

        TextView headercreatedAt = new TextView(this);
        headercreatedAt.setText("측정 시간");
        headercreatedAt.setPadding(8, 8, 8, 8);
        headerRow.addView(headercreatedAt);

        tableLayout.addView(headerRow);

        for (Rain rain : rainList) {
            TableRow tableRow = new TableRow(this);

            TextView clientName = new TextView(this);
            clientName.setText(rain.getClientName());
            clientName.setPadding(8, 8, 8, 8);
            tableRow.addView(clientName);

            TextView accRain = new TextView(this);
            accRain.setText(rain.getAccRain());
            accRain.setPadding(8, 8, 8, 8);
            tableRow.addView(accRain);

            TextView createdAt = new TextView(this);
            createdAt.setText(rain.getAccRainDt());
            createdAt.setPadding(8, 8, 8, 8);
            tableRow.addView(createdAt);

            tableLayout.addView(tableRow);
        }
    }
}