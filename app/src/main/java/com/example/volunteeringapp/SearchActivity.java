package com.example.volunteeringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        final TextView searchBox = findViewById(R.id.searchBox);
        Button searchButton = findViewById(R.id.searchButton);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Data app = (Data) getApplication();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTxt = searchBox.getText().toString();
                db.collection("activities")
                        .whereArrayContains("keys", searchTxt)
                        .whereEqualTo("status", 0)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult() != null) {
                                    app.setTask(task);
                                    Intent intent = new Intent(SearchActivity.this, SearchResult.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SearchActivity.this, "No result!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
