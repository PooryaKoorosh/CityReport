package com.example.koorosh.cityreport;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by Koorosh on 8/15/2017.
 */

public class InternetFail extends Activity {

    int TYPE_WIFI = 1;
    int TYPE_MOBILE = 2;
    int TYPE_NOT_CONNECTED = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networkfail);

        findViewById(R.id.btnWifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });

        findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getConnectivityStatus(getBaseContext()))
                {
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }


    private boolean getConnectivityStatus(Context context) {
        int conn = getConnectivity(context);

        if (conn == TYPE_WIFI) {
            return true;
        } else if (conn == TYPE_MOBILE) {
            return true;
        } else if (conn == TYPE_NOT_CONNECTED) {
            return false;
        }
        return false;
    }

    private int getConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    @Override
    protected void onStart() {
        super.onStart();
        G.isNetworkFailed = true;
        Log.e("onStart", "onStart: " + G.isNetworkFailed );
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        G.isNetworkFailed = false;
        Log.e("onStop", "onStop: " + G.isNetworkFailed );
        super.onDestroy();
    }
}
