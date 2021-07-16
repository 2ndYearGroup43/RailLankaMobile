package com.example.raillankatrack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LiveLocation extends AppCompatActivity {
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    Button startSharing, endJourney, stopSharing;
// location sharing vars
    Boolean shareFlag;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;


    private long interval = 15000;
    private long fastestInterval= 5000;
    private int priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private Marker marker;
    private double Latitude=0.0, Longitude=0.0;

    //journey details
    private String journeyId;
    private Boolean endedFlag;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_location);

        //get data from the selected assignment
        Intent intent = getIntent();
        AssignmentItem assignment =intent.getParcelableExtra("AssignmentItem");//enter the name when parcelling onclick
        this.journeyId=assignment.getJourneyId();
        Toast.makeText(this, journeyId, Toast.LENGTH_SHORT).show();



        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        Toolbar toolbar = findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Location");
        toolbar.inflateMenu(R.menu.menu);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shareFlag=false;
        endedFlag=false;
        startSharing = findViewById(R.id.startSharing);
        stopSharing = findViewById(R.id.stopSharing);
        endJourney = findViewById(R.id.endJourney);


        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.live_google_map);
        client = LocationServices.getFusedLocationProviderClient(LiveLocation.this);


        startSharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSharing.setClickable(false);
                startSharing.setVisibility(View.INVISIBLE);

                shareLocation();

                stopSharing.setVisibility(View.VISIBLE);
                stopSharing.setClickable(true);
            }
        });

        stopSharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSharing.setClickable(false);
                stopSharing.setVisibility(View.INVISIBLE);
                setOffLine(journeyId, Latitude, Longitude, new onResponseCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        try {
                            if(jsonObject.getBoolean("journey")){
                                stopLocationUpdates();
                                startSharing.setVisibility(View.VISIBLE);
                                startSharing.setClickable(true);
                            }else{
                                stopLocationUpdates();
                                Toast.makeText(LiveLocation.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                Toast.makeText(LiveLocation.this, "Journey Ended", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LiveLocation.this, AppStartActivity.class));
                                endedFlag=true;
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(String msg) {
                        Toast.makeText(LiveLocation.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        endJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endJourney(journeyId, Latitude, Longitude, new onResponseCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        try {//onSuccess mean no error checked from endjourney response
                            stopLocationUpdates();
                            Toast.makeText(LiveLocation.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            Toast.makeText(LiveLocation.this, "Journey Ended", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LiveLocation.this, AppStartActivity.class));
                            endedFlag=true;
                            finish();
                            return;
                        } catch (JSONException e) {
                            Toast.makeText(LiveLocation.this, e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(String msg) {
                        Toast.makeText(LiveLocation.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(LiveLocation.this, "Journey couldn't be ended please try again!!", Toast.LENGTH_SHORT).show();

            }
        });


    }


    //start fusedloaction

    public void requestLocationUpdate(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            shareFlag=true;
            //location request for the requestsed updates, callback fucnton for the retrievd updates, looper object to manage msg queue in callback
        }
    }

    public void buildLocationRequest(){
        locationRequest= new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
        locationRequest.setPriority(priority);
        locationRequest.setSmallestDisplacement(0);
    }


    //build apigapiclient

    public void shareLocation(){
        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this);
        buildLocationRequest();

        locationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Latitude=locationResult.getLastLocation().getLatitude();
                Longitude=locationResult.getLastLocation().getLongitude();

                if(Latitude==0 && Longitude==0){
                    requestLocationUpdate();
                }else{
                    sendLocationUpdates(journeyId, Latitude, Longitude, new onResponseCallback() {
                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            try {
                                if(jsonObject.getBoolean("journey")){
                                    Toast.makeText(LiveLocation.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }else{
                                    stopLocationUpdates();
                                    Toast.makeText(LiveLocation.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(LiveLocation.this, "Journey Ended", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LiveLocation.this, AppStartActivity.class));
                                    endedFlag=true;
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(LiveLocation.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFail(String msg) {
                            Toast.makeText(LiveLocation.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latLng=new LatLng(Latitude, Longitude);
                            if(marker!=null){
                                marker.remove();  //this prevents repeating the marker aall over the plaaace
                            }
                            MarkerOptions markerOptions= new MarkerOptions().position(latLng).title("I'm here"); //create a marker with specified options
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15)); //set zoom
                            marker=googleMap.addMarker(markerOptions); //add the marker to the map
                            Toast.makeText(LiveLocation.this, "Map location updated Lat: "+Latitude+", Long: "+Longitude+".",Toast.LENGTH_SHORT ).show();
                        }
                    });
                }
            }
        };
        locationSettingsRequest();    //start the stuff here
    }



    //after this function need to create the sendlocationupdates and also the other one next need to come up with the flag thing

    private void locationSettingsRequest(){
        SettingsClient settingsClient=LocationServices.getSettingsClient(this); //client to talk with the settings

        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder(); //creating a builder object to a location settings request
        builder.addLocationRequest(locationRequest); //add the desired location request to the builder
        builder.setAlwaysShow(true);//state that the location is required to continue the activity (wordings dialog)
        LocationSettingsRequest locationSettingsRequest=builder.build();//build the settings request

        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                requestLocationUpdate();//on success the the location request updates function is called (all settings are satisfied)
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {//settings not satisfied
                int statusCode= ((ApiException) e).getStatusCode();
                switch (statusCode){
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED: //but can be resolved by enabling the dialog
                        int REQUEST_CHECK_SETTINGS=214;
                        ResolvableApiException resolvableApiException=(ResolvableApiException) e;
                        try {
                            //shows the dialog
                            //checks the result in the on activity result defined below this function (requestlocationupdates is called)
                            resolvableApiException.startResolutionForResult(LiveLocation.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                            Toast.makeText(LiveLocation.this, "Unable to exeute the request", Toast.LENGTH_SHORT).show(); //igonre the error kiyla thynne doc wala
                        }
                        break;
                    case  LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(LiveLocation.this, "Location settings inadequest cannot be reolved at the moment", Toast.LENGTH_SHORT).show();//cannot be resolved by settigs change
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(LiveLocation.this, "Settings request was cancelled. You are unable to share location now", Toast.LENGTH_SHORT).show();  //location settings request is cancelled
            }
        });

        //add comments to the above method(done) and  create save location method below
        //added the onactivity result to handle the dialog result(location settings)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==214){ //location request dialog code
            switch (resultCode){
                case Activity
                        .RESULT_OK: //user said ok
                    requestLocationUpdate();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "Location is not unabled user cancelled", Toast.LENGTH_SHORT).show(); //user cancelled
                    break;
                default:
                    break;
            }
        }
    }

    private void stopLocationUpdates(){
        if(shareFlag){
            mFusedLocationClient.removeLocationUpdates(locationCallback);
            shareFlag=false;
        }
    }

    private void sendLocationUpdates(final String journeyId, final Double Lat, final Double Long, final onResponseCallback callback){
        String url=getResources().getString(R.string.URLROOT)+"drivermobilelocations/saveLocationData";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("error")){
                        callback.onFail(jsonObject.getString("message"));
                    }else{
                        callback.onSuccess(jsonObject);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFail(e.toString());

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFail(error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params=new HashMap<>();
                params.put("journeyId", journeyId);
                params.put("lat", Lat.toString());
                params.put("lng", Long.toString());
                return params;
            }
        };


        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(LiveLocation.this).addToRequestQueue(request);
    }

    public void setOffLine(final String journeyId, final Double lat, final Double lng, final onResponseCallback callback){
        String url = getResources().getString(R.string.URLROOT)+"driverMobileLocations/stopSharing";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message=jsonObject.getString("message");
                    Toast.makeText(LiveLocation.this, message, Toast.LENGTH_SHORT).show();
                    callback.onSuccess(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFail(e.toString());
                    Toast.makeText(LiveLocation.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFail(error.toString());
                Toast.makeText(LiveLocation.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params=new HashMap<>();
                params.put("journeyId", journeyId);
                params.put("lastLat", lat.toString());
                params.put("lastLng", lng.toString());
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(LiveLocation.this).addToRequestQueue(request);
    }


    public void endJourney(final String journeyId, final Double lat, final Double lng, final onResponseCallback callback){
        String url = getResources().getString(R.string.URLROOT)+"driverMobileLocations/endJourney";
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("error")){
                        callback.onFail(jsonObject.getString("message"));
                    }else{
                        callback.onSuccess(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LiveLocation.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LiveLocation.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params=new HashMap<>();
                params.put("journeyId", journeyId);
                params.put("lastLat", lat.toString());
                params.put("lastLng", lng.toString());
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(LiveLocation.this).addToRequestQueue(request);
    }




//callback function for the string requests with the server
    public interface onResponseCallback{
        public void onSuccess(JSONObject jsonObject);

        public void onFail(String msg);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!endedFlag){
            setOffLine(journeyId, Latitude, Longitude, new onResponseCallback() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    try {
                        if(jsonObject.getBoolean("journey")){
                            stopLocationUpdates();
                            startSharing.setVisibility(View.VISIBLE);
                            startSharing.setClickable(true);
                        }else{
                            stopLocationUpdates();
                            Toast.makeText(LiveLocation.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            Toast.makeText(LiveLocation.this, "Journey Ended", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LiveLocation.this, AppStartActivity.class));
                            endedFlag=true;
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String msg) {
                    Toast.makeText(LiveLocation.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);   //loads a menu into the current view
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //defines onitems selected on the view
        switch (item.getItemId()){
            case R.id.menuHome:
                startActivity(new Intent(LiveLocation.this, AppStartActivity.class));
                finish();
                break;
            case R.id.menuProfile:
                startActivity(new Intent(LiveLocation.this, DriverProfile.class));
                finish();
                break;
            case R.id.menuLogout:
                SharedPrefManager.getInstance(LiveLocation.this).logout();
                startActivity(new Intent(LiveLocation.this, MainActivity.class));
                finish();
                break;
        }
        return true;
    }




}