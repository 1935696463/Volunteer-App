package com.example.volunteeringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author ShiqianYan
 */

public class ActivityDetail extends AppCompatActivity {
    private static final String NOCV = "numberOfCurrentVolunteers";
    private static final String NOVN = "numberOfVolunteersNeeded";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        final Data app = (Data) getApplication();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String status = "";

        TextView detailStatus = findViewById(R.id.detailStatus);
        TextView detailTitle = findViewById(R.id.detailTitle);
        TextView detailDetail = findViewById(R.id.detailDetail);
        final EditText checkInCode = findViewById(R.id.checkInCode);
        Button checkInButton = findViewById(R.id.checkInButton);
        final EditText commentText = findViewById(R.id.comment);
        Button commitComment = findViewById(R.id.commitComment);
        final Button signUp = findViewById(R.id.signUp);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) detailDetail.getLayoutParams();
        params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        detailDetail.setLayoutParams(params);

        int num = 1;
        switch (Objects.requireNonNull(app.getActivity().get("status")).toString()) {
            case "0":
                status = "Preparing";
                signUp.setVisibility(View.VISIBLE);

                ArrayList volunteers = (ArrayList) app.getActivity().get("volunteers");
                for (Object volunteer : Objects.requireNonNull(volunteers)) {
                    if (volunteer.toString().equals(app.getUser().getId())) {
                        signUp.setEnabled(false);
                        String txt = "Already signed up!";
                        signUp.setText(txt);
                    }
                }

                signUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (app.getActivity().get(NOCV) == app.getActivity().get(NOVN)) {
                            Toast.makeText(ActivityDetail.this, "Quota is full", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> data = new HashMap<>();
                            data.put("numberOfCurrentVolunteers", (Long) Objects.requireNonNull(app.getActivity().get("numberOfCurrentVolunteers")) + 1);
                            db.collection("activities")
                                    .document(app.getActivity().getId())
                                    .set(data, SetOptions.merge());

                            data.clear();
                            data.put("numberOfVolunteersNeeded", (Long) Objects.requireNonNull(app.getActivity().get("numberOfVolunteersNeeded")) - 1);
                            db.collection("activities")
                                    .document(app.getActivity().getId())
                                    .set(data, SetOptions.merge());

                            db.collection("activities")
                                    .document(app.getActivity().getId())
                                    .update("volunteers", FieldValue.arrayUnion(app.getUser().getId()));

                            db.collection("users")
                                    .document(app.getUser().getId())
                                    .update("activityParticipated", FieldValue.arrayUnion(app.getActivity().getId()));

                            app.updateUser();
                            app.updateActivity();
                            Toast.makeText(ActivityDetail.this, "Signed up successfully!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ActivityDetail.this, Index.class);
                            startActivity(intent);
                        }
                    }
                });

                break;
            case "1":
                status = "Processing";
                checkInCode.setVisibility(View.VISIBLE);
                checkInButton.setVisibility(View.VISIBLE);

                checkInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String code = checkInCode.getText().toString();
                        if (Objects.requireNonNull(app.getActivity().get("checkInCode")).toString().equals(code)) {
                            Toast.makeText(ActivityDetail.this, "Checked in successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ActivityDetail.this, "Checked in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case "2":
                status = "Completed";
                commentText.setVisibility(View.VISIBLE);
                commitComment.setVisibility(View.VISIBLE);
                ArrayList comments = (ArrayList) app.getActivity().get("comments");

                for (Object comment : Objects.requireNonNull(comments)) {
                    ConstraintLayout cL = findViewById(R.id.activity_detail);
                    TextView activityDetail = new TextView(ActivityDetail.this);
                    activityDetail.setId(num);
                    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    activityDetail.setLayoutParams(layoutParams);
                    activityDetail.setText(comment.toString());
                    cL.addView(activityDetail);

                    ConstraintSet set = new ConstraintSet();
                    if (num == 1) {
                        app.setPreviousView(findViewById(R.id.commitComment));
                    }
                    set.connect(activityDetail.getId(), ConstraintSet.TOP, app.getPreviousView().getId(), ConstraintSet.BOTTOM);
                    set.connect(activityDetail.getId(), ConstraintSet.LEFT, app.getPreviousView().getId(), ConstraintSet.LEFT);
                    set.connect(activityDetail.getId(), ConstraintSet.RIGHT, app.getPreviousView().getId(), ConstraintSet.RIGHT);
                    set.constrainWidth(activityDetail.getId(), (int) (getResources().getDisplayMetrics().widthPixels * 0.8));
                    set.setMargin(activityDetail.getId(), ConstraintSet.TOP, 60);
                    app.setPreviousView(activityDetail);
                    set.applyTo(cL);

                    num++;
                }

                commitComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cmt = app.getUser().get("userName") +
                                ": " +
                                commentText.getText().toString();
                        db.collection("activities")
                                .document(app.getActivity().getId())
                                .update("comments", FieldValue.arrayUnion(cmt));
                        Toast.makeText(ActivityDetail.this, "Commented successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            default:
        }

        detailStatus.setText(status);
        detailTitle.setText(Objects.requireNonNull(app.getActivity().get("title")).toString());
        detailDetail.setText(Objects.requireNonNull(app.getActivity().get("detail")).toString());

        Button deleteActivity = findViewById(R.id.deleteActivity);
        deleteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final Data app = (Data) getApplication();

                ArrayList volunteerParticipated = (ArrayList) app.getActivity().get("volunteers");
                if (volunteerParticipated != null) {
                    for (final Object volunteerId : volunteerParticipated) {
                        db.collection("users")
                                .document(volunteerId.toString())
                                .update("activityParticipated", FieldValue.arrayRemove(app.getActivity().getId()));
                    }
                }

                db.collection("activities")
                        .document(app.getActivity().getId())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ActivityDetail.this, "Deleted successully", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ActivityDetail.this, Index.class);
                                intent.putExtra("target", "setting");
                                startActivity(intent);
                            }
                        });
            }
        });

        Button updateActivity = findViewById(R.id.updateActivity);
        updateActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDetail.this, PublishActivity.class);
                startActivity(intent);
            }
        });

        if ((Boolean) Objects.requireNonNull(app.getUser().get("administrator"))) {
            signUp.setVisibility(View.INVISIBLE);
            deleteActivity.setVisibility(View.VISIBLE);
            updateActivity.setVisibility(View.VISIBLE);
        }
    }
}
