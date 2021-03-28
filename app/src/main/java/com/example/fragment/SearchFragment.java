package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.volunteeringapp.ActivityDetail;
import com.example.volunteeringapp.Data;
import com.example.volunteeringapp.DisplayActivities;
import com.example.volunteeringapp.PublishActivity;
import com.example.volunteeringapp.R;
import com.example.volunteeringapp.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ShiqianYan
 */

public class SearchFragment extends Fragment {
    private ArrayList<Integer> idColl = new ArrayList<>();
    private ArrayList<DocumentSnapshot> charActivities = new ArrayList<>();
    private ArrayList<DocumentSnapshot> timeActivities = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Data app = (Data) Objects.requireNonNull(getActivity()).getApplication();
        app.setPreviousView(getActivity().findViewById(R.id.turnToSearch));
        DisplayActivities.setNum(2);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("activities")
                .whereEqualTo("status", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            ConstraintLayout cL = Objects.requireNonNull(getActivity()).findViewById(R.id.search);
                            TextView activity = new TextView(getActivity());

                            idColl = DisplayActivities.displayActivities(cL, activity, document, getActivity(), 0);
                        }
                    }
                });

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("SearchFragmentPool").build();
        ExecutorService fixedThreadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 0L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), threadFactory);

        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                db.collection("activities")
                        .orderBy("title")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                charActivities = (ArrayList<DocumentSnapshot>) Objects.requireNonNull(task.getResult()).getDocuments();
                            }
                        });
            }
        });

        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                db.collection("activities")
                        .orderBy("time")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                timeActivities = (ArrayList<DocumentSnapshot>) Objects.requireNonNull(task.getResult()).getDocuments();
                            }
                        });
            }
        });

        TextView sortByChar = getActivity().findViewById(R.id.sortByChar);
        sortByChar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFilling(charActivities);
            }
        });

        TextView sortByTime = getActivity().findViewById(R.id.sortByTime);
        sortByTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFilling(timeActivities);
            }
        });

        TextView turnToSearch = getActivity().findViewById(R.id.turnToSearch);
        turnToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        Button publish = getActivity().findViewById(R.id.publishCategory);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PublishActivity.class);
                startActivity(intent);
            }
        });
    }

    private void changeFilling(ArrayList<DocumentSnapshot> arrayList) {
        int i = 0;
        final Data app = (Data) Objects.requireNonNull(getActivity()).getApplication();

        for (final DocumentSnapshot document : arrayList) {
            if ((Long) Objects.requireNonNull(document.get("status")) == 0) {
                TextView textView = Objects.requireNonNull(getActivity()).findViewById(idColl.get(i));

                String detail = Objects.requireNonNull(document.get("detail")).toString();
                String shortenedDetail = detail.substring(0, Math.min(detail.length(), 100)) + " ...";
                String activity = "Title: " + document.get("title") + "\n\nDetail: " + shortenedDetail + "\n\nTime: " + document.getDate("time");

                textView.setText(activity);
                i++;

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        app.setActivity(document);
                        Intent intent = new Intent(getActivity(), ActivityDetail.class);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
