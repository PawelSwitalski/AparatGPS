package com.main.aparatgps.photo.googledriveshare;

import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.main.aparatgps.R;
import com.main.aparatgps.photo.DriveServieHelper;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.util.Collections;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class GoogleDriveShare extends AppCompatActivity {
    private DriveServieHelper driveServieHelper;
    private DriveServiceHelper2 driveServiceHelper2;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_drive_shere);
        Intent intent = getIntent();
        photoPath = intent.getStringExtra("photoPath");

        requestSignIn();


        query();
        try {
            //driveServieHelper.shareImage(photoPath, "testUser");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void query() {
        if (driveServiceHelper2 != null) {
            //Log.d(TAG, "Querying for files.");

            driveServiceHelper2.queryFiles()
                    .addOnSuccessListener(fileList -> {
                        StringBuilder builder = new StringBuilder();
                        for (File file : fileList.getFiles()) {
                            builder.append(file.getName()).append("\n");
                        }
                        String fileNames = builder.toString();
                        System.out.println();

                        //setReadOnlyMode();
                    })
                    .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
        }
    }

    private void requestSignIn() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestScopes(new Scope(DriveScopes.DRIVE_FILE)).build();

        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        startActivityForResult(client.getSignInIntent(), 400);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 400:
                if(requestCode == 400){
                    handleSignInIntent(data);
                }
                break;
        }
    }

    private void handleSignInIntent(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                //GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singleton(DriveScopes.DRIVE_FILE));
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singleton(DriveScopes.DRIVE));
                credential.setSelectedAccount(googleSignInAccount.getAccount());
                Drive googleDriveService = new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential
                ).setApplicationName("Android GPS").build();
                driveServieHelper = new DriveServieHelper(googleDriveService);
                driveServiceHelper2 = new DriveServiceHelper2(googleDriveService);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}