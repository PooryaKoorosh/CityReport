package com.example.koorosh.cityreport;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Koorosh on 8/11/2017.
 */

public class LoginActivity extends Activity implements View.OnClickListener{

    EditText edUsername,edPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.FinalLoginBtn).setOnClickListener(this);

        edUsername = (EditText)findViewById(R.id.edUsername);
        edPassword = (EditText)findViewById(R.id.edPassword);

        G.setCustomTypeface(edUsername);
        G.setCustomTypeface(edPassword);
        G.setCustomTypeface(findViewById(R.id.FinalLoginBtn));

        G.context=this;
        G.activity=this;


    }

    @Override
    protected void onResume() {
        super.onResume();
        G.context=this;
        G.activity=this;
    }

    @Override
    public void onClick(View view) {
        SharedPrefrencesManager sp = new SharedPrefrencesManager(this);

        if(!CheckEmptyInput())
        {
            Toast.makeText(this, "لطفا تمامی فیلد ها را پر بفرمایید", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(sp.getUsername().equals("Default") || sp.getPassword().equals("Default"))
            {
                LoginUser(edUsername.getText().toString(), edPassword.getText().toString());
            }
            else {
          if(sp.getUsername().equals(edUsername.getText().toString()) && sp.getPassword().equals(edPassword.getText().toString()))
          {
//              Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//              startActivity(intent);
//              finish();
              LoginUser(edUsername.getText().toString(), edPassword.getText().toString());
          }
          else
          {
              G.showAlert("مشخصات وارد شده صحیح نمی باشد",LoginActivity.this);
          }
            }
        }
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
                    }else if(response.equals("invalid")){
                        G.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "اطلاعات وارد شده صحیح نمی باشد", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        G.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, G.ERROR, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, G.ERROR, Toast.LENGTH_SHORT).show();
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

            for (int i = 0 ; i < posts_array.length() ; i++) {
                StructPosts structPosts = new StructPosts();
            structPosts.id = posts_array.getJSONObject(i).getString("id");
            structPosts.type = posts_array.getJSONObject(i).getString("type");
                structPosts.text = posts_array.getJSONObject(i).getString("text");
                structPosts.lat = Double.parseDouble(posts_array.getJSONObject(i).getString("lat"));
                structPosts.lng = Double.parseDouble(posts_array.getJSONObject(i).getString("lng"));
                G.posts.add(structPosts);
            }


            return true;
        } catch ( Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean CheckEmptyInput() {
        if(edUsername.getText().toString().equals("") || edPassword.getText().toString().equals(""))
        {
           return false;
        }
        return  true;
    }

    public StructTypes GetTypesById(int id)
    {
        for(int i=0;i<G.types.size();i++) {
            if (G.types.get(i).id.equals(id+"")){
                Log.e("GetTypesById: ", id+"");
                return G.types.get(i);
            }
        }
        return  G.types.get(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity.this,LauncherActivity.class));
        finish();
    }

}
