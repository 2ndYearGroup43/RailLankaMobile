package com.example.raillankatrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Current_Assignments extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AssignmentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private final ArrayList<AssignmentItem> currentList = new ArrayList<>();
    private Button refreshBtn;

    // AssignmentItem assignmentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current__assignments);
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        Toolbar toolbar=findViewById(R.id.toolBar);
        refreshBtn =findViewById(R.id.refreshBtn);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Live Assignments");
        toolbar.inflateMenu(R.menu.menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getCurrentAssignment(SharedPrefManager.getInstance(Current_Assignments.this).getDriverId(), new CallBack() {
            @Override
            public void onSuccess(ArrayList<AssignmentItem> assignmentItems, AssignmentItem assignmentItem) {
                //Current_Assignments.this.currentList.add(assignmentItem);
                //Toast.makeText(Current_Assignments.this, assignmentItem.getTrainId(), Toast.LENGTH_SHORT).show();
                createAssignmentList(assignmentItems);
                buildRecyclerView();

            }
            @Override
            public void onFail(String msg) {
                Toast.makeText(Current_Assignments.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentList.clear();
                getCurrentAssignment(SharedPrefManager.getInstance(Current_Assignments.this).getDriverId(), new CallBack() {
                    @Override
                    public void onSuccess(ArrayList<AssignmentItem> assignmentItems, AssignmentItem assignmentItem) {
                        createAssignmentList(assignmentItems); //create the global array list
                        buildRecyclerView(); //build the recycle view from the created array list
                    }
                    @Override
                    public void onFail(String msg) {
                        Toast.makeText(Current_Assignments.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void getCurrentAssignment(final String driverId, final CallBack onCallBack){
       final ProgressDialog progressDialog= new ProgressDialog(Current_Assignments.this);
       progressDialog.setCancelable(false);
       progressDialog.setIndeterminate(false);
       progressDialog.setTitle("Loading");
       progressDialog.show();
       final ArrayList<AssignmentItem> assignmentItemArrayList=new ArrayList<>();

       String url=getResources().getString(R.string.URLROOT)+"DriverMobileAssignments/getCurrentAssignment";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("result")){
                        JSONObject assignmentOb=jsonObject.getJSONObject("assignment");

                        //Toast.makeText(Current_Assignments.this, "aha", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(Current_Assignments.this,  assignmentOb.getString("name"), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(Current_Assignments.this,assignmentOb.getString("trainId"), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(Current_Assignments.this,assignmentOb.getString("date"), Toast.LENGTH_SHORT).show();
                        AssignmentItem assignmentItemk=new AssignmentItem(assignmentOb.getString("trainId"),
                                assignmentOb.getString("name"),
                                assignmentOb.getString("journeyId"),
                                assignmentOb.getString("date"),
                                assignmentOb.getString("src_station_name"),
                                assignmentOb.getString("dest_station_name"));
                        assignmentItemArrayList.add(new AssignmentItem(assignmentOb.getString("trainId"),
                                assignmentOb.getString("name"),
                                assignmentOb.getString("journeyId"),
                                assignmentOb.getString("date"),
                                assignmentOb.getString("src_station_name"),
                                assignmentOb.getString("dest_station_name")));

                        onCallBack.onSuccess(assignmentItemArrayList, assignmentItemk);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    onCallBack.onFail(e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(Current_Assignments.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param= new HashMap<>();
                param.put("driverId", driverId);

                return  param;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(Current_Assignments.this).addToRequestQueue(request);

       

    }

    //callback interface created to return results into the activity scope from the local scope
    public interface CallBack{
        void onSuccess(ArrayList<AssignmentItem> assignmentItems, AssignmentItem assignmentItem); //onsuccess

        void onFail(String msg);
    }

    public void createAssignmentList(ArrayList<AssignmentItem> assignmentItems){
        int size=assignmentItems.size();
        for (int i=0; i<size; i++){
            Current_Assignments.this.currentList.add(assignmentItems.get(i));
        }
    }

    //build reyclerView
    public void buildRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView); //refer the recycle view from the layout
        recyclerView.setHasFixedSize(true);//only if the list wont change size
        layoutManager=new LinearLayoutManager(Current_Assignments.this); //instantiate a layout manager
        adapter=new AssignmentAdapter(currentList); //pass the list into the adapter

        recyclerView.setLayoutManager(layoutManager); //set the layout manager for the recycle view
        recyclerView.setAdapter(adapter); //set adapter to the recycle view
        adapter.setOnItemClickListener(new AssignmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                currentList.get(position);
                Intent intent= new Intent(Current_Assignments.this, LiveLocation.class);
                intent.putExtra("AssignmentItem", currentList.get(position));
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
                startActivity(new Intent(Current_Assignments.this, AppStartActivity.class));
                finish();
                break;
            case R.id.menuProfile:
                startActivity(new Intent(Current_Assignments.this, DriverProfile.class));
                finish();
                break;
            case R.id.menuLogout:
                SharedPrefManager.getInstance(Current_Assignments.this).logout();
                startActivity(new Intent(Current_Assignments.this, MainActivity.class));
                finish();
                break;
        }
        return true;
    }
}