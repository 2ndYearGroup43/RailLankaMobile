package com.example.raillankatrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DriverProfile extends AppCompatActivity {

    TextView textViewDriverId;
    TextView textViewEmpId;
    TextView textViewName;
    TextView textViewEmail;
    TextView textViewMobile;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(DriverProfile.this, MainActivity.class));
            finish();
        }
        Toolbar toolbar=findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Live Assignments");
        toolbar.inflateMenu(R.menu.menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        backBtn=findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DriverProfile.this, AppStartActivity.class));
            }
        });

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        textViewDriverId=(TextView)findViewById(R.id.textViewDriverId);
        textViewEmpId=(TextView)findViewById(R.id.textViewEmployeeId);
        textViewName=(TextView)findViewById(R.id.textViewName);
        textViewEmail=(TextView)findViewById(R.id.textViewEmail);
        textViewMobile=(TextView)findViewById(R.id.textViewMobileNo);

        textViewDriverId.setText(SharedPrefManager.getInstance(this).getDriverId());
        textViewEmpId.setText(SharedPrefManager.getInstance(this).getEmployeeId());
        textViewName.setText(SharedPrefManager.getInstance(this).getName());
        textViewEmail.setText(SharedPrefManager.getInstance(this).getEmail());
        textViewMobile.setText(SharedPrefManager.getInstance(this).getMobileNo());


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
                startActivity(new Intent(DriverProfile.this, AppStartActivity.class));
                finish();
                break;
            case R.id.menuProfile:
                startActivity(new Intent(DriverProfile.this, DriverProfile.class));
                finish();
                break;
            case R.id.menuLogout:
                SharedPrefManager.getInstance(DriverProfile.this).logout();
                startActivity(new Intent(DriverProfile.this, MainActivity.class));
                finish();
                break;
        }
        return true;
    }
}