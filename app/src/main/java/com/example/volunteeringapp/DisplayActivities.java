package com.example.volunteeringapp;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class DisplayActivities {
    private static int num;

    public static void setNum(int num) {
        DisplayActivities.num = num;
    }

    private static ArrayList<Integer> idColl = new ArrayList<>();

    public static ArrayList<Integer> displayActivities(ConstraintLayout cL, TextView textView, final DocumentSnapshot document, final Activity context, final int type) {
        final Data app = (Data) context.getApplication();
        String activity = "";
        String detail;
        String shortenedDetail = "";

        textView.setId(num);
        idColl.add(num);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setBackground(ContextCompat.getDrawable(context, R.drawable.history_textview));
        textView.setGravity(Gravity.CENTER);
        if (type != 3) {
            detail = Objects.requireNonNull(document.get("detail")).toString();
            shortenedDetail = detail.substring(0, Math.min(detail.length(), 100)) + " ...";
        }

        switch (type) {
            case 0:
                activity = "Title: " + Objects.requireNonNull(document).get("title") + "\n\nDetail: " + shortenedDetail + "\n\nTime: " + document.getDate("time");
                break;
            case 1:
                activity = "Title: " + Objects.requireNonNull(document).get("title") + "\n\nTotal time: " + document.get("timeRequired") + " hours";
                break;
            case 2:
                activity = document.get("title") + "";
                break;
            case 3:
                activity = document.getId() + "";
                break;
            default:
        }

        textView.setText(activity);
        cL.addView(textView);

        ConstraintSet set = new ConstraintSet();
        if (num == 1) {
            set.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        } else {
            set.connect(textView.getId(), ConstraintSet.TOP, app.getPreviousView().getId(), ConstraintSet.BOTTOM);
        }
        set.connect(textView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        set.connect(textView.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);

        set.constrainHeight(textView.getId(), ConstraintLayout.LayoutParams.WRAP_CONTENT);
        set.constrainWidth(textView.getId(), (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8));
        set.setMargin(textView.getId(), ConstraintSet.TOP, 40);
        app.setPreviousView(textView);
        set.applyTo(cL);
        num++;

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 3) {
                    app.setVolunteer(document);
                    Intent intent = new Intent(context, VolunteerDetail.class);
                    context.startActivity(intent);
                } else {
                    app.setActivity(document);
                    Intent intent = new Intent(context, ActivityDetail.class);
                    context.startActivity(intent);
                }
            }
        });

        return idColl;
    }
}
