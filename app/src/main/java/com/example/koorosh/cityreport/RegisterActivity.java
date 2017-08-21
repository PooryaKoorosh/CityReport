package com.example.koorosh.cityreport;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Koorosh on 8/11/2017.
 */

public class RegisterActivity extends Activity implements View.OnClickListener {

    RelativeLayout relativeLayout;
    EditText edEmail , edUsername,edPass,edPassRepeat,edName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        G.context=this;
        G.activity=this;
        relativeLayout = (RelativeLayout)findViewById(R.id.register_relative_layout);
        edEmail = (EditText)findViewById(R.id.edEmail);
        edUsername = (EditText)findViewById(R.id.edUsername);
        edPass = (EditText)findViewById(R.id.edPassword);
        edPassRepeat = (EditText)findViewById(R.id.edPasswordRepeat);
        edName = (EditText)findViewById(R.id.edName);

        G.setCustomTypeface(edEmail);
        G.setCustomTypeface(edUsername);
        G.setCustomTypeface(edPass);
        G.setCustomTypeface(edPassRepeat);
        G.setCustomTypeface(edName);
        G.setCustomTypeface(findViewById(R.id.FinalRegisterBtn));

        findViewById(R.id.FinalRegisterBtn).setOnClickListener(this);
    }

    public boolean CheckForEmptyEditText() {
        ArrayList<EditText> myEditTextList = new ArrayList<EditText>();

        for( int i = 0; i < relativeLayout.getChildCount(); i++ )
            if( relativeLayout.getChildAt( i ) instanceof EditText )
                myEditTextList.add( (EditText) relativeLayout.getChildAt( i ) );

        for (EditText ed:myEditTextList) {

            if(ed.getText().toString().equals(""))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {

        if(!CheckForEmptyEditText()) {
            Toast.makeText(this, "لطفا تمامی فیلد ها را پر بفرمایید", Toast.LENGTH_SHORT).show();
        }else {

        Register(edEmail.getText().toString(),edUsername.getText().toString(),edPass.getText().toString(),edPassRepeat.getText().toString(),edName.getText().toString());

        }
    }

    private void Register(String Email, String Username, String Pass,String rePass,String Name) {

        if(!rePass.equals(Pass)){
            Toast.makeText(this, "پسورد ها مغایرت دارند", Toast.LENGTH_SHORT).show();
        }else{

            String url=G.server+"register.php";
            ContentValues cv=new ContentValues();
            cv.put("email",Email);cv.put("user",Username);cv.put("pass",Pass);cv.put("name",Name);
            G.ShowLoading(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Gonnect.cancelRequest("register");
                }
            });

            Gonnect.sendCancelableRequest(url, cv, "register", new Gonnect.ResponseSuccessListener() {
                @Override
                public void responseRecieved(String response) {
                    G.HideLoading();
                String res=response.trim();
                    if(res.equals("success")){
                        G.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, G.SUCCESS, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LauncherActivity.class));
                                finish();
                            }
                        });
                    }else if(res.equals("already-exists")){
                        G.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "نام کاربری مورد نظر قابل انخاب نمی باشد", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        G.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, G.ERROR, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterActivity.this, G.ERROR, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        G.context=this;
        G.activity=this;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this,LauncherActivity.class));
        finish();
    }
}
