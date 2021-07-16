package com.example.raillankatrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

public class AppStartActivity extends AppCompatActivity {


    ImageView logoutBtn, profileBtn, myLocation, pastAss, liveAss, help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        final SharedPreferences sharedPreferences=getSharedPreferences("UserInfo", MODE_PRIVATE);
        //check if logged in
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        Toolbar toolbar=findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        toolbar.inflateMenu(R.menu.menu);
        String login;

        help=findViewById(R.id.help);
        logoutBtn=findViewById(R.id.logoutBtn);
        profileBtn=findViewById(R.id.profileBtn);
        liveAss=findViewById(R.id.liveAss);
        pastAss=findViewById(R.id.pastAss);
        myLocation=findViewById(R.id.myLocation);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(AppStartActivity.this).logout();
                startActivity(new Intent(AppStartActivity.this, MainActivity.class));
                finish();
            }
        });
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppStartActivity.this, DriverProfile.class));
            }
        });
        liveAss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppStartActivity.this, Current_Assignments.class));
            }
        });

        pastAss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppStartActivity.this, Past_Assignments.class));
            }
        });
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppStartActivity.this, MyLocationMap.class));
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppStartActivity.this, CallStationActivity.class));
            }
        });


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
                startActivity(new Intent(AppStartActivity.this, AppStartActivity.class));
                finish();
                break;
            case R.id.menuProfile:
                startActivity(new Intent(AppStartActivity.this, DriverProfile.class));
                finish();
                break;
            case R.id.menuLogout:
                SharedPrefManager.getInstance(AppStartActivity.this).logout();
                startActivity(new Intent(AppStartActivity.this, MainActivity.class));
                finish();
                break;
        }
        return true;
    }
}