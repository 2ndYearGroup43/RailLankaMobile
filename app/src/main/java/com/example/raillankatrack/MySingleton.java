package com.example.raillankatrack;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

public class MySingleton { //creates a request queue for the apps life time recommended by android volley for constant internet usage

    private static MySingleton nInstance; //static instance of singleton pattern for requests
    private RequestQueue nRequestQueue; //volley request queue class
    private Context nCtx; //context of the class
    public MySingleton(Context nCtx) {   //constructor instantiate the context
        this.nCtx = nCtx;
        nRequestQueue=getnRequestQueue();
    }

    public RequestQueue getnRequestQueue(){
        if(nRequestQueue==null){
            Cache cache=new DiskBasedCache(nCtx.getCacheDir(), 1024*1024); //instantiate a disk based cache 1MB
            Network network=new BasicNetwork(new HurlStack()); //setup a network using network clas for http requests
            nRequestQueue=new RequestQueue(cache, network); //instantiate the request queue using created cache and network
            nRequestQueue= Volley.newRequestQueue(nCtx.getApplicationContext());  //we get application context because we want to last it throught the application instance
        }
        return nRequestQueue;
    }

    public static synchronized MySingleton getInstance(Context context){
        if(nInstance==null){
            nInstance=new MySingleton((context));
        }
        return nInstance;
    }

    public <T> void  addToRequestQueue(Request<T> request){
        nRequestQueue.add(request);
    } //adds the request to the request queue



}
