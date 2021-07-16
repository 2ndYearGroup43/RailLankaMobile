package com.example.raillankatrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {

    MaterialEditText email, password;
    Button login;
    SharedPreferences sharedPreferences;
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, AppStartActivity.class));
            return;
        }
        sharedPreferences=getSharedPreferences("UserInfo", MODE_PRIVATE);

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        forgotPassword=findViewById(R.id.forgotPassword);

        String forgotLink = "Forgot Password? Click Here.";
        //clickable link creation
        SpannableString s = new SpannableString(forgotLink);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) { //on click event
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
            }
        };
        s.setSpan(clickableSpan, 17, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgotPassword.setText(s); //spannable String object set as text
        forgotPassword.setMovementMethod(LinkMovementMethod.getInstance());


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail=email.getText().toString();
                String txtPassword=password.getText().toString();
                if (TextUtils.isEmpty(txtEmail)||TextUtils.isEmpty(txtPassword)){
                    Toast.makeText(MainActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else{
                    login(txtEmail, txtPassword);
                }
            }
        });

    }

    private void login(final String email, final String password){
        final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false); //not until the task completes
        progressDialog.setTitle("Logging In");
        progressDialog.show();
        String uRl=getResources().getString(R.string.URLROOT)+"drivermobiles/login";
        StringRequest request=new StringRequest(Request.Method.POST, uRl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject=new JSONObject(response);//create a json object of the response
                    if (!jsonObject.getBoolean("error")){
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin( //include details inside sharedprefmanager
                                jsonObject.getString("userId"),
                                jsonObject.getString("driverId"),
                                jsonObject.getString("employeeId"),
                                jsonObject.getString("firstName"),
                                jsonObject.getString("lastName"),
                                jsonObject.getString("email"),
                                jsonObject.getString("mobileNo")
                        );
                        Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,AppStartActivity.class));
                        finish();

                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param= new HashMap<>();
                param.put("email", email);
                param.put("password", password);

                return param;

            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(request);
    }
}