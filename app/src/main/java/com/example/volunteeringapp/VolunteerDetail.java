package com.example.volunteeringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.fragment.AreYouSureFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VolunteerDetail extends AppCompatActivity {
    Spinner ageGroup;
    Spinner keySkills;
    Spinner activityPrefer;

    int ageGroupPosition;
    int keySkillsPosition;
    int activityPreferPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer_detail);

        TextView userName = findViewById(R.id.activityDetailUserName);
        TextView password = findViewById(R.id.activityDetailPassword);
        TextView email = findViewById(R.id.volunteerDetailEmail);
        ageGroup = findViewById(R.id.volunteerDetailAgeGroup);
        keySkills = findViewById(R.id.volunteerDetailKeySkills);
        activityPrefer = findViewById(R.id.volunteerDetailActivityPrefer);
        Button deleteUser = findViewById(R.id.deleteUser);

        userName.setOnClickListener(new TextViewListener());
        password.setOnClickListener(new TextViewListener());
        email.setOnClickListener(new TextViewListener());

        final Data app = (Data) getApplication();
        ageGroupPosition = ((Long) Objects.requireNonNull(app.getVolunteer().get("ageGroup"))).intValue();
        keySkillsPosition = ((Long) Objects.requireNonNull(app.getVolunteer().get("keySkills"))).intValue();
        activityPreferPosition = ((Long) Objects.requireNonNull(app.getVolunteer().get("activityPrefer"))).intValue();

        String emailStringBuilder = "Email: " + app.getVolunteer().get("email");
        String userNameStringBuilder = "UserName: " + app.getVolunteer().getId();
        String passwordStringBuilder = "Password: " + app.getVolunteer().get("password");
        userName.setText(userNameStringBuilder);
        password.setText(passwordStringBuilder);
        email.setText(emailStringBuilder);
        ageGroup.setSelection(ageGroupPosition);
        keySkills.setSelection(keySkillsPosition);
        activityPrefer.setSelection(activityPreferPosition);

        ageGroup.setOnItemSelectedListener(new SpinnerListener());
        keySkills.setOnItemSelectedListener(new SpinnerListener());
        activityPrefer.setOnItemSelectedListener(new SpinnerListener());

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                ArrayList activityParticipated = (ArrayList) app.getVolunteer().get("activityParticipated");
                if (activityParticipated != null) {
                    for (final Object activityId : activityParticipated) {
                        db.collection("activities")
                                .document(activityId.toString())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot document = task.getResult();

                                        db.collection("activities")
                                                .document(activityId.toString())
                                                .update("volunteers", FieldValue.arrayRemove(app.getVolunteer().getId()));

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("numberOfCurrentVolunteers", (Long) Objects.requireNonNull(Objects.requireNonNull(document).get("numberOfCurrentVolunteers")) - 1);
                                        db.collection("activities")
                                                .document(activityId.toString())
                                                .set(data, SetOptions.merge());

                                        data.clear();
                                        data.put("numberOfVolunteersNeeded", (Long) Objects.requireNonNull(document.get("numberOfVolunteersNeeded")) + 1);
                                        db.collection("activities")
                                                .document(activityId.toString())
                                                .set(data, SetOptions.merge());
                                    }
                                });
                    }
                }

                db.collection("users")
                        .document(app.getVolunteer().getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(VolunteerDetail.this, "Deleted successfully", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(VolunteerDetail.this, Index.class);
                                intent.putExtra("target", "setting");
                                startActivity(intent);
                            }
                        });
            }
        });

        Toolbar mToolbar = findViewById(R.id.volunteer_detail_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VolunteerDetail.this, Index.class);
                intent.putExtra("target", "setting");
                startActivity(intent);
            }
        });
    }

    private class TextViewListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(VolunteerDetail.this, ModifyInformation.class);
            switch (v.getId()) {
                case R.id.volunteerDetailEmail:
                    intent.putExtra("information", "email");
                    break;
                case R.id.activityDetailUserName:
                    intent.putExtra("information", "userName");
                    break;
                case R.id.activityDetailPassword:
                    intent.putExtra("information", "password");
                    break;
                default:
            }
            startActivity(intent);
        }
    }

    public class SpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position != ageGroupPosition && position != keySkillsPosition && position != activityPreferPosition) {
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);

                switch (parent.getId()) {
                    case R.id.volunteerDetailAgeGroup:
                        if (position != ageGroupPosition) {
                            bundle.putString("spinner", "ageGroup");
                        }
                        break;
                    case R.id.volunteerDetailKeySkills:
                        if (position != keySkillsPosition) {
                            bundle.putString("spinner", "keySkills");
                        }
                        break;
                    case R.id.volunteerDetailActivityPrefer:
                        if (position != activityPreferPosition) {
                            bundle.putString("spinner", "activityPrefer");
                        }
                        break;
                    default:
                }

                AreYouSureFragment areYouSureFragment = new AreYouSureFragment();
                areYouSureFragment.show(getSupportFragmentManager(), "AreYouSure");
                areYouSureFragment.setArguments(bundle);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
