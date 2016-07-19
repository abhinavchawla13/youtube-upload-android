package com.example.kii.gmaillogin;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kii on 2016-07-19.
 */
public class VideoCapture {

    public static long videoSize = 5;
    public static final int MEDIA_TYPE_VIDEO = 2;



    /** Create a file Uri for saving an image or video
     * @param context*/
    public static Uri getOutputMediaFileUri(Context context){
        return Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_VIDEO, context));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type, Context context){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(context.getFilesDir().getAbsolutePath() , "YouTube_Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
