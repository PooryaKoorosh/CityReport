package com.example.koorosh.cityreport;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Koorosh on 8/10/2017.
 */

public class LauncherActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        G.context=this;
        G.activity=this;

String message = getIntent().getStringExtra("message");

        if(checkPermission()){
            if(!isLocationEnabled(getBaseContext())) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
            SharedPrefrencesManager sp = new SharedPrefrencesManager(this);
            if(message != null)
            {
                ChooseAction();
                message = null;
            }
            else if(sp.getUsername().equals("Default") && sp.getPassword().equals("Default"))
            {
                ChooseAction();
            }
            else
            {
                LoginUser(sp.getUsername(),sp.getPassword());
            }

        }
        else
        {
            askPermission();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        G.HideLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();
        G.context=this;
        G.activity=this;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    private void askPermission() {
        Log.d("", "askPermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                5001
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("", "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 5001: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    if (checkPermission())
                    {
                        if(!isLocationEnabled(getBaseContext())) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            finish();
                        }

                        SharedPrefrencesManager sp = new SharedPrefrencesManager(this);
                        if(sp.getUsername().equals("Default") && sp.getPassword().equals("Default"))
                        {
                            ChooseAction();
                        }
                        else
                        {
                            LoginUser(sp.getUsername(),sp.getPassword());
                        }
                    }

                } else {
                    // Permission denied
                    finish();
                    System.exit(0);
                }
                break;
            }
        }
    }

    private boolean checkPermission() {
        Log.d("", "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    private void ChooseAction() {
        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void LoginUser(String Username, String Password) {

    G.ShowLoading(new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            Gonnect.cancelRequest("login");
        }
    });
    ContentValues cv = new ContentValues();
    cv.put("user", Username);
    cv.put("pass", Password);
    Gonnect.sendCancelableRequest(G.server + "login.php", cv, "login", new Gonnect.ResponseSuccessListener() {
        @Override
        public void responseRecieved(String response) {
            G.HideLoading();
            if (ResponseJsonParser(response)) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                G.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LauncherActivity.this, G.ERROR, Toast.LENGTH_SHORT).show();
                    }
                    });
            }
        }
    }, new Gonnect.ResponseFailureListener() {
        @Override
        public void responseFailed(IOException exception) {
            G.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    G.HideLoading();
                    Toast.makeText(LauncherActivity.this, G.ERROR, Toast.LENGTH_SHORT).show();
                }
            });
        }
    });
    }

    private boolean ResponseJsonParser(String response) {
        try {
            JSONObject info = new JSONObject(response);
            JSONArray profile_array = info.getJSONArray("profile");
            JSONArray posts_array = info.getJSONArray("posts");
            String token = info.getString("token");


            SharedPrefrencesManager sp = new SharedPrefrencesManager(getBaseContext());
            sp.setEmail(profile_array.getJSONObject(0).getString("email").toString());
            sp.setName(profile_array.getJSONObject(0).getString("name").toString());
            sp.setPassword(profile_array.getJSONObject(0).getString("password").toString());
            sp.setUsername(profile_array.getJSONObject(0).getString("username").toString());
            sp.setToken(token.toString());



            Log.e("ResponseJsonParser: ", posts_array.length()+"");
            for (int i = 0 ; i < posts_array.length() ; i++) {
                StructPosts structPosts = new StructPosts();
                structPosts.id = posts_array.getJSONObject(i).getString("id");
                structPosts.types = G.GetTypesById(Integer.parseInt(posts_array.getJSONObject(i).getString("type")));
                structPosts.title = posts_array.getJSONObject(i).getString("title");
                structPosts.text = posts_array.getJSONObject(i).getString("text");
                structPosts.lat = Double.parseDouble(posts_array.getJSONObject(i).getString("lat"));
                structPosts.lng = Double.parseDouble(posts_array.getJSONObject(i).getString("lng"));
                G.posts.add(structPosts);
            }


            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }




}
