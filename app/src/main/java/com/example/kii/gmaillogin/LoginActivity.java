package com.example.kii.gmaillogin;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import android.Manifest;


import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    //Declaring EditText
    private EditText editTextEmail;
    private EditText editTextSubject;
    private EditText editTextMessage;
    private Button camButton;
    private VideoView imgView;
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int CAM_REQ = 1;


    //Send button
    private Button buttonSend;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing the views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextSubject = (EditText) findViewById(R.id.editTextSubject);
        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        camButton = (Button) findViewById(R.id.rVideoButton);
        imgView = (VideoView) findViewById(R.id.imgView);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, 3423);
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



        //Adding click listener
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
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
        Log.d("ABC", "ABC");
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    String mEmail; // Received from newChooseAccountIntent(); passed to getToken()
    String mType;
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//    }

    private File getFile(){
        File folder = new File("sdcard/camera_app");
        if(!folder.exists()){
            folder.mkdir();
        }
        File vod_file = new File(folder, "cam_vod.mp4");
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
            String path = "sdcard/camera_app/cam_vod.mp4";
            imgView.setVideoPath(path);
            imgView.start();
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
                new GetUsernameTask(LoginActivity.this, account, SCOPE).execute();
//            } else {
//                Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
//            }
        }
    }

    public InputStream loadJSONFromAsset() {
        String json = null;
        InputStream is;
        try {
             is = getAssets().open("client_secrets.json");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return is;

    }

    public InputStream getVideoStream() {
        InputStream is;
        try {
            is = getAssets().open("Sample1mb.mp4");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return is;
    }

//    public File createNewFile(){
//        File file = new File(getFilesDir(), "dataStore");
//        return file;
//    }
}

