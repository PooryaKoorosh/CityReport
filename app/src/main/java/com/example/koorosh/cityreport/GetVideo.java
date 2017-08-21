package com.example.koorosh.cityreport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GetVideo {

    private static final String TAG = "ImagePicker";
    private static final String TEMP_VIDEO_NAME = "tempVideo.mp4";
    public static Intent getPickVideoIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)));
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    "Choose");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static File getTempFile(Context context) {
        File imageFile = new File(context.getExternalCacheDir(), TEMP_VIDEO_NAME);
        imageFile.getParentFile().mkdirs();
        return imageFile;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
            Log.d(TAG, "Intent: " + intent.getAction() + " package: " + packageName);
        }
        return list;
    }


    public static Uri getVideoFromResult(Context context, int resultCode,
                                            Intent videoReturnedIntent) {
        Log.d(TAG, "getVideoFromResult, resultCode: " + resultCode);
        Bitmap bm = null;
        File videoFile = getTempFile(context);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedVideo;
            boolean isCamera = (videoReturnedIntent == null ||
                    videoReturnedIntent.getData() == null  ||
                    videoReturnedIntent.getData().toString().contains(videoFile.toString()));
            if (isCamera) {     /** CAMERA **/
                G.isCamera=true;
                selectedVideo = Uri.fromFile(videoFile);
            } else {            /** ALBUM **/
            G.isCamera=false;
                selectedVideo = videoReturnedIntent.getData();
            }
            Log.d(TAG, "selectVideo: " + selectedVideo);
        return selectedVideo;
        }
        return null;
    }

}
