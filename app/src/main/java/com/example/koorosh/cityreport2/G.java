package com.example.koorosh.cityreport2;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Koorosh on 8/11/2017.
 */

public class G extends Application{

    public static String server="http://pk.pabbasi.ir/";
    private static String RegisterUrl = "http://pk.pabbasi.ir/register.php?user={0}&email={1}&pass={2}&name={3}";
    private static String LoginUrl = "http://pk.pabbasi.ir/login.php?user={0}&pass={1}";
    private static String LogoutUrl = "http://pk.pabbasi.ir/logout.php?token={0}";
    private static String VerityUserUrl = "http://pk.pabbasi.ir/verify-username.php?user={0}";
    private static String ReportPointSimpleUrl = "http://pk.pabbasi.ir/add-post.php";
    private static String ReportPointWithImageUrl = "http://pk.pabbasi.ir/image-post.php";
    private static String ReportPointWithVideoUrl = "http://pk.pabbasi.ir/video-post.php";
    private static String TypesUrl = "http://pk.pabbasi.ir/types.php";
    public static SweetAlertDialog alertDialog;

    public static boolean isCamera=false;

    public static String SUCCESS="عملیات مورد نظر با موفقیت انجام شد";
    public static String ERROR="خطایی در اتصال به اینترنت رخ داد";
    public static String LOADING="در حال بارگذاری ...";

    public static ArrayList<StructTypes> types=new ArrayList<>();
    public static boolean isNetworkFailed = false;
    public static int PointPostMode = 0;//0=>simple post    1=>image post   2=>video post
    public static boolean ChoosedDocument = false;

    public static Context context;
    public static Activity activity;

    public static String GetRegisterURl(String User,String Email,String Pass,String Name) {
        String temp = RegisterUrl.replace("{0}",User);
         temp = temp.replace("{1}",Email);
        temp = temp.replace("{2}",Pass);
        temp = temp.replace("{3}",Name);

        return temp;
    }

    public static String GetLoginURl(String User,String Pass) {
        String temp = LoginUrl.replace("{0}",User);
        temp = temp.replace("{1}",Pass);

        return temp;
    }

    public static String GetLogoutURl(String Token) {
        String temp = LogoutUrl.replace("{0}",Token);

        return temp;
    }

    public static String GetVerifyUserURl(String User) {
        String temp = VerityUserUrl.replace("{0}",User);

        return temp;
    }

    public static String GetTypesUrl()
    {
        return TypesUrl;
    }

    public static String GetReportPointSimpleUrl()
    {
        return ReportPointSimpleUrl;
    }

    public static String GetReportPointWithImageUrl()
    {
        return ReportPointWithImageUrl;
    }

    public static String GetReportPointWithVideoUrl()
    {
        return ReportPointWithVideoUrl;
    }

    public static void showAlert(String Message , Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("هشدار");
        alertDialog.setMessage(Message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"تایید",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void ShowLoading(DialogInterface.OnCancelListener ocl){
        alertDialog = new SweetAlertDialog(G.context, SweetAlertDialog.PROGRESS_TYPE);
        alertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        alertDialog.setTitleText(LOADING);
        //alertDialog.setCancelable(false);
        alertDialog.setOnCancelListener(ocl);
        G.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();

            }
        });
    }

    public static void HideLoading(){
        G.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismissWithAnimation();
            }
        });

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
