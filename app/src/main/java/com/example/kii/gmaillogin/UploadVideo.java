package com.example.kii.gmaillogin;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.vision.text.Text;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kii on 2016-07-12.
 */


/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */



/**
 * Upload a video to the authenticated user's channel. Use OAuth 2.0 to
 * authorize the request. Note that you must add your video files to the
 * project folder to upload them with this application.
 *
 * @author Jeremy Walker
 */
public class UploadVideo {

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Define a global variable that specifies the MIME type of the video
     * being uploaded.
     */
    private static final String VIDEO_FILE_FORMAT = "video/*";

    private static final String SAMPLE_VIDEO_FILENAME = "sample-video.mp4";

    /**
     * Upload the user-selected video to the user's YouTube channel. The code
     * looks for the video in the application's project folder and uses OAuth
     * 2.0 to authorize the API request.
     *
     * @param args command line args (not used).
     */

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;


    public static void uploadIt(Credential credential, final Context context, InputStream finalStream){
//        // This OAuth 2.0 access scope allows an application to upload files
        // to the authenticated user's YouTube channel, but doesn't allow
        // other types of access.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.upload");

        try {
//             Authorize the request.
//            Credential credential = Auth.authorize(scopes, "uploadvideo");
//             This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("AbhinavTest")
                    .setGoogleClientRequestInitializer(new YouTubeRequestInitializer("556216215455-l1vjfnimatis52t242v9g72m3j9kiokt.apps.googleusercontent.com"))
//                    .setYouTubeRequestInitializer(new YouTubeRequestInitializer("AIzaSyA_wS4nqtaxPT5XvX3_IV6n9uot24YPNJ8"))
                    .build();

//            if (credential.getSelectedAccountName() == null) {
//                credential.getSelectedAccountName();
//
//            }
//          Log.d("youtube", "abc");
//            System.out.println("Uploading: " + SAMPLE_VIDEO_FILENAME);
//
            // Add extra information to the video before uploading.
            Video videoObjectDefiningMetadata = new Video();

            // Set the video to be publicly visible. This is the default
            // setting. Other supporting settings are "unlisted" and "private."
            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus("public");
            videoObjectDefiningMetadata.setStatus(status);

            // Most of the video's metadata is set on the VideoSnippet object.
            VideoSnippet snippet = new VideoSnippet();

            // This code uses a Calendar instance to create a unique name and
            // description for test purposes so that you can easily upload
            // multiple files. You should remove this code from your project
            // and use your own standard names instead.
            Calendar cal = Calendar.getInstance();
            snippet.setTitle("Test Upload via Java on " + cal.getTime());
            snippet.setDescription(
                    "Video uploaded via YouTube Data API V3 using the Java library " + "on " + cal.getTime());

            // Set the keyword tags that you want to associate with the video.
            List<String> tags = new ArrayList<String>();
            tags.add("test");
            tags.add("example");
            tags.add("java");
            tags.add("YouTube Data API V3");
            tags.add("erase me");
            snippet.setTags(tags);


            // Add the completed snippet object to the video resource.
          videoObjectDefiningMetadata.setSnippet(snippet);
          InputStream is = finalStream;
//            Log.i("is", is.toString());



            InputStreamContent mediaContent = new InputStreamContent(VIDEO_FILE_FORMAT,
                    is);
            final double total = (double) is.available();

            // Insert the video. The command sends three arguments. The first
            // specifies which information the API request is setting and which
            // information the API response should return. The second argument
            // is the video resource that contains metadata about the new video.
            // The third argument is the actual video content.
            YouTube.Videos.Insert videoInsert = youtube.videos()
                    .insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent);

            // Set the upload type and add an event listener.
            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

            // Indicate whether direct media upload is enabled. A value of
            // "True" indicates that direct media upload is enabled and that
            // the entire media content will be uploaded in a single request.
            // A value of "False," which is the default, indicates that the
            // request will use the resumable media upload protocol, which
            // supports the ability to resume an upload operation after a
            // network interruption or other transmission failure, saving
            // time and bandwidth in the event of network failures.
            uploader.setDirectUploadEnabled(false);

            MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener() {
                public void progressChanged(MediaHttpUploader uploader) throws IOException {
                    switch (uploader.getUploadState()) {
                        case INITIATION_STARTED:
                            System.out.println("Initiation Started");
                            break;
                        case INITIATION_COMPLETE:
                            System.out.println("Initiation Completed");
                            break;
                        case MEDIA_IN_PROGRESS:
                            System.out.println("Upload in progress");
                            final double progressPerc = ((double) (uploader.getNumBytesUploaded())/total)*100;
                            final DecimalFormat df = new DecimalFormat("#.#");


                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    // code goes here
//                                    LoginActivity.progress.setText(df.format(progressPerc) + "%");
                                    LoginActivity.progressDialog.setProgress((int) Math.ceil(progressPerc));

                                }
                            });

//                            LoginActivity.runTestOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                }
//                            });
//
                            System.out.println("Upload percentage: " + progressPerc + "%");
                            break;
                        case MEDIA_COMPLETE:
                            System.out.println("Upload Completed!");
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    // code goes here
                                    Toast t = Toast.makeText(context, "Upload Successfull! Sit back and Relax while email is being sent!", Toast.LENGTH_LONG);
//                                    LoginActivity.progress.setText("Upload finished! Sit back, and relax while email is being sent.");
                                    LoginActivity.progressDialog.setProgress(100);
                                    LoginActivity.progressDialog.dismiss();
                                    t.show();
                                }
                            });
                            break;
                        case NOT_STARTED:
                            System.out.println("Upload Not Started!");
                            break;
                    }
                }
            };
            uploader.setProgressListener(progressListener);

            // Call the API and upload the video.
            Video returnedVideo = videoInsert.execute();


//            // Print data about the newly inserted video from the API response.
//            System.out.println("\n================== Returned Video ==================\n");
//            System.out.println("  - Id: " + returnedVideo.getId());
//            System.out.println("  - Title: " + returnedVideo.getSnippet().getTitle());
//            System.out.println("  - Tags: " + returnedVideo.getSnippet().getTags());
//            System.out.println("  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
//            System.out.println("  - Video Count: " + returnedVideo.getStatistics().getViewCount());
            Log.d("returnedVideo.id", returnedVideo.getId());
            Log.d("returnedVideo.getPro", returnedVideo.toString());



            checkAPI(returnedVideo.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkAPI(String id) throws IOException, JSONException {

        String url = "http://192.168.0.14:8080/api/youtube-tester";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "url=" + id + "&email=" + LoginActivity.editTextEmail.getText() +
                "&subject=" + LoginActivity.editTextSubject.getText() +
                "&message=" + LoginActivity.editTextMessage.getText() +
                "&userMail=" + LoginActivity.mEmail;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();


        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        Log.d("a", response.toString());

    }

}