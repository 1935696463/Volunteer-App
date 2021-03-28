package com.example.volunteeringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import com.example.fragment.AreYouSureFragment;

public class MyProfile extends AppCompatActivity {
    TextView email;
    Spinner ageGroup;
    Spinner keySkills;
    Spinner activityPrefer;

    int ageGroupPosition;
    int keySkillsPosition;
    int activityPreferPosition;

    Data app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);

        email = findViewById(R.id.email);
        ageGroup = findViewById(R.id.ageGroup);
        keySkills = findViewById(R.id.keySkills);
        activityPrefer = findViewById(R.id.activityPrefer);

        app = (Data) getApplication();

        ageGroupPosition = ((Long) Objects.requireNonNull(app.getUser().get("ageGroup"))).intValue();
        keySkillsPosition = ((Long) Objects.requireNonNull(app.getUser().get("keySkills"))).intValue();
        activityPreferPosition = ((Long) Objects.requireNonNull(app.getUser().get("activityPrefer"))).intValue();

        String stringBuilder = "Email: " + app.getUser().get("email");
        email.setText(stringBuilder);

        ageGroup.setSelection(ageGroupPosition);
        keySkills.setSelection(keySkillsPosition);
        activityPrefer.setSelection(activityPreferPosition);

        ageGroup.setOnItemSelectedListener(new SpinnerListener());
        keySkills.setOnItemSelectedListener(new SpinnerListener());
        activityPrefer.setOnItemSelectedListener(new SpinnerListener());

        Toolbar mToolbar = findViewById(R.id.my_profile_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfile.this, Index.class);
                intent.putExtra("target", "setting");
                startActivity(intent);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfile.this, ModifyInformation.class);
                intent.putExtra("information", "email");
                startActivity(intent);
            }
        });
    }

    public class SpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position != ageGroupPosition && position != keySkillsPosition && position != activityPreferPosition) {
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);

                switch (parent.getId()) {
                    case R.id.ageGroup:
                        if (position != ageGroupPosition) {
                            bundle.putString("spinner", "ageGroup");
                        }
                        break;
                    case R.id.keySkills:
                        if (position != keySkillsPosition) {
                            bundle.putString("spinner", "keySkills");
                        }
                        break;
                    case R.id.activityPrefer:
                        if (position != activityPreferPosition) {
                            bundle.putString("spinner", "activityPrefer");
                        }
                        break;
                    default:
                }

                AreYouSureFragment areYouSureFragment = new AreYouSureFragment();
                areYouSureFragment.show(getSupportFragmentManager(), "AreYouSure");
                areYouSureFragment.setArguments(bundle);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
