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

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText driverId, employeeId, firstName, lastName, email, mobileNo, password, confirmPassword;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        driverId=findViewById(R.id.driverId);
        employeeId=findViewById(R.id.employeeId);
        firstName=findViewById(R.id.firstName);
        lastName=findViewById(R.id.lastName);
        email=findViewById(R.id.email);
        mobileNo=findViewById(R.id.mobileNo);
        password=findViewById(R.id.password);
        confirmPassword=findViewById(R.id.confirmPassword);
        register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String txtDriverID=driverId.getText().toString();
                String txtEmployeeID=employeeId.getText().toString();
                String txtFirstName=firstName.getText().toString();
                String txtLastName=lastName.getText().toString();
                String txtEmail=email.getText().toString();
                String txtMobileNo=mobileNo.getText().toString();
                String txtPassword=password.getText().toString();
                String txtConfirmPassword=confirmPassword.getText().toString();

                if(TextUtils.isEmpty(txtDriverID) || TextUtils.isEmpty(txtEmployeeID) ||
                        TextUtils.isEmpty(txtFirstName) || TextUtils.isEmpty(txtLastName) ||
                        TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtMobileNo) ||
                        TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtConfirmPassword)){
                    Toast.makeText(RegisterActivity.this, "All fields are Required", Toast.LENGTH_SHORT).show();
                }else if(!txtPassword.equals(txtConfirmPassword)){
                    Toast.makeText(RegisterActivity.this, "Confirm password fields aren't the same", Toast.LENGTH_SHORT).show();
                }else{
                    registerNewAccount(txtDriverID,txtEmployeeID,txtFirstName,txtLastName,txtEmail,txtMobileNo,txtPassword,txtConfirmPassword);
                }

            }
        });

    }

    private void registerNewAccount(final String driverId, final String employeeId, final String firstName, final String lastName, final String email, final String mobileNo,
                                    final String password, final String confirmPassword){
        final ProgressDialog progressDialog=new ProgressDialog(RegisterActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Registering Account");
        progressDialog.show();
        String uRl="http://192.168.43.107/drivertrack/register.php";
        StringRequest request=new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("error").equals("false")){
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        finish();
                    }else if(jsonObject.getString("error").equals("true")) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                if(response.equals("Successfully Registered")){
//                    progressDialog.dismiss();
//                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
//                    finish();
//                }else{
//                    progressDialog.dismiss();
//                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param= new HashMap<>();
                param.put("driverId", driverId);
                param.put("employeeId", employeeId);
                param.put("firstName", firstName);
                param.put("lastName", lastName);
                param.put("email", email);
                param.put("mobileNo", mobileNo);
                param.put("password", password);
                param.put("confirmPassword", confirmPassword);
                return param;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ));
        MySingleton.getInstance(RegisterActivity.this).addToRequestQueue(request);
    }
}