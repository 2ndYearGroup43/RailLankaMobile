package com.example.raillankatrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CallStationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CallStationAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    final ArrayList<CallStationItem> stationList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_station);
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class));
            finish();   
        }
        Toolbar toolbar=findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Call Stations");
        toolbar.inflateMenu(R.menu.menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        stationList.add(new CallStationItem("Maradana Control Room", "0118549631"));
//        stationList.add(new CallStationItem("Colombo-Fort Control Room", "0112589631"));
//        stationList.add(new CallStationItem("Kandy Control Room", "0113553831"));
//        stationList.add(new CallStationItem("Galle Control Room", "0115862431"));
//        stationList.add(new CallStationItem("Ragama Control Room", "0115862431"));
//        stationList.add(new CallStationItem("Jaffna Control Room", "0115862431"));
        getStationList(SharedPrefManager.getInstance(CallStationActivity.this).getDriverId(), new CallBack() {
            @Override
            public void onSuccess(ArrayList<CallStationItem> stationItemArrayList) {
                int size=stationItemArrayList.size();
                for(int i=0; i<size; i++){
                    CallStationActivity.this.stationList.add(stationItemArrayList.get(i));
                    buildRecycleView();
                }

            }

            @Override
            public void onFail(String msg) {
                Toast.makeText(CallStationActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });





    }

    public void getStationList(final String driverId, final CallBack onCallback){
        final ProgressDialog progressDialog=new ProgressDialog(CallStationActivity.this);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading..");
        progressDialog.show();

        final ArrayList<CallStationItem> stationItems=new ArrayList<>();

        String url=getResources().getString(R.string.URLROOT)+"driverMobiles/getStations";

        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("stations");
                    int size=jsonArray.length();
                    for(int i=0; i<size; i++){
                        JSONObject station=jsonArray.getJSONObject(i);
                        CallStationItem callStationItem=new CallStationItem(station.getString("stationID"),
                                station.getString("name"),
                                station.getString("telephoneNo"));
                        stationItems.add(callStationItem);
                    }

                    onCallback.onSuccess(stationItems);
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                    onCallback.onFail(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(CallStationActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params=new HashMap<>();
                params.put("driverId", driverId);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(CallStationActivity.this).addToRequestQueue(request);
    }

    public interface CallBack{
        public void onSuccess(ArrayList<CallStationItem> stationItemArrayList);

        public void onFail(String msg);
    }

    public void buildStationList(ArrayList<CallStationItem> stationItems){
        int size=stationItems.size();
        for(int i=0;i<size;i++){
            CallStationActivity.this.stationList.add(stationItems.get(i));
        }
    }

    public  void buildRecycleView(){
        recyclerView=findViewById(R.id.contactRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        adapter= new CallStationAdapter(stationList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CallStationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                stationList.get(position);
                Intent intent=new Intent(Intent.ACTION_DIAL);
                Toast.makeText(CallStationActivity.this,"tel:"+stationList.get(position).getPhone(), Toast.LENGTH_SHORT ).show();
                intent.setData(Uri.parse("tel:"+stationList.get(position).getPhone()));
                startActivity(intent);
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
                startActivity(new Intent(CallStationActivity.this, AppStartActivity.class));
                finish();
                break;
            case R.id.menuProfile:
                startActivity(new Intent(CallStationActivity.this, DriverProfile.class));
                finish();
                break;
            case R.id.menuLogout:
                SharedPrefManager.getInstance(CallStationActivity.this).logout();
                startActivity(new Intent(CallStationActivity.this, MainActivity.class));
                finish();
                break;
        }
        return true;
    }
}