package com.example.volunteeringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChangePassword extends AppCompatActivity {
    EditText newPassword;
    EditText confirmPassword;
    Button changePasswordSubmit;
    EditText currentPassword;
    Data app;
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.newConfirmPassword);
        changePasswordSubmit = findViewById(R.id.changePasswordSubmit);
        currentPassword = findViewById(R.id.currentPassword);
        app = (Data) getApplication();
        intent = getIntent();

        if (intent.getIntExtra("source", 0) == 1) {
            ConstraintLayout cL = findViewById(R.id.change_password);
            ConstraintSet set = new ConstraintSet();
            set.clone(cL);

            set.setMargin(newPassword.getId(), ConstraintSet.TOP, 0);
            set.setMargin(changePasswordSubmit.getId(), ConstraintSet.TOP, 0);
            set.applyTo(cL);

            currentPassword.setVisibility(View.INVISIBLE);
            findViewById(R.id.forgot).setVisibility(View.INVISIBLE);
        }

        changePasswordSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.getIntExtra("source", 0) == 0) {
                    if (Objects.requireNonNull(app.getUser().get("password")).toString().equals(currentPassword.getText().toString())) {
                        changePassword();
                    } else {
                        Toast.makeText(ChangePassword.this, "Invalid current password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    changePassword();
                }
            }
        });

        TextView forgotPassword = findViewById(R.id.forgot);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangePassword.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    void changePassword() {
        Data app = (Data) getApplication();

        if (newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put("password", newPassword.getText().toString());
            db.collection("users").document(app.getUser().getId())
                    .set(data, SetOptions.merge());
            Toast.makeText(ChangePassword.this, "Password changed successful!", Toast.LENGTH_SHORT).show();

            app.updateUser();

            Intent intent = new Intent(ChangePassword.this, Index.class);
            startActivity(intent);
        } else {
            Toast.makeText(ChangePassword.this, "Inconsistent password", Toast.LENGTH_SHORT).show();
        }
    }
}
