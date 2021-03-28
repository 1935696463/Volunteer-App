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
import androidx.fragment.app.Fragment;

import com.example.volunteeringapp.AvailabilityTime;
import com.example.volunteeringapp.ChangePassword;
import com.example.volunteeringapp.Data;
import com.example.volunteeringapp.MyProfile;
import com.example.volunteeringapp.R;
import com.example.volunteeringapp.VolunteerManagement;
import com.example.volunteeringapp.VolunteeringHistory;
import com.example.volunteeringapp.VolunteeringTime;

import java.util.Objects;

public class SettingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView settingUserName = Objects.requireNonNull(getActivity()).findViewById(R.id.userName);
        Data app = (Data) getActivity().getApplication();
        settingUserName.setText(Objects.requireNonNull(app.getUser().getId()));

        Button myProfile = getActivity().findViewById(R.id.myProfile);
        Button volunteeringHistory = getActivity().findViewById(R.id.volunteeringHistory);
        Button changePassword = getActivity().findViewById(R.id.changePassword);
        Button volunteeringTimeBtn = getActivity().findViewById(R.id.volunteeringTimeBtn);
        Button availabilityTime = getActivity().findViewById(R.id.availabilityTime);
        Button volunteerManagement = getActivity().findViewById(R.id.volunteerManagement);

        myProfile.setOnClickListener(new ButtonListener());
        volunteeringHistory.setOnClickListener(new ButtonListener());
        changePassword.setOnClickListener(new ButtonListener());
        volunteeringTimeBtn.setOnClickListener(new ButtonListener());
        availabilityTime.setOnClickListener(new ButtonListener());
        volunteerManagement.setOnClickListener(new ButtonListener());

        if ((Boolean) Objects.requireNonNull(app.getUser().get("administrator"))) {
            myProfile.setVisibility(View.INVISIBLE);
            volunteeringHistory.setVisibility(View.INVISIBLE);
            changePassword.setVisibility(View.INVISIBLE);
            volunteeringTimeBtn.setVisibility(View.INVISIBLE);
            availabilityTime.setVisibility(View.INVISIBLE);

            volunteerManagement.setVisibility(View.VISIBLE);
        }
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.myProfile:
                    intent = new Intent(getActivity(), MyProfile.class);
                    break;
                case R.id.volunteeringHistory:
                    intent = new Intent(getActivity(), VolunteeringHistory.class);
                    break;
                case R.id.changePassword:
                    intent = new Intent(getActivity(), ChangePassword.class);
                    break;
                case R.id.volunteeringTimeBtn:
                    intent = new Intent(getActivity(), VolunteeringTime.class);
                    break;
                case R.id.availabilityTime:
                    intent = new Intent(getActivity(), AvailabilityTime.class);
                    break;
                case R.id.volunteerManagement:
                    intent = new Intent(getActivity(), VolunteerManagement.class);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + v.getId());
            }
            startActivity(intent);
        }
    }
}
