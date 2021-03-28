package com.example.volunteeringapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class SearchResult extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        DisplayActivities.setNum(1);
        Data app = (Data) getApplication();
        Task<QuerySnapshot> task = app.getTask();
        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
            ConstraintLayout cL = findViewById(R.id.search_result);
            TextView textView = new TextView(SearchResult.this);
            DisplayActivities.displayActivities(cL, textView, document, SearchResult.this, 2);
        }
    }
}
