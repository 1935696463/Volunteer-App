package com.example.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.volunteeringapp.Data;
import com.example.volunteeringapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AreYouSureFragment extends DialogFragment {
    private String userName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.are_you_sure, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Data app = (Data) Objects.requireNonNull(getActivity()).getApplication();
        if ((Boolean) Objects.requireNonNull(app.getUser().get("administrator"))) {
            userName = app.getVolunteer().getId();
        } else {
            userName = app.getUser().getId();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure?")
                .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        int position = Objects.requireNonNull(getArguments()).getInt("position");

                        Map<String, Object> data = new HashMap<>();
                        data.put(getArguments().getString("spinner"), position);
                        db.collection("users").document(Objects.requireNonNull(userName))
                                .set(data, SetOptions.merge());
                        Toast.makeText(getActivity(), "Change saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null);
        return builder.create();
    }
}
