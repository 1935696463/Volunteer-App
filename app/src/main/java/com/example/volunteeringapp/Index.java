package com.example.volunteeringapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fragment.MapFragment;
import com.example.fragment.SearchFragment;
import com.example.fragment.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Index extends AppCompatActivity {
    SettingFragment settingFragment = new SettingFragment();
    MapFragment mapFragment = new MapFragment();
    SearchFragment searchFragment = new SearchFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String target = intent.getStringExtra("target");

        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        settingFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.indexContainer, searchFragment);
        fragmentTransaction.add(R.id.indexContainer, mapFragment);
        fragmentTransaction.add(R.id.indexContainer, settingFragment);

        fragmentTransaction.hide(mapFragment);
        fragmentTransaction.hide(settingFragment);
        fragmentTransaction.commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            String currentFragment = "searchFragment";

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                hideFragment(currentFragment);
                switch (item.getItemId()) {
                    case R.id.navigationSearch:
                        fragmentTransaction.show(searchFragment);
                        fragmentTransaction.commit();
                        currentFragment = "searchFragment";
                        return true;
                    case R.id.navigationMap:
                        fragmentTransaction.show(mapFragment);
                        fragmentTransaction.commit();
                        currentFragment = "savedFragment";
                        return true;
                    case R.id.navigationSetting:
                        fragmentTransaction.show(settingFragment);
                        fragmentTransaction.commit();
                        currentFragment = "settingFragment";
                        return true;
                    default:
                }
                return false;
            }

            void hideFragment(String currentFragment) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (currentFragment) {
                    case "searchFragment":
                        fragmentTransaction.hide(searchFragment);
                        fragmentTransaction.commit();
                        break;
                    case "savedFragment":
                        fragmentTransaction.hide(mapFragment);
                        fragmentTransaction.commit();
                        break;
                    case "settingFragment":
                        fragmentTransaction.hide(settingFragment);
                        fragmentTransaction.commit();
                        break;
                    default:
                }
            }
        });

        if ("setting".equals(target)) {
            bottomNavigationView.setSelectedItemId(R.id.navigationSetting);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.navigationSearch);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        MapFragment.mLocationPermissionGranted = false;
        switch (requestCode) {
            case MapFragment.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MapFragment.mLocationPermissionGranted = true;
                }
            }
        }
        mapFragment.updateLocationUI();
    }
}