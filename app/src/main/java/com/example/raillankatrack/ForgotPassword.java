package com.example.raillankatrack;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {
    MaterialEditText email;
    Button resetPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email=findViewById(R.id.fpemail);
        resetPwd=findViewById(R.id.resetReq);

        resetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail=email.getText().toString();
                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(ForgotPassword.this, "Please enter the email.", Toast.LENGTH_SHORT).show();
                }else{
                    resetPassword(textEmail);
                }
            }
        });


    }


    private void resetPassword(final String email){
        final ProgressDialog progressDialog=new ProgressDialog(ForgotPassword.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Requesting...");
        progressDialog.show();
        String uRl=getResources().getString(R.string.URLROOT)+"drivermobiles/driverforgotpassword";
        StringRequest request= new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(!jsonObject.getBoolean("error")){
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPassword.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                        finish();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPassword.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPassword.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param=new HashMap<>();
                param.put("email", email);
                return  param;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(ForgotPassword.this).addToRequestQueue(request);
    }

}