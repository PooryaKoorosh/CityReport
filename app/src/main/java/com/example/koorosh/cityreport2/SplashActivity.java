package com.example.koorosh.cityreport2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Exchanger;

/**
 * Created by Koorosh on 8/17/2017.
 */

public class SplashActivity extends Activity {

    private ProgressBar mProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        G.context=this;
        G.activity=this;
        mProgress = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);

        // Start lengthy operation in a background thread
//        new Thread(new Runnable() {
//            public void run() {
//                /*doWork();*/
//            }
//        }).start();

        //Dadash vase type nabayd string bedi bayad id e sho bedi pas tuye arraye string behtare zakhire nkoni

        getTypes();

    }

    private void getTypes(){

        Gonnect.getData(G.GetTypesUrl(), new Gonnect.ResponseListener() {
            @Override
            public void responseRecieved(boolean isSuccess, String errorOrResponse) {
                final String res=errorOrResponse.trim();
                if(isSuccess){
                    G.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(SplashActivity.this, G.SUCCESS, Toast.LENGTH_SHORT).show();
                            try {
                                parseJson(res);
                                Intent intent = new Intent(SplashActivity.this,LauncherActivity.class);
                                startActivity(intent);
                            }catch (Exception ex){
                                Intent intent = new Intent(SplashActivity.this,InternetFail.class);
                                startActivity(intent);
                            }
                        }
                    });

                }else{
                    G.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SplashActivity.this, G.ERROR, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SplashActivity.this,InternetFail.class);
                            startActivity(intent);
                        }
                    });

                }
            }
        });

    }

    private void parseJson(String json) throws JSONException{

        JSONArray array=new JSONArray(json);
        StructTypes types;
        for(int i=0;i<array.length();i++){
            types=new StructTypes();
            JSONObject object=array.getJSONObject(i);
            types.id=object.getString("id");
            types.title=object.getString("title");
            G.types.add(types);

        }


    }

    private void doWork() {
        Volley volley=new Volley();
        RequestQueue queue = volley.newRequestQueue(getBaseContext());

        for (int progress=0; progress<100; progress+=10) {
            try {
                Thread.sleep(600);
                mProgress.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, G.GetTypesUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {

                            ArrayList<String> Types = new ArrayList<String>();
                            JSONArray array = new JSONArray(response);
                            for (int i = 0 ; i < array.length() ; i++)
                            {
                                String str = array.getJSONObject(i).getString("title");
                                Log.e("onResponse: ", str);
                                Types.add(str);
                            }
                            SharedPrefrencesManager sp = new SharedPrefrencesManager(getBaseContext());
                            sp.saveArray(Types);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Intent intent = new Intent(SplashActivity.this,LauncherActivity.class);
                        startActivity(intent);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Intent intent = new Intent(SplashActivity.this,InternetFail.class);
                startActivity(intent);
            }
        });

        queue.add(stringRequest);
    }

}
