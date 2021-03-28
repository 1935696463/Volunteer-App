package com.example.volunteeringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class AvailabilityTime extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.availability_time);

        final CheckBox monday = findViewById(R.id.monday);
        final CheckBox tuesday = findViewById(R.id.tuesday);
        final CheckBox wednesday = findViewById(R.id.wednesday);
        final CheckBox thursday = findViewById(R.id.thursday);
        final CheckBox friday = findViewById(R.id.friday);
        final CheckBox saturday = findViewById(R.id.saturday);
        final CheckBox sunday = findViewById(R.id.sunday);

        final String[] week = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        final ArrayList<CheckBox> weekCheckBox = new ArrayList<>();
        weekCheckBox.add(monday);
        weekCheckBox.add(tuesday);
        weekCheckBox.add(wednesday);
        weekCheckBox.add(thursday);
        weekCheckBox.add(friday);
        weekCheckBox.add(saturday);
        weekCheckBox.add(sunday);


        final Data app = (Data) getApplication();
        ArrayList availabilityTime = (ArrayList) app.getUser().get("availabilityTime");
        for (int i = 0; i < 7; i++) {
            if (Objects.requireNonNull(availabilityTime).contains(week[i])) {
                weekCheckBox.get(i).setChecked(true);
            }
        }

        Toolbar mToolbar = findViewById(R.id.availability_time_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("users").document(app.getUser().getId());
                for (int i = 0; i < 7; i++) {
                    docRef.update("availabilityTime", FieldValue.arrayRemove(week[i]));
                }
                for (int i = 0; i < 7; i++) {
                    if (weekCheckBox.get(i).isChecked()) {
                        docRef.update("availabilityTime", FieldValue.arrayUnion(week[i]));
                    }
                }

                Intent intent = new Intent(AvailabilityTime.this, Index.class);
                intent.putExtra("target", "setting");
                startActivity(intent);
            }
        });
    }
}
