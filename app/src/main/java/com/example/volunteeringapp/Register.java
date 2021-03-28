package com.example.volunteeringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        final String[] ageGroupArray = getResources().getStringArray(R.array.age_group);
        final String[] keySkillsArray = getResources().getStringArray(R.array.key_skills);
        final String[] activityPreferArray = getResources().getStringArray(R.array.activity_prefer);
        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText registerUserNameEditText = findViewById(R.id.registerUserName);
                final String registerUserName = registerUserNameEditText.getText().toString();
                EditText registerPasswordEditText = findViewById(R.id.registerPassword);
                final String registerPassword = registerPasswordEditText.getText().toString();
                EditText confirmPasswordEditText = findViewById(R.id.confirmPassword);
                final String confirmPassword = confirmPasswordEditText.getText().toString();
                EditText emailEditText = findViewById(R.id.registerEmail);
                final String email = emailEditText.getText().toString();
                Spinner selectAgeGroup = findViewById(R.id.selectAgeGroup);
                final String ageGroup = selectAgeGroup.getSelectedItem().toString();
                Spinner selectkeySkills = findViewById(R.id.selectKeySkills);
                final String keySkills = selectkeySkills.getSelectedItem().toString();
                Spinner selectActivityPrefer = findViewById(R.id.selectActivityPrefer);
                final String activityPrefer = selectActivityPrefer.getSelectedItem().toString();
                Spinner selectAvailabilityTime = findViewById(R.id.selectAvailabilityTime);
                final ArrayList<String> availabilityTime = new ArrayList<>();
                availabilityTime.add(selectAvailabilityTime.getSelectedItem().toString());
                final int ageGroupPosition = getPosition(ageGroupArray, ageGroup);
                final int keySkillsPosition = getPosition(keySkillsArray, keySkills);
                final int activityPreferPosition = getPosition(activityPreferArray, activityPrefer);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                final CollectionReference users = db.collection("users");
                final DocumentReference docRef = users.document(registerUserName);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (Objects.requireNonNull(document).exists()) {
                            Toast.makeText(Register.this, "User already exists!", Toast.LENGTH_SHORT).show();
                        } else if (!registerPassword.equals(confirmPassword)) {
                            Toast.makeText(Register.this, "Inconsistent password", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> user = new HashMap<>();
                            user.put("userName", registerUserName);
                            user.put("password", registerPassword);
                            user.put("email", email);
                            user.put("ageGroup", ageGroupPosition);
                            user.put("keySkills", keySkillsPosition);
                            user.put("activityPrefer", activityPreferPosition);
                            user.put("availabilityTime", availabilityTime);
                            user.put("administrator", false);
                            for (int i = 0; i < 7; i++) {
                                docRef.update("availabilityTime", FieldValue.arrayUnion(0));
                            }

                            users.document(registerUserName).set(user);
                            Toast.makeText(Register.this, "Register successful!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Register.this, LogIn.class);
                            startActivity(intent);
                        }
                    }
                });

            }
        });
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
