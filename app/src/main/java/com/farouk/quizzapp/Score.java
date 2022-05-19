package com.farouk.quizzapp;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Score extends AppCompatActivity {


    private TextView addressTv;
    private ProgressBar progressBar;
    private Intent intent1;
    private Button showOnMapBtn;
    private int score;
    private double lat, lng;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        progressBar = findViewById(R.id.progress_bar);
        TextView scoreText = findViewById(R.id.scoretext);
        addressTv = findViewById(R.id.address);

        Button retryBtn = findViewById(R.id.retry);
        Button logoutBtn = findViewById(R.id.logout);
        showOnMapBtn = findViewById(R.id.showOnMap);

        intent1 = getIntent();
        score = intent1.getIntExtra("currentScore", 0);

        scoreText.setText((String.valueOf(score)));
        progressBar.setProgress(score * 10);

        //getLocation();

        showOnMapBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Score.this, ShowOnMap.class);
            intent.putExtra("Lat", lat);
            intent.putExtra("lng", lng);
            startActivity(intent);

        });

        retryBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Score.this, Quizzes.class);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Score.this, MainActivity.class);
            intent.putExtra("logout", "You are logged out!");
            startActivity(intent);
        });

    }

    public void getLocation() {
        @SuppressLint("MissingPermission") ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                                fusedLocationClient.getLastLocation()
                                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                try {
                                                    Geocoder geocoder;
                                                    List<Address> addresses;
                                                    geocoder = new Geocoder(Score.this, Locale.getDefault());
                                                    lat = location.getLatitude();
                                                    lng = location.getLongitude();
                                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                                    String address = addresses.get(0).getAddressLine(0);
                                                    addressTv.setText(address);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                Toast.makeText(this, "Only approximate location access granted!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Access to Location denied!", Toast.LENGTH_SHORT).show();
                            }
                        }
                );

        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });


    }
}

