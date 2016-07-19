package com.example.kii.gmaillogin;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by kii on 2016-07-13.
 */


public class GetUsernameTask extends AsyncTask {
        Activity mActivity;
        String mScope;
        Account maccount;
        InputStream finalStream;
        private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        private static final JsonFactory JSON_FACTORY = new JacksonFactory();
        static final String client_id = "server:client_id:" + "556216215455-en2cq3o97lhalrmsarjris34oqrgetbv.apps.googleusercontent.com";
        static final String client_secret = "ofJIVGt4D3yuVuHutrrWLkSr";


        GetUsernameTask(Activity activity, Account account, String scope, InputStream finalStream){
        this.mActivity=activity;
        this.mScope=scope;
        this.maccount=account;
        this.finalStream = finalStream;
        }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            final String token = fetchToken();
            if (token != null) {
                // **Insert the good stuff here.**
                // Use the token to access the user's Google data.
//                ...
                Log.d("token", token);
//                GoogleCredential credential =
//                        new GoogleCredential.Builder()
//                                .setTransport(HTTP_TRANSPORT)
//                                .setJsonFactory(JSON_FACTORY)
//                                .setServiceAccountScopes(Arrays.asList(mScope))
//                                .setServiceAccountId("556216215455-l1vjfnimatis52t242v9g72m3j9kiokt.apps.googleusercontent.com")
//                                .build();
//                credential.setAccessToken(token);



                GoogleCredential credential = new GoogleCredential.Builder()
                        .setTransport(Auth.HTTP_TRANSPORT).setJsonFactory(Auth.JSON_FACTORY)
                        .setClientSecrets(client_id, client_secret).setRequestInitializer((new HttpRequestInitializer(){
                            @Override
                            public void initialize(HttpRequest request)
                                    throws IOException {
                                request.getHeaders().put("Authorization", "Bearer " + token);
                            }
                        })).build();
                                credential.setAccessToken(token);




//                //=============
//                //CHECK THIS OUT
//                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
//                        mActivity, Arrays.asList(mScope))
//                        .setSelectedAccountName(maccount.name);
//                //===============

//               GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
//                mActivity, client_id)
//                .setSelectedAccountName(maccount.name);

                UploadVideo.uploadIt(credential, mActivity.getApplicationContext(), finalStream);
            }
            else{
                Log.d("abc", "abc");
            }
        } catch (IOException e) {
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
//            ...
        }
        return null;
    }

    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
            String a =  GoogleAuthUtil.getToken(mActivity, maccount, mScope);
            return a;
        } catch (UserRecoverableAuthException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    1001);
            fetchToken();
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
//            mActivity.handleException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            Log.d("fm", fatalException.getMessage());
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.

        }
        return null;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        doInBackground();
//    }

    }


