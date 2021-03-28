package com.example.volunteeringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ModifyInformation extends AppCompatActivity {
    String userName;
    Boolean isAdministrator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_email);

        Intent intent = getIntent();
        final String information = intent.getStringExtra("information");

        final EditText modifyEmail = findViewById(R.id.modifyEmailText);
        final Data app = (Data) getApplication();
        isAdministrator = (Boolean) Objects.requireNonNull(app.getUser().get("administrator"));
        userName = isAdministrator ? app.getVolunteer().getId() : app.getUser().getId();
        modifyEmail.setText(isAdministrator ? Objects.requireNonNull(app.getVolunteer().get(Objects.requireNonNull(information))).toString() : Objects.requireNonNull(app.getUser().get(Objects.requireNonNull(information))).toString());

        Button saveModifyEmail = findViewById(R.id.saveModifyEmail);
        saveModifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> data = new HashMap<>();
                data.put(information, modifyEmail.getText().toString());
                db.collection("users")
                        .document(userName)
                        .set(data, SetOptions.merge());
                Toast.makeText(ModifyInformation.this, "Change saved!", Toast.LENGTH_SHORT).show();

                app.updateUser();

                Intent intent = new Intent(ModifyInformation.this, Index.class);
                intent.putExtra("target", "setting");
                startActivity(intent);
            }
        });
    }
}
