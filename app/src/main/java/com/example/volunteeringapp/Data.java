package com.example.volunteeringapp;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Data extends Application {
    private DocumentSnapshot user;
    private DocumentSnapshot activity;
    private DocumentSnapshot volunteer;

    public DocumentSnapshot getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(DocumentSnapshot volunteer) {
        this.volunteer = volunteer;
    }

    private Task<QuerySnapshot> task;

    public DocumentSnapshot getActivity() {
        return activity;
    }

    public void setActivity(DocumentSnapshot activity) {
        this.activity = activity;
    }

    private View previousView;

    public View getPreviousView() {
        return previousView;
    }

    public void setPreviousView(View previousView) {
        this.previousView = previousView;
    }

    public DocumentSnapshot getUser() {
        return user;
    }

    public void setUser(DocumentSnapshot user) {
        this.user = user;
    }

    public void updateUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(getUser().getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        setUser(document);
                    }
                });
    }

    public void updateActivity() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("activities")
                .document(getActivity().getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        setActivity(document);
                    }
                });
    }

    public Task<QuerySnapshot> getTask() {
        return task;
    }

    public void setTask(Task<QuerySnapshot> task) {
        this.task = task;
    }
}
