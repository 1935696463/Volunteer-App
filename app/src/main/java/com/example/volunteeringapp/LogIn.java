package com.example.volunteeringapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LogIn extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button logIn = findViewById(R.id.logIn);

        final EditText logInUserNameEditText = findViewById(R.id.logInUserName);
        final EditText logInPasswordEditText = findViewById(R.id.logInPassword);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference users = db.collection("users");

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = logInUserNameEditText.getText().toString();
                final String logInPassword = logInPasswordEditText.getText().toString();

                if (userName.isEmpty()) {
                    Toast.makeText(LogIn.this, "Please enter username!", Toast.LENGTH_SHORT).show();
                } else if (logInPassword.isEmpty()) {
                    Toast.makeText(LogIn.this, "Please enter password!", Toast.LENGTH_SHORT).show();
                } else {
                    users.document(userName)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (Objects.requireNonNull(document).exists()) {
                                            if (logInPassword.equals(document.get("password"))) {
                                                Data app = (Data) getApplication();
                                                app.setUser(document);
                                                Toast.makeText(LogIn.this, "Log in successfully!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent();
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.setClass(LogIn.this,Index.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(LogIn.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(LogIn.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.d("LogIn", "" + task.getException());
                                    }
                                }
                            });
                }
            }
        });

//        logInUserNameEditText.setText(R.string.test_user_name);
//        logInPasswordEditText.setText(R.string.test_password);
//        logIn.performClick();

        Button turnToRegister = findViewById(R.id.turnToRegister);

        turnToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, Register.class);
                startActivity(intent);
            }
        });
    }
}
