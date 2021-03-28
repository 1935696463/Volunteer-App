package com.example.volunteeringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class VolunteeringHistory extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer_history);

        Toolbar mToolbar = findViewById(R.id.volunteering_history_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VolunteeringHistory.this, Index.class);
                intent.putExtra("target", "setting");
                startActivity(intent);
            }
        });

        Data app = (Data) getApplication();
        ArrayList activityParticipated = (ArrayList) app.getUser().get("activityParticipated");
        if (activityParticipated == null) {
            Toast.makeText(VolunteeringHistory.this, "No volunteering history", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DisplayActivities.setNum(2);
            app.setPreviousView(findViewById(R.id.volunteering_history_toolbar));
            for (Object activityId : activityParticipated) {
                db.collection("activities")
                        .document(activityId.toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                ConstraintLayout cL = findViewById(R.id.volunteer_history);
                                TextView volunteerHistory = new TextView(VolunteeringHistory.this);
                                DocumentSnapshot document = task.getResult();

                                DisplayActivities.displayActivities(cL, volunteerHistory, document, VolunteeringHistory.this, 0);
                            }
                        });
            }
        }
    }
}
