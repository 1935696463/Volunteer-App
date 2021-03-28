package com.example.volunteeringapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PublishActivity extends AppCompatActivity {
    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_activity);

        final String[] ageGroupArray = getResources().getStringArray(R.array.age_group);
        final String[] categoryArray = getResources().getStringArray(R.array.activity_prefer);

        final EditText title = findViewById(R.id.publishTitle);
        final EditText location = findViewById(R.id.publishLocation);
        final TextView time = findViewById(R.id.publishTime);
        final EditText detail = findViewById(R.id.publishDetail);
        final EditText numberOfVolunteersNeeded = findViewById(R.id.publishNumberOfVolunteersNeeded);
        final Spinner ageGroup = findViewById(R.id.publishAgeGroup);
        final Spinner category = findViewById(R.id.publishCategory);
        final EditText timeRequired = findViewById(R.id.publishTimeRequired);
        final EditText otherRequirements = findViewById(R.id.publishOtherRequirements);
        Button publish = findViewById(R.id.publishActivity);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Data app = (Data) getApplication();

        final DatePickerDialog datePickerDialog = new DatePickerDialog(PublishActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1 + 1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        final TimePickerDialog timePickerDialog = new TimePickerDialog(PublishActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);
                calendar.set(Calendar.SECOND, 0);

                time.setText(calendar.getTime().toString());
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
                datePickerDialog.show();
            }
        });

        if ((Boolean) Objects.requireNonNull(app.getUser().get("administrator"))) {
            publish.setText("Update");

            Date date = app.getActivity().getDate("time");
            calendar.setTime(date);
            title.setText(Objects.requireNonNull(app.getActivity().get("title")).toString());
            location.setText(Objects.requireNonNull(app.getActivity().get("location")).toString());
            datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            timePickerDialog.updateTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            time.setText(calendar.getTime().toString());
            detail.setText(Objects.requireNonNull(app.getActivity().get("detail")).toString());
            numberOfVolunteersNeeded.setText(Objects.requireNonNull(app.getActivity().get("numberOfVolunteersNeeded")).toString());
            ageGroup.setSelection(((Long) Objects.requireNonNull(app.getActivity().get("ageGroupNeeded"))).intValue());
            category.setSelection(((Long) Objects.requireNonNull(app.getActivity().get("category"))).intValue());
            timeRequired.setText(Objects.requireNonNull(app.getActivity().get("timeRequired")).toString());
            otherRequirements.setText(Objects.requireNonNull(app.getActivity().get("otherRequirements")).toString());

            publish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newTitle = title.getText().toString();
                    String newLocation = location.getText().toString();
                    Timestamp newTime = new Timestamp(calendar.getTime());
                    String newDetail = detail.getText().toString();
                    int newNUmberOfVolunteersNeeded = Integer.parseInt(numberOfVolunteersNeeded.getText().toString());
                    int ageGroupPosition = getPosition(ageGroupArray, ageGroup.getSelectedItem().toString());
                    int categoryPosition = getPosition(categoryArray, category.getSelectedItem().toString());
                    int newTimeRequired = Integer.parseInt(timeRequired.getText().toString());
                    String newOtherRequirements = otherRequirements.getText().toString();

                    Map<String, Object> data = new HashMap<>();
                    data.put("title", newTitle);
                    data.put("location", newLocation);
                    data.put("time", newTime);
                    data.put("detail", newDetail);
                    data.put("numberOfVolunteersNeeded", newNUmberOfVolunteersNeeded);
                    data.put("ageGroupNeeded", ageGroupPosition);
                    data.put("category", categoryPosition);
                    data.put("timeRequired", newTimeRequired);
                    data.put("otherRequirements", newOtherRequirements);

                    db.collection("activities")
                            .document(app.getActivity().getId())
                            .set(data, SetOptions.merge());

                    Toast.makeText(PublishActivity.this, "Activity updated successfully!", Toast.LENGTH_SHORT).show();
                    app.updateActivity();

                    Intent intent = new Intent(PublishActivity.this, Index.class);
                    startActivity(intent);
                }
            });
        } else {
            publish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("title", title.getText().toString());
                    data.put("comments", new ArrayList<String>());
                    data.put("volunteers", new ArrayList<String>());
                    data.put("checkInCode", "");
                    data.put("status", 0);
                    data.put("numberOfCurrentVolunteers", 0);
                    data.put("location", location.getText().toString());
                    data.put("time", new Timestamp(calendar.getTime()));
                    data.put("detail", detail.getText().toString());
                    data.put("numberOfVolunteersNeeded", Integer.parseInt(numberOfVolunteersNeeded.getText().toString()));
                    data.put("ageGroupNeeded", getPosition(ageGroupArray, ageGroup.getSelectedItem().toString()));
                    data.put("category", getPosition(categoryArray, category.getSelectedItem().toString()));
                    data.put("timeRequired", Integer.parseInt(timeRequired.getText().toString()));
                    data.put("otherRequirements", otherRequirements.getText().toString());
                    data.put("publisher", app.getUser().getId());

                    db.collection("activities")
                            .document()
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(PublishActivity.this, "Publish successfully!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(PublishActivity.this, Index.class);
                                    startActivity(intent);
                                }
                            });
                }
            });
        }
    }

    int getPosition(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (value.equals(array[i])) {
                return i;
            }
        }
        return 0;
    }
}
