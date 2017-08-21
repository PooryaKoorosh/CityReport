package com.example.koorosh.cityreport;

/**
 * Created by Koorosh on 8/15/2017.
 */

        import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
        import android.util.Log;
        import android.widget.Toast;


public class NetworkStateChangeReceiver extends BroadcastReceiver {

    int TYPE_WIFI = 1;
    int TYPE_MOBILE = 2;
    int TYPE_NOT_CONNECTED = 0;
    Dialog popup;

    @Override
    public void onReceive(Context context, Intent intent) {

        popup = new Dialog(context);
        if(!getConnectivityStatus(context) && !G.isNetworkFailed) {
            Intent i  =new Intent(context,InternetFail.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
if(MyApplication.isActivityVisible()) {
    context.startActivity(i);
}
            Log.e("onReceive", "onReceive: starting fail network activity : " + G.isNetworkFailed);
        }

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

}
