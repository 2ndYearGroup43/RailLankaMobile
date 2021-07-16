package com.example.raillankatrack;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Past_Assignments extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PastAssignmentAdapter adapter;
    private  RecyclerView.LayoutManager layoutManager;
    private final ArrayList<PastAssignmentItem> pastList=new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past__assignments);
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(Past_Assignments.this, MainActivity.class));
            finish();
        }
        Toolbar toolbar=findViewById(R.id.toolBar);
        
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Past Assignments");
        toolbar.inflateMenu(R.menu.menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(this, SharedPrefManager.getInstance(Past_Assignments.this).getDriverId(), Toast.LENGTH_SHORT).show();

        getPastAssignments(SharedPrefManager.getInstance(Past_Assignments.this).getDriverId(), new CallBack() {
            @Override
            public void onSuccess(ArrayList<PastAssignmentItem> pastAssignmentItems) {
                buildPastList(pastAssignmentItems);
                buildRecycleView();

            }

            @Override
            public void onFail(String msg) {
                Toast.makeText(Past_Assignments.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
//        pastList.add(new PastAssignmentItem("10", "2020-10-10", "Colombo-Fort", "Badulla",
//                "0808", "2020-10-10", "10:10:26"  ));
//        pastList.add(new PastAssignmentItem("10", "2020-10-24", "Badulla", "Kandy",
//                "0101", "2020-10-24", "09:20:26"  ));
//        pastList.add(new PastAssignmentItem("10", "2020-11-10", "Colombo-Fort", "Galle",
//                "0404", "2020-11-10", "17:18:26"  ));
//
//        buildRecycleView();




    }

    public void getPastAssignments(final String driverId, final CallBack onCallBack){
        final ProgressDialog progressDialog = new ProgressDialog(Past_Assignments.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        final ArrayList<PastAssignmentItem> pastAssignmentItems=new ArrayList<>();
        SharedPreferences sharedPreferences;
        String url=getResources().getString(R.string.URLROOT)+"DriverMobileAssignments/getPastAssignments";
        //Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObjectRespnse=new JSONObject(response);
                    JSONArray jsonArray;
                    jsonArray=jsonObjectRespnse.getJSONArray("assignments");
//                    jsonArray = js.getJSONArray("assignments");
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject assignment;
                        assignment=jsonArray.getJSONObject(i);
                        PastAssignmentItem pastAssignment=new PastAssignmentItem(assignment.getString("trainId"),
                                assignment.getString("date"),
                                assignment.getString("src_name"),
                                assignment.getString("dest_name"),
                                assignment.getString("moderatorId"),
                                assignment.getString("ended_date"),
                                assignment.getString("ended_time"));
                        pastAssignmentItems.add(pastAssignment);
                    }

                    onCallBack.onSuccess(pastAssignmentItems);
                } catch (JSONException e) {
                    e.printStackTrace();
                    onCallBack.onFail(e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(Past_Assignments.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param= new HashMap<>();
                param.put("driverId", driverId);
                return param;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(Past_Assignments.this).addToRequestQueue(request);

    }


    public interface CallBack{
        void onSuccess(ArrayList<PastAssignmentItem> pastAssignmentItems);

        void onFail(String msg);
    }




    public void buildPastList(ArrayList<PastAssignmentItem> pastAssignmentItems){
        int size=pastAssignmentItems.size();
        for(int i=0; i<size; i++){
            Past_Assignments.this.pastList.add(pastAssignmentItems.get(i));
        }
    }

    public void buildRecycleView(){
        recyclerView=findViewById(R.id.pastRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        adapter=new PastAssignmentAdapter(pastList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new PastAssignmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                pastList.get(position);
                startActivity(new Intent(Past_Assignments.this, AppStartActivity.class));
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
                startActivity(new Intent(Past_Assignments.this, AppStartActivity.class));
                finish();
                break;
            case R.id.menuProfile:
                startActivity(new Intent(Past_Assignments.this, DriverProfile.class));
                finish();
                break;
            case R.id.menuLogout:
                SharedPrefManager.getInstance(Past_Assignments.this).logout();
                startActivity(new Intent(Past_Assignments.this, MainActivity.class));
                finish();
                break;
        }
        return true;
    }
}