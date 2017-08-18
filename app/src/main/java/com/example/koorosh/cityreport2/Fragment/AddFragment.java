package com.example.koorosh.cityreport2.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.koorosh.cityreport2.G;
import com.example.koorosh.cityreport2.GetImage;
import com.example.koorosh.cityreport2.GetVideo;
import com.example.koorosh.cityreport2.Gonnect;
import com.example.koorosh.cityreport2.LauncherActivity;
import com.example.koorosh.cityreport2.R;
import com.example.koorosh.cityreport2.SharedPrefrencesManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kbeanie.multipicker.api.CameraVideoPicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.VideoPicker;
import com.kbeanie.multipicker.api.callbacks.VideoPickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenVideo;
import com.kbeanie.multipicker.api.exceptions.PickerException;
import com.kbeanie.multipicker.core.PickerManager;
import com.kbeanie.multipicker.core.VideoPickerImpl;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;

import static android.content.Context.LOCATION_SERVICE;

public class AddFragment extends Fragment implements View.OnClickListener {

    String path;Uri uri;
    private static final String TAG = "HomeFragment";
    ArrayList<String> items=new ArrayList<>();
    public AddFragment() {
        // Required empty public constructor
    }

    MapView mMapView;
    private GoogleMap googleMap;
    double latitude = 0;
    double longitude = 0;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2,fab3;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    Dialog popup = null;
    Dialog camordevice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add, container, false);
        if(!isLocationEnabled(getContext())) {
            Toast.makeText(getContext(), "here2", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            System.exit(0);
        }

        fab = (FloatingActionButton)rootView.findViewById(R.id.myFAB);
        fab1 = (FloatingActionButton)rootView.findViewById(R.id.SimpleFab);
        fab2 = (FloatingActionButton)rootView.findViewById(R.id.ImageFab);
        fab3 = (FloatingActionButton)rootView.findViewById(R.id.VideoFab);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("لطفا کمی صبر کنید ...");
        pd.setCancelable(false);
        pd.show();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (checkPermission()) {
                    googleMap.setMyLocationEnabled(true);

                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                    // fetch last location if any from provider - GPS.
                    final LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                    final Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                    pd.dismiss();
                    LatLng here = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions().position(here).title("Marker Title").snippet("Marker Description"));

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(here).zoom(16).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        final LocationListener locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(final Location location) {

                                // getting location of user
                                 latitude = location.getLatitude();
                                 longitude = location.getLongitude();

                                pd.dismiss();
                                //Toast.makeText(getContext(), latitude+" : " + longitude, Toast.LENGTH_SHORT).show();

                                LatLng here = new LatLng(latitude, longitude);
                                googleMap.addMarker(new MarkerOptions().position(here).title("Marker Title").snippet("Marker Description"));

                                // For zooming automatically to the location of the marker
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(here).zoom(16).build();
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                // do something with Latlng
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {
                            }

                            @Override
                            public void onProviderEnabled(String provider) {
                                // do something
                            }

                            @Override
                            public void onProviderDisabled(String provider) {
                                // notify user "GPS or Network provider" not available
                                Intent intent = new Intent(getContext(), LauncherActivity.class);
                                startActivity(intent);
                            }
                        };

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 500, locationListener);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 500, locationListener);

                } else askPermission();

            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupCamorDevice();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private boolean checkPermission() {
        Log.d("", "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    private void askPermission() {
        Log.d("", "askPermission()");
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                5001
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("", "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(getActivity(), requestCode, Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case 5001: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    if (checkPermission())
                        googleMap.setMyLocationEnabled(true);


                } else {
                    // Permission denied
                    getActivity().finish();
                    System.exit(0);
                }
                break;
            }
        }

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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.myFAB:
                animateFAB();
                break;

            case R.id.SimpleFab:
                animateFAB();
                G.PointPostMode = 0;
                ShowAddPointPopup();

                break;

            case R.id.ImageFab:
                animateFAB();
                G.PointPostMode = 1;
                ShowAddPointPopup();

                break;

            case R.id.VideoFab:
                animateFAB();
                G.PointPostMode = 2;
                ShowAddPointPopup();

                break;
        }
    }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;

        }
    }

    public void ShowAddPointPopup() {
        popup = new Dialog(getActivity());
        popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup.setContentView(R.layout.popup_add_point);
        popup.setCancelable(true);
        popup.getWindow().setGravity(Gravity.CENTER);
        popup.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final Spinner spinner = (Spinner)popup.findViewById(R.id.spProblems);
        SharedPrefrencesManager sp = new SharedPrefrencesManager(getContext());

        for(int i=0;i<G.types.size();i++){
            items.add(G.types.get(i).title);
        }

        final String Token = sp.getToken();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        final EditText edTitle = (EditText)popup.findViewById(R.id.edTitle);
        final EditText edDescription = (EditText)popup.findViewById(R.id.edDescription);


        switch (G.PointPostMode)
        {
            case 0:
                (popup.findViewById(R.id.btnDocumentChoose)).setVisibility(View.GONE);
                break;

            case 1:
                (popup.findViewById(R.id.btnDocumentChoose)).setVisibility(View.VISIBLE);
                ((Button)popup.findViewById(R.id.btnDocumentChoose)).setText("ارسال عکس");
                break;

            case 2:
                (popup.findViewById(R.id.btnDocumentChoose)).setVisibility(View.VISIBLE);
                ((Button)popup.findViewById(R.id.btnDocumentChoose)).setText("ارسال ویدیو");
                break;
        }

        popup.findViewById(R.id.btnDocumentChoose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(G.PointPostMode == 1) {
//                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                        photoPickerIntent.setType("image/*");
//                        startActivityForResult(photoPickerIntent, 5000);
                        Intent chooseImageIntent = GetImage.getPickImageIntent(G.context);
                        startActivityForResult(chooseImageIntent, 1);

                    }else if(G.PointPostMode == 2) {

                       // camordevice.show();
                        Intent chooseVideoIntent = GetVideo.getPickVideoIntent(G.context);
                        startActivityForResult(chooseVideoIntent, 2);

                    }
            }
        });

        popup.findViewById(R.id.ReportBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //token={0}&title={1}&text={2}&type={3}&lat={4}&lng={5}&image={6{&video={7}

                String  type="0";
                String typeString=items.get((int)spinner.getSelectedItemId());
                for(int i=0;i<G.types.size();i++) {
                    if (G.types.get(i).title.equals(typeString.trim())) {
                        type = G.types.get(i).id;
                    }
                }
                if(G.PointPostMode == 0)
                {

                    ReportPointSimple(Token,edTitle.getText().toString(),edDescription.getText().toString()
                            ,type,String.valueOf(latitude),String.valueOf(longitude));
                    Log.e("onClick: ",String.valueOf(spinner.getSelectedItemId()+1) );
                }
                else{
                    if(G.ChoosedDocument) {


                        ReportPointAdvanced(Token,edTitle.getText().toString(),edDescription.getText().toString()
                                ,type,String.valueOf(latitude),String.valueOf(longitude));
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "لطفا یک فایل ضمیمه انتخاب کنید", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        popup.show();
    }

    private void setupCamorDevice(){
        camordevice = new Dialog(getActivity());
        camordevice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        camordevice.setContentView(R.layout.camordevice);
        camordevice.setCancelable(true);
        camordevice.getWindow().setGravity(Gravity.CENTER);
        camordevice.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup gallery=(ViewGroup)camordevice.findViewById(R.id.gallery_root);
        ViewGroup camera=(ViewGroup)camordevice.findViewById(R.id.camera_root);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            pickVideoFromGallery();
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LOG","CLICKED2");
            }
        });



        Button gallerysub=(Button)camordevice.findViewById(R.id.gallery_sub);
        Button camerasub =(Button)camordevice.findViewById(R.id.camera_sub);
        gallerysub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            pickVideoFromGallery();
            }
        });

        camerasub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void ReportPointAdvanced(String Token , String Title , String Text , String Type , String Lat , String Lng) {

        String uploadType = "";
        String uploadUrl = "";
        if(G.PointPostMode == 1)
        {
//            uploadType = "image";
//            uploadUrl=G.GetReportPointWithImageUrl();
            try {
                uploadImageFile(Title, Text, Lat, Lng, Type, Token);
            }catch (Exception ex){
                Toast.makeText(G.context, G.ERROR, Toast.LENGTH_SHORT).show();
            }
        }
        else if (G.PointPostMode == 2)
        {
            try {
                uploadVideoFile(Title, Text, Lat, Lng, Type, Token);
            }catch (Exception ex){
                Toast.makeText(G.context, G.ERROR, Toast.LENGTH_SHORT).show();
            }
        }

//
//        AndroidNetworking.upload(uploadUrl)
//                .addMultipartFile(uploadType, myFile)
//                .addMultipartParameter("token",Token)
//                .addMultipartParameter("title",Title)
//                .addMultipartParameter("text",Text)
//                .addMultipartParameter("type",Type)
//                .addMultipartParameter("lat",Lat)
//                .addMultipartParameter("lng",Lng)
//                .setPriority(Priority.HIGH)
//                .setTag("TAG")
//                .build()
//                .setUploadProgressListener(new UploadProgressListener() {
//                    @Override
//                    public void onProgress(long bytesUploaded, long totalBytes) {
//                        // do anything with progress
//                        //tvProgress.setText((bytesUploaded / totalBytes)*100 + " %");
//                        Log.e("onProgress: ", (bytesUploaded / totalBytes)*100 + " %");
//                    }
//                })
//                .getAsString(new StringRequestListener() {
//                    @Override
//                    public void onResponse(String response) {
//                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(ANError anError) {
//                        Toast.makeText(getActivity(), anError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });

    }

    private void ReportPointSimple(String Token , String Title , String Text , String Type , String Lat , String Lng){
        AndroidNetworking.post(G.GetReportPointSimpleUrl())
                .addBodyParameter("token",Token)
                .addBodyParameter("title",Title)
                .addBodyParameter("text",Text)
                .addBodyParameter("type",Type)
                .addBodyParameter("lat",Lat)
                .addBodyParameter("lng",Lng)
                //.setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                    popup.dismiss();
                        Log.e("onResponse3: ", response);
                        Toast.makeText(getActivity(), "نظر شما ثبت شد", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getActivity(), "متاسفانه ثبت نظر شما با مشکل مواجه شد", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
//
//
//        if (resultCode == RESULT_OK) {
//            final Uri selectedImageUri = data.getData();
//            String filepath = getRealPathFromURI(selectedImageUri,getActivity());
//             myFile = new File(filepath);
//            G.ChoosedDocument = true;
//
//        }else {
//           // Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();
//            G.ChoosedDocument = false;
//        }
        Log.d("LOG",reqCode+"");
        switch(reqCode) {
            case 1:
                Bitmap bitmap = GetImage.getImageFromResult(G.context, resultCode, data);
                setImage(bitmap);
                // TODO use bitmap
                break;
            case 2:
                try{
                G.ChoosedDocument=true;
                uri= GetVideo.getVideoFromResult(G.context,resultCode,data);
                String pa=uri.getPath();
                path=pa;
                Log.d("PATH","PATH "+pa);}catch (Exception e){}
                break;
            default:
                super.onActivityResult(reqCode, resultCode, data);
                break;
        }
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
//        String[] projection = { MediaStore.Images.Media.DATA };
//        @SuppressWarnings("deprecation")
//        Cursor cursor = context.managedQuery(contentURI, projection, null,
//                null, null);
//        if (cursor == null)
//            return null;
//        int column_index = cursor
//                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        if (cursor.moveToFirst()) {
//            String s = cursor.getString(column_index);
//            // cursor.close();
//            return s;
//        }
//        // cursor.close();
//        return null;
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = G.context.getContentResolver().query(contentURI, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private void setImage(Bitmap b){
        G.ChoosedDocument=true;
        FileOutputStream out =null;
        try {
//            File yekanDir = new File(Environment.getExternalStorageDirectory().getPath() + "/CityReport/");
//            File yekanDir = new File(Environment.getDataDirectory().getPath() +"/");
            File yekanDir = new File(G.context.getFilesDir() + "/CityReport/");

            if (!yekanDir.exists()) {
                Log.d("file", yekanDir.mkdirs() + ""+yekanDir.getAbsolutePath());

            }
            String rootPath = yekanDir.getPath();
            out = new FileOutputStream(rootPath + "/" + "tempFile.png");
            path=rootPath + "/" + "tempFile.png";
            b.compress(Bitmap.CompressFormat.PNG, 100, out);

        }catch (Exception ex){
            Log.d("Err",ex.getMessage().toString());
            path="";

        }finally {
            try {
                if (out != null) {
                    out.close();

                }
            } catch (IOException e) {
                e.printStackTrace(); path="";
                Log.d("Err",e.getMessage().toString());
            }
        }

    }

    private void uploadImageFile(String title,String text,String lat,String lng,String type,String token){
        G.ShowLoading(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Gonnect.cancelRequest("post");
            }
        });
        String url=G.server+"add-image.php";
        ContentValues cv=new ContentValues();cv.put("title",title);cv.put("text",text);cv.put("lat",lat);cv.put("lng",lng);
        cv.put("token",token);
        cv.put("type",type);
        Log.d("PATH",path);
        Gonnect.upload(url, cv,"post","image", "tempFile.png", "image/png", new File(path), new Gonnect.ResponseSuccessListener() {
            @Override
            public void responseRecieved(String response) {
                String res=response.trim();
                Log.d("RES",res);
                //Vaqti Movafaq shod ye kari bokon
                G.HideLoading();
                G.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(G.context, G.SUCCESS, Toast.LENGTH_SHORT).show();
                        popup.dismiss();
                    }
                });


            }
        }, new Gonnect.ResponseFailureListener() {
            @Override
            public void responseFailed(IOException exception) {
                Log.d("ERR",exception.getMessage().toString());
                //Vaqti Error Dad Yekari bokon
                G.HideLoading();
                G.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(G.context, G.ERROR, Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });

    }

    private void uploadVideoFile(String title,String text,String lat,String lng,String type,String token){
       G.ShowLoading(new DialogInterface.OnCancelListener() {
           @Override
           public void onCancel(DialogInterface dialog) {
               Gonnect.cancelRequest("post");
           }
       });
         String url=G.server+"add-video.php";
        Log.d("CAMERA",G.isCamera+"");
        if(G.isCamera){

        }else{
            path=getRealPathFromURI(uri,G.activity);
        }

        File file=new File(path);

        Log.d("finalPath",file.getAbsolutePath()+file.getName());
        String mimetype="video";
        ContentValues cv=new ContentValues();cv.put("title",title);cv.put("text",text);cv.put("lat",lat);cv.put("lng",lng);
        cv.put("token",token);
        cv.put("type",type);
        Gonnect.upload(url, cv,"post","video", file.getName(), mimetype, file, new Gonnect.ResponseSuccessListener() {
            @Override
            public void responseRecieved(String response) {
                G.HideLoading();
                String res=response.trim();
                Log.d("RES",res);
                //Vaqti Movafaq shod ye kari bokon
                Toast.makeText(G.context, G.SUCCESS, Toast.LENGTH_SHORT).show();
                popup.dismiss();

            }
        }, new Gonnect.ResponseFailureListener() {
            @Override
            public void responseFailed(IOException exception) {
                Log.d("ERR",exception.getMessage().toString());
                //Vaqti Error Dad Yekari bokon
                G.HideLoading();
                G.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(G.context, G.ERROR, Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });

    }

    private void pickVideoFromGallery(){

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),2);

    }

}
