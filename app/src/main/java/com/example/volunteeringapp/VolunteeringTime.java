package com.example.volunteeringapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VolunteeringTime extends AppCompatActivity {
    Handler mHandler;
    TextView totalVolunteeringTime;
    int num;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteering_time);

        Toolbar mToolbar = findViewById(R.id.volunteering_Time_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VolunteeringTime.this, Index.class);
                intent.putExtra("target", "setting");
                startActivity(intent);
            }
        });

        totalVolunteeringTime = findViewById(R.id.totalVolunteeringTime);
        final Data app = (Data) getApplication();
        final ArrayList activityParticipated = (ArrayList) app.getUser().get("activityParticipated");
        if (activityParticipated == null) {
            Toast.makeText(VolunteeringTime.this, "No volunteering history", Toast.LENGTH_SHORT).show();
        } else {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            app.setPreviousView(findViewById(R.id.totalVolunteeringTime));
            DisplayActivities.setNum(2);

            ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("VolunteeringTimePool").build();
            ExecutorService fixedThreadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 0L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), threadFactory);

            final Message msg = Message.obtain();
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for (Object activityId : Objects.requireNonNull(activityParticipated)) {
                        db.collection("activities")
                                .whereEqualTo(FieldPath.documentId(), activityId)
                                .whereEqualTo("status", 2)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                            if ((Long) Objects.requireNonNull(document.get("status")) == 2) {
                                                ConstraintLayout cL = findViewById(R.id.volunteeringTime);
                                                TextView activity = new TextView(VolunteeringTime.this);
                                                DisplayActivities.displayActivities(cL, activity, document, VolunteeringTime.this, 1);

                                                msg.arg1 += ((Long) Objects.requireNonNull(document.get("timeRequired"))).intValue();
                                            }

                                            if (num == activityParticipated.size() - 1) {
                                                mHandler.sendMessage(msg);
                                            }

                                        }
                                        num++;
                                    }
                                });
                    }
                }
            });

            mHandler = new MHandler(VolunteeringTime.this);
        }
    }

    static class MHandler extends Handler {
        WeakReference<VolunteeringTime> mActivity;

        MHandler(VolunteeringTime activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            VolunteeringTime activity = mActivity.get();
            if (activity != null) {
                String str = "Total time: " + msg.arg1 + " hours";
                activity.totalVolunteeringTime.setText(str);
            }
        }
    }
}
