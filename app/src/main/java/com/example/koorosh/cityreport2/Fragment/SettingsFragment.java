package com.example.koorosh.cityreport2.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.koorosh.cityreport2.G;
import com.example.koorosh.cityreport2.LauncherActivity;
import com.example.koorosh.cityreport2.LoginActivity;
import com.example.koorosh.cityreport2.R;
import com.example.koorosh.cityreport2.RegisterActivity;
import com.example.koorosh.cityreport2.SharedPrefrencesManager;


/**
 * Created by Koorosh on 8/10/2017.
 */

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }
    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

mView =  inflater.inflate(R.layout.fragment_settings, container, false);

        mView.findViewById(R.id.LogoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });



        // Inflate the layout for this fragment
        return mView;
    }

    private void Logout() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("لطفا کمی صبر کنید...");
        pd.setCancelable(false);
        pd.show();

        final SharedPrefrencesManager sp = new SharedPrefrencesManager(getContext());
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, G.GetLogoutURl(sp.getToken()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pd.dismiss();
                        if(response.contains("success"))
                        {

                           sp.removeEmail();
                            sp.removeName();
                            sp.removePassword();
                            sp.removeToken();
                            sp.removeUsername();



                            Intent intent = new Intent(getContext(), LauncherActivity.class);
                            startActivity(intent);

                            getActivity().finish();
                        }
                        else
                        {
                            G.showAlert("متاسفانه خروج موفق نبود و یا شما در حال حاضر ورود نکرده اید.",getContext());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                G.showAlert("لطفا از اتصال خود به اینترنت مطمئن شوید و مجددا تلاش کنید",getContext());
            }
        });

        queue.add(stringRequest);
    }
}
