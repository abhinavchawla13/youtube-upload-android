package com.example.kii.gmaillogin;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.Manifest;


import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.net.ssl.HttpsURLConnection;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    //Declaring EditText
    public static EditText editTextEmail;
    public static EditText editTextSubject;
    public static EditText editTextMessage;
    private Button camButton;
    private VideoView imgView;
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int CAM_REQ = 1;
    public InputStream finalStream;
    public static TextView progress;
    public static ProgressDialog progressDialog;
    public static String mEmail;



    //Send button
    private Button buttonSend;
    private Button createVideo;



    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //network

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }



        //Initializing the views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextSubject = (EditText) findViewById(R.id.editTextSubject);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        camButton = (Button) findViewById(R.id.rVideoButton);
        imgView = (VideoView) findViewById(R.id.imgView);
        createVideo = (Button) findViewById(R.id.createVideoButton);

//        buttonSend = (Button) findViewById(R.id.buttonSend);
        requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, 3423);


        progress = (TextView) findViewById(R.id.progressTV);
        progressDialog = new ProgressDialog(this);


//
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.GET_ACCOUNTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                       100);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }



//        //Adding click listener
//        buttonSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    checkAPI();
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        //Adding click listener
        createVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File mediaFile =
                        new File(LoginActivity.this.getFilesDir().getAbsolutePath()
                                + "/myvideo.mp4");
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                File file = getFile();
                Uri videoUri = Uri.fromFile(mediaFile);
//              takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                startActivityForResult(takeVideoIntent, CAM_REQ);


//                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//                startActivityForResult(takeVideoIntent, CAM_REQ);
            }
        });


        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                UploadVideo uv = new UploadVideo();

//                InputStream is = loadJSONFromAsset();
//                File f = createNewFile();
//                uv.uploadIt(is, f);
//                Intent camera_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                File file = getFile();
//                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//                startActivityForResult(camera_intent, CAM_REQ);

                pickUserAccount();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

     // Received from newChooseAccountIntent(); passed to getToken()
    String mType;
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//    }

    private File getFile(){
        File folder = new File(this.getFilesDir(), "youtube_app");
        if(!folder.exists()){
            folder.mkdir();
        }
        File vod_file = new File(folder, "123456.mp4");
        return vod_file;
    }

    private void sendEmail() {
        //Getting content for email
        String email = editTextEmail.getText().toString().trim();
        String subject = editTextSubject.getText().toString().trim();
        String message = editTextMessage.getText().toString().trim();

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);
        //Executing sendmail to send email
        sm.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == CAM_REQ) {
            Uri videoUri = data.getData();
            try {
                finalStream = getContentResolver().openInputStream(videoUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("hello", "here");
            }
            imgView.setVideoURI(videoUri);
            imgView.start();

//            Cursor videoCursor =
//                    getContentResolver().query(videoUri, null, null, null, null);
//            int sizeIndex = videoCursor.getColumnIndex(OpenableColumns.SIZE);
//            videoCursor.moveToFirst();
//            progress.setText(Long.toString(videoCursor.getLong(sizeIndex)));

//            if (resultCode == RESULT_OK) {
//                Toast.makeText(this, "Video saved to:\n" +
//                        data.getData(), Toast.LENGTH_LONG).show();
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "Video recording cancelled.",
//                        Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Failed to record video",
//                        Toast.LENGTH_LONG).show();
//            }
        }
        else if(requestCode == REQUEST_CODE_PICK_ACCOUNT){
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                mType = data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
                // With the account name acquired, go get the auth token
                Account account = new Account(mEmail, mType);
                getUsername(account);
                Log.d("account", account.toString());
//                Log.d("email", mEmail);
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, "Picked Account", Toast.LENGTH_SHORT).show();
            }

            // Handle the result from exceptions
//        ...
        }
    }

//    String mEmail; // Received from newChooseAccountIntent(); passed to getToken()
    String SCOPE = "oauth2:https://www.googleapis.com/auth/youtube.upload";

    /**
     * Attempts to retrieve the username.
     * If the account is not yet known, invoke the picker. Once the account is known,
     * start an instance of the AsyncTask to get the auth token and do work with it.
     */
    private void getUsername(Account account) {
        if (mEmail == null) {
            pickUserAccount();
        } else {
//            if (isDeviceOnline()) {
            Log.i("BeforeGetUserNameTask", finalStream.toString());
                new GetUsernameTask(LoginActivity.this, account, SCOPE, finalStream).execute();
                progressDialog.setMessage("Uploading Video");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setIndeterminate(false);
                progressDialog.setProgress(0);
                progressDialog.setMax(100);
                progressDialog.show();

//            } else {
//                Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
//            }
        }
    }



//    public File createNewFile(){
//        File file = new File(getFilesDir(), "dataStore");
//        return file;
//

    public void checkAPI() throws IOException, JSONException {

        String url = "http://10.0.1.8:8080/api/youtube-tester";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "url=12345678";

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

    public void setText(final Double value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setText(Double.toString(value));
            }
        });
    }

}

