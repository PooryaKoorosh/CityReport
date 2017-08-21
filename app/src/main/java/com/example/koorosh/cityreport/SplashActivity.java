package com.example.koorosh.cityreport;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
        mProgress = findViewById(R.id.splash_screen_progress_bar);

//        new Thread(new Runnable() {
//            public void run() {
//                for (int progress=0; progress<100; progress+=10) {
//                    try {
//                        Thread.sleep(500);
//                        mProgress.setProgress(progress);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();

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
                            }catch (Exception ex){
                                Log.e("run_error: ", ex.getMessage());
                                Intent intent = new Intent(SplashActivity.this,InternetFail.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                }else{
                    G.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("run_error: ", res);
                            Toast.makeText(SplashActivity.this, G.ERROR, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SplashActivity.this,InternetFail.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                }
            }
        });

    }

    private void parseJson(String json) throws JSONException
    {

        JSONArray array=new JSONArray(json);
        StructTypes types;
        for(int i=0;i<array.length();i++){
            types=new StructTypes();
            JSONObject object=array.getJSONObject(i);
            types.id=object.getString("id");
            types.title=object.getString("title");
            G.types.add(types);

        }
        mProgress.setProgress(50);
        GetAllPosts();
    }



    public void GetAllPosts()
    {
        Gonnect.getData(G.server + "posts.php", new Gonnect.ResponseSuccessListener() {
            @Override
            public void responseRecieved(String response) {
                Log.e("responseRecieved: ", response);
                if (ResponseJsonParser2(response)) {
                                Intent intent = new Intent(SplashActivity.this,LauncherActivity.class);
                                startActivity(intent);
                                finish();
                } else {
                    G.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SplashActivity.this, G.ERROR, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SplashActivity.this, G.ERROR, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean ResponseJsonParser2(String response) {
        try {
            JSONArray all_post = new JSONArray(response);

            for (int i = 0 ; i < all_post.length() ; i++) {
                try {
                    StructPosts structPosts = new StructPosts();
                    structPosts.id = all_post.getJSONObject(i).getString("id");
                    structPosts.types = G.GetTypesById(Integer.parseInt(all_post.getJSONObject(i).getString("type")));
                    structPosts.title = all_post.getJSONObject(i).getString("title");
                    structPosts.text = all_post.getJSONObject(i).getString("text");
                    structPosts.lat = Double.parseDouble(all_post.getJSONObject(i).getString("lat"));
                    structPosts.lng = Double.parseDouble(all_post.getJSONObject(i).getString("lng"));
                    G.all_posts.add(structPosts);
                }
                catch (NumberFormatException ex)
                {

                }
            }

            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return  false;
        }
    }


}
