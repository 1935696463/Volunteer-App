package com.example.volunteeringapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class VolunteerManagement extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer_management);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DisplayActivities.setNum(1);
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            ConstraintLayout cL = findViewById(R.id.volunteer_management);
                            TextView volunteer = new TextView(VolunteerManagement.this);

                            if (!"Administrator".equals(document.getId())) {
                                DisplayActivities.displayActivities(cL, volunteer, document, VolunteerManagement.this, 3);
                            }
                        }
                    }
                });
    }
}
