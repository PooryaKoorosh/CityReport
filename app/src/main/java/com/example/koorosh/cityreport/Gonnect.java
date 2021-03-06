package com.example.koorosh.cityreport;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Gonnect {
    private static  OkHttpClient okHttpClient=new OkHttpClient();

    public Gonnect(){

    }

    private  static RequestBody setupRequestBody(ContentValues values){

        RequestBody requestBody=null;
        if (values != null && values.size() > 0) {
            FormBody.Builder formEncoding = new FormBody.Builder();

            Set<String> keySet = values.keySet();
            for (String key : keySet) {
                try {
                    values.getAsString(key);
                    formEncoding.add(key, values.getAsString(key));

                } catch (Exception ex) {

                    Log.d("GonnectLog","Error Happend While Setting Up Request Body : "+ex.getMessage());
                }
            }
            requestBody = formEncoding.build();

        }
        return requestBody;

    }

    private static RequestBody setupMultipartBody(ContentValues values, String uploadKey, String filename, MediaType mediaType, File file){

        MultipartBody.Builder builder=new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        Set<String> keySet = values.keySet();
        for (String key : keySet) {
            try {
                builder.addFormDataPart(key, values.getAsString(key));
                Log.d("key","KEY :"+key+"  VALUE:"+values.getAsString(key));
            } catch (Exception ex) {

                Log.d("GonnectLog","Error Happend While Setting Up Request Body : "+ex.getMessage());
            }
        }

//        builder.addPart(setupRequestBody(values));
               builder.addFormDataPart(uploadKey, filename,
                        RequestBody.create(mediaType, file));
        return builder.build();

    }


    public static void upload(String url, ContentValues values,String tag,String uploadKey,String filename,String mediaType,File file,final ResponseSuccessListener listener,final ResponseFailureListener failureListener){


        RequestBody requestBody=setupMultipartBody(values,uploadKey,filename,MediaType.parse(mediaType),file);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .tag(tag)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                failureListener.responseFailed(e);

            }

            @Override public void onResponse(Call call, Response response) throws IOException {


                listener.responseRecieved(response.body().string());


            }
        });

    }

    //Simple Requests

    public static void sendRequest(String url, ContentValues values, final ResponseListener listener){

        RequestBody requestBody=setupRequestBody(values);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                listener.responseRecieved(false,e.getMessage().toString());

            }

            @Override public void onResponse(Call call, Response response) throws IOException {


                listener.responseRecieved(true,response.body().string());


            }
        });

    }

    public static void sendRequest(String url, ContentValues values, final ResponseSuccessListener listener){

        RequestBody requestBody=setupRequestBody(values);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {


            }

            @Override public void onResponse(Call call, Response response) throws IOException {


                listener.responseRecieved(response.body().string());


            }
        });

    }

    public static void sendRequest(String url, ContentValues values, final ResponseFailureListener failureListener){

        RequestBody requestBody=setupRequestBody(values);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                failureListener.responseFailed(e);

            }

            @Override public void onResponse(Call call, Response response) throws IOException {




            }
        });

    }

    public static void sendRequest(String url, ContentValues values, final ResponseSuccessListener listener,final ResponseFailureListener failureListener){

        RequestBody requestBody=setupRequestBody(values);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                failureListener.responseFailed(e);

            }

            @Override public void onResponse(Call call, Response response) throws IOException {


                listener.responseRecieved(response.body().string());


            }
        });

    }

    public static void sendCancelableRequest(String url, ContentValues values, String tag,final ResponseSuccessListener listener,final ResponseFailureListener failureListener){

        RequestBody requestBody=setupRequestBody(values);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .tag(tag)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                failureListener.responseFailed(e);

            }

            @Override public void onResponse(Call call, Response response) throws IOException {


                listener.responseRecieved(response.body().string());


            }
        });

    }
    //Pro Requests


    //Launch Activity Requests

    public static void sendRequestAndLaunchActivity(String url, ContentValues values, final Context context, final Class activity){

        RequestBody requestBody=setupRequestBody(values);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {


            }

            @Override public void onResponse(Call call, Response response) throws IOException {

                Intent intent=new Intent(context,activity);
                intent.putExtra("response",response.body().string());
                context.startActivity(new Intent(context,activity));

            }
        });

    }

    public static void sendRequestAndLaunchActivity(String url, ContentValues values, final ResponseFailureListener failureListener, final Context context, final Class activity){

        RequestBody requestBody=setupRequestBody(values);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                failureListener.responseFailed(e);

            }

            @Override public void onResponse(Call call, Response response) throws IOException {


                Intent intent=new Intent(context,activity);
                intent.putExtra("response",response.body().string());
                context.startActivity(new Intent(context,activity));
                

            }
        });

    }

    public static void sendCancelableRequestAndLaunchActivity(String url, String tag,ContentValues values, final ResponseFailureListener failureListener, final Context context, final Class activity){

        RequestBody requestBody=setupRequestBody(values);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .tag(tag)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                failureListener.responseFailed(e);

            }

            @Override public void onResponse(Call call, Response response) throws IOException {


                Intent intent=new Intent(context,activity);
                intent.putExtra("response",response.body().string());
                context.startActivity(new Intent(context,activity));


            }
        });

    }

    //Simple Get Data

    public static void getData(String url, final ResponseListener listener){


        Request request=new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                listener.responseRecieved(false,"Cannot Connect");

            }

            @Override public void onResponse(Call call, Response response) throws IOException {


                listener.responseRecieved(true,response.body().string());

            }
        });

    }

    public static void getData(String url, final ResponseSuccessListener listener){


        Request request=new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {


            }

            @Override public void onResponse(Call call, Response response) throws IOException {


                listener.responseRecieved(response.body().string());

            }
        });

    }

    public static void getData(String url,final ResponseFailureListener failureListener){


        Request request=new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                failureListener.responseFailed(e);

            }

            @Override public void onResponse(Call call, Response response) throws IOException {



            }
        });

    }

    public static void getData(String url, final ResponseSuccessListener listener,final ResponseFailureListener failureListener){


        Request request=new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                failureListener.responseFailed(e);

            }

            @Override public void onResponse(Call call, Response response) throws IOException {


                listener.responseRecieved(response.body().string());

            }
        });

    }

    public static void getCancelableData(String url,String tag, final ResponseSuccessListener listener,final ResponseFailureListener failureListener){


        Request request=new Request.Builder()
                .url(url)
                .tag(tag)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                failureListener.responseFailed(e);

            }

            @Override public void onResponse(Call call, Response response) throws IOException {


                listener.responseRecieved(response.body().string());

            }
        });

    }

    //Launch Activity Get Data(s)

    public static void getDataAndLaunchActivity(String url,final Class activity, final Context context){


        Request request=new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {



            }

            @Override public void onResponse(Call call, Response response) throws IOException {

                Intent intent=new Intent(context,activity);
                intent.putExtra("response",response.body().string());
                context.startActivity(new Intent(context,activity));
            }
        });

    }

    public static void getDataAndLaunchActivity(String url, final ResponseFailureListener failureListener, final Class activity, final Context context){


        Request request=new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                failureListener.responseFailed(e);

            }

            @Override public void onResponse(Call call, Response response) throws IOException {

                Intent intent=new Intent(context,activity);
                intent.putExtra("response",response.body().string());
                context.startActivity(new Intent(context,activity));
            }
        });

    }

    public static void getCancelableDataAndLaunchActivity(String url,String tag, final ResponseFailureListener failureListener, final Class activity, final Context context){


        Request request=new Request.Builder()
                .url(url)
                .tag(tag)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {

                failureListener.responseFailed(e);

            }

            @Override public void onResponse(Call call, Response response) throws IOException {

                Intent intent=new Intent(context,activity);
                intent.putExtra("response",response.body().string());
                context.startActivity(new Intent(context,activity));
            }
        });

    }

    //Pro Get Data(s)

    //Interfaces

    public interface ResponseSuccessListener{

        public void responseRecieved(String response);

    }

    public interface ResponseFailureListener{

        public void responseFailed(IOException exception);

    }

    public interface  ResponseListener{

        public void responseRecieved(boolean isSuccess, String errorOrResponse);

    }



    //ResponseController

    public static void responseController(String target,String response){





    }

    public static void cancelRequest(String tag){

        for(Call call : okHttpClient.dispatcher().queuedCalls()) {
            if(call.request().tag().equals(tag))
                call.cancel();
        }
        for(Call call : okHttpClient.dispatcher().runningCalls()) {
            if(call.request().tag().equals(tag))
                call.cancel();
        }

    }


}
