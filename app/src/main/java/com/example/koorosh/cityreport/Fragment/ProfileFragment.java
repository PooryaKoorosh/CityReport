package com.example.koorosh.cityreport.Fragment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.koorosh.cityreport.G;
import com.example.koorosh.cityreport.Gonnect;
import com.example.koorosh.cityreport.LauncherActivity;
import com.example.koorosh.cityreport.R;
import com.example.koorosh.cityreport.SharedPrefrencesManager;
import com.example.koorosh.cityreport.StructPosts;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Koorosh on 8/10/2017.
 */

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }
    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

mView =  inflater.inflate(R.layout.fragment_profile, container, false);

        mView.findViewById(R.id.LogoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });

        TextView tv1 = mView.findViewById(R.id.tvNumber1);
        final TextView tv2 = mView.findViewById(R.id.tvNumber2);
        TextView tv3 = mView.findViewById(R.id.tvNumber3);
        TextView tv4 = mView.findViewById(R.id.tvNumber4);
        final ToggleButton toggleButton = mView.findViewById(R.id.showPasswordToggle);
        final SharedPrefrencesManager sp = new SharedPrefrencesManager(getContext());


        G.setCustomTypeface(tv1);
        G.setCustomTypeface(tv2);
        G.setCustomTypeface(tv3);
        G.setCustomTypeface(tv4);
        G.setCustomTypeface(mView.findViewById(R.id.LogoutBtn));
        G.setCustomTypeface(toggleButton);

        tv1.setText(sp.getUsername());
        tv2.setText("*****");
        tv3.setText(sp.getEmail());
        tv4.setText(sp.getName());


        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggleButton.isChecked())
                {
                    tv2.setText(sp.getPassword());
                }
                else {
                    tv2.setText("*****");
                }
            }
        });

        G.context=getContext();
        G.activity=getActivity();

        // Inflate the layout for this fragment
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        G.context=getContext();
        G.activity=getActivity();
    }

    private void Logout() {

        final SharedPrefrencesManager sp = new SharedPrefrencesManager(getContext());
        G.ShowLoading(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Gonnect.cancelRequest("login");
            }
        });
        ContentValues cv=new ContentValues();
        cv.put("token",sp.getToken());
        Gonnect.sendCancelableRequest(G.server + "logout.php", cv, "logout", new Gonnect.ResponseSuccessListener() {
            @Override
            public void responseRecieved(String response) {
                G.HideLoading();
                if(response.contains("success")){
                            sp.removeEmail();
                            sp.removeName();
                            sp.removePassword();
                            sp.removeToken();
                            sp.removeUsername();
                            G.posts = new ArrayList<StructPosts>();

                            startActivity(new Intent(getContext(), LauncherActivity.class));

                            getActivity().finish();
                }else{
                    G.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), G.ERROR, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }, new Gonnect.ResponseFailureListener() {
            @Override
            public void responseFailed(IOException exception) {G.HideLoading();
                G.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), G.ERROR, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
