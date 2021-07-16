package com.example.raillankatrack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MyLocationMap extends AppCompatActivity {
    SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private long interval=30000;
    private long fastestInterval=10000;
    private int priority=LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private Marker marker;
    private Double Latitude=0.0, Longtitude=0.0;
    private Boolean updatesOnFlag;
    Button sharenow, showLoc, stopLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location_map);
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        Toolbar toolbar = findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Location");
        toolbar.inflateMenu(R.menu.menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharenow = findViewById(R.id.sharenowBtn);
        showLoc=findViewById(R.id.showMyLoc);
        stopLoc=findViewById(R.id.stopMyLoc);


        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        client = LocationServices.getFusedLocationProviderClient(this);
        updatesOnFlag=false;


//        if (ActivityCompat.checkSelfPermission(MyLocationMap.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            //permission granted call the location fetching method
//            getCurrentLocation();
//        } else {
//            //permission denied
//            ActivityCompat.requestPermissions(MyLocationMap.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
//        }

        sharenow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyLocationMap.this, Current_Assignments.class));
            }
        });

        showLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoc.setClickable(false);
                showLoc.setVisibility(View.INVISIBLE);
                buildMap();
                stopLoc.setClickable(true);
                stopLoc.setVisibility(View.VISIBLE);
            }
        });

        stopLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLoc.setVisibility(View.INVISIBLE);
                stopLoc.setClickable(false);
                stopLocationUpdates();
                showLoc.setVisibility(View.VISIBLE);
                showLoc.setClickable(true);
            }
        });

    }

//    private void getCurrentLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            ActivityCompat.requestPermissions(MyLocationMap.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
//        }
//        Task<Location> task = client.getLastLocation();
//        task.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(final Location location) {
//                if(location !=null){
//                    //sync map
//                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
//                        @Override
//                        public void onMapReady(GoogleMap googleMap) {
//                            LatLng latLng=new LatLng(location.getLatitude(),
//                                    location.getLongitude());
//                            //create marker
//
//                            MarkerOptions options=new MarkerOptions().position(latLng).title("I am here");
//
//                            //zoom
//
//                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//                            googleMap.addMarker(options);
//
//                        }
//                    });
//                }
//            }
//        });
//
//    }

    public void  requestLocationUpdates(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            updatesOnFlag=true;
        }
    }

    public void buildLocationRequest(){
        locationRequest=new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
        locationRequest.setSmallestDisplacement(0);
    }

    public void buildMap(){
        buildLocationRequest();
        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Latitude=locationResult.getLastLocation().getLatitude();
                Longtitude=locationResult.getLastLocation().getLongitude();

                if(Latitude==0.0 && Longtitude==0.0){
                    requestLocationUpdates();
                }else{
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latLng=new LatLng(Latitude,Longtitude);
                            if(marker!=null){
                                marker.remove();
                            }
                            MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("My current Location");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            marker=googleMap.addMarker(markerOptions);
                            Toast.makeText(MyLocationMap.this, "Location=> Lat: "+Latitude+" Lng: "+Longtitude,Toast.LENGTH_LONG).show();

                        }
                    });
                }
            }
        };
        locationSettingsRequest();
    }



    private void locationSettingsRequest(){
        SettingsClient settingsClient=LocationServices.getSettingsClient(MyLocationMap.this);

        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        LocationSettingsRequest locationSettingsRequest=builder.build();

        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                requestLocationUpdates();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode= ((ApiException)e).getStatusCode();
                switch (statusCode){
                    case LocationSettingsStatusCodes
                            .RESOLUTION_REQUIRED:
                        int CHANGE_REQUEST_SETTINGS=214;
                        ResolvableApiException resolvableApiException=(ResolvableApiException)e;
                        try {
                            resolvableApiException.startResolutionForResult(MyLocationMap.this, CHANGE_REQUEST_SETTINGS);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                            Toast.makeText(MyLocationMap.this, ex.toString(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(MyLocationMap.this, "Location Settings cannot be enabled.", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(MyLocationMap.this, "Settings request was cancelled. unable to activate location settings", Toast.LENGTH_SHORT ).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==214){
            switch (resultCode){
                case Activity
                        .RESULT_OK:
                    requestLocationUpdates();
                break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(MyLocationMap.this, "Location is unable to display on the map", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void stopLocationUpdates(){
        if(updatesOnFlag){
            client.removeLocationUpdates(locationCallback);
            updatesOnFlag=false;
            Toast.makeText(MyLocationMap.this, "Location updates were stopped", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MyLocationMap.this, "Location updates are not enabled", Toast.LENGTH_LONG).show();
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if(requestCode==44){
//            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//
//                getCurrentLocation();
//            }
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if(updatesOnFlag){
            buildMap();
            Toast.makeText(MyLocationMap.this, "Resuming location", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(updatesOnFlag){
            client.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuHome:
                startActivity(new Intent(MyLocationMap.this, AppStartActivity.class));
                finish();
                break;
            case R.id.menuProfile:
                startActivity(new Intent(MyLocationMap.this, DriverProfile.class));
                finish();
                break;
            case R.id.menuLogout:
                SharedPrefManager.getInstance(MyLocationMap.this).logout();
                startActivity(new Intent(MyLocationMap.this, MainActivity.class));
                finish();
                break;
        }
        return true;
    }
}