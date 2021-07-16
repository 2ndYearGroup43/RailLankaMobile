package com.example.raillankatrack;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

public class SharedPrefManager {

    private static SharedPrefManager nInstance;
    private Context nCtx;

    private static final String SHARED_PREF_NAME="driversharedpref";
    private static final String KEY_USERID="userid";
    private static final String KEY_EMPLOYEEID="employeeid";
    private static final String KEY_DRIVERID="driverid";
    private static final String KEY_EMAIL="email";
    private static final String KEY_FNAME="firstname";
    private static final String KEY_LNAME="lastname";
    private static final String KEY_MOBILENO="mobileno";

    public SharedPrefManager(Context nCtx) {
        this.nCtx = nCtx;
    }


    public static synchronized SharedPrefManager getInstance(Context context){ //current instace that the app is on
        if(nInstance==null){
            nInstance=new SharedPrefManager((context));
        }
        return nInstance;
    }

    public boolean userLogin(String userId, String driverId,String employeeId, String firstName,  String lastName, String email, String mobileNo ){
        SharedPreferences sharedPreferences=nCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE); //mode private only can be accessed by calling application
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(KEY_USERID, userId);
        editor.putString(KEY_DRIVERID, driverId);
        editor.putString(KEY_EMPLOYEEID, employeeId);
        editor.putString(KEY_FNAME, firstName);
        editor.putString(KEY_LNAME, lastName);
        editor.putString(KEY_MOBILENO, mobileNo);
        editor.putString(KEY_EMAIL, email);

        editor.apply();
        return true;
    }

    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences=nCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(KEY_USERID, null)!=null){
            return true;
        }
        return false;
    }

    public boolean logout(){
        SharedPreferences sharedPreferences=nCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public String getDriverId(){
        SharedPreferences sharedPreferences=nCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_DRIVERID,null);
    }

    public String getEmployeeId(){
        SharedPreferences sharedPreferences=nCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMPLOYEEID,null);
    }
    public String getName(){
        SharedPreferences sharedPreferences=nCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String name=sharedPreferences.getString(KEY_FNAME,null)+" "+sharedPreferences.getString(KEY_LNAME,null);
        return name;
    }
    public String getEmail(){
        SharedPreferences sharedPreferences=nCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL,null);
    }
    public String getMobileNo(){
        SharedPreferences sharedPreferences=nCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MOBILENO,null);
    }





}
