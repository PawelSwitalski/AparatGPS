package com.main.aparatgps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.*;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.VideoCapture;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.view.CameraView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.main.aparatgps.photo.DatabaseHelper;
import com.main.aparatgps.photo.DriveServieHelper;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static androidx.camera.core.VideoCapture.*;


public class MainActivity extends AppCompatActivity {

    static Button btnClose, btnLens, btnVideo, btnStop, btnPhoto, btnGallery;

    DriveServieHelper driveServieHelper;

    private Executor executor = Executors.newSingleThreadExecutor();
    CameraSelector cameraSelector;
    CameraView mCameraView;

    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO",
            "android.permission.ACCESS_FINE_LOCATION", "android.permission.INTERNET",
            "android.permission.ACCESS_COARSE_LOCATION", "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN", "android.permission.ACCESS_FINE_LOCATION"};

    private LocationManager locationManager;

    private String locationStr;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestSignIn();
        if (allPermissionsGranted()) {
            startGPS();
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS);
        }
        DatabaseHelper dataBaseHelper = new DatabaseHelper(getApplicationContext(), "FavourtiePhotos", null, 1);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
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
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(MainActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
                credential.setSelectedAccount(googleSignInAccount.getAccount());
                Drive googleDriveService = new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential
                ).setApplicationName("Android GPS").build();
                driveServieHelper = new DriveServieHelper(googleDriveService);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void startGPS() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // GPS methods

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String str = "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG);
                locationStr = str;
                // TODO Add better localisation info
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }


    private void startCamera() {
        btnPhoto = findViewById(R.id.btnPhoto);
        btnVideo = findViewById(R.id.btnVideo);
        btnStop = findViewById(R.id.btnStop);
        btnLens = findViewById(R.id.btnLens);
        btnClose = findViewById(R.id.btnClose);
        btnGallery = findViewById(R.id.btnGallery);
        mCameraView = findViewById(R.id.view_finder);
        mCameraView.setFlash(ImageCapture.FLASH_MODE_AUTO);
        //can set flash mode to auto,on,off...
        ImageCapture.Builder builder = new ImageCapture.Builder();
        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);
        // if has hdr (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable hdr.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mCameraView.bindToLifecycle((LifecycleOwner) MainActivity.this);

        // set click listener to all buttons

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                startActivity(intent);
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCameraView.isRecording()){return;}

                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                String name = mDateFormat.format(new Date()) + ".jpg";
                String name2 = mDateFormat.format(new Date()) + "exif.jpg";
                final File file1 = new File(getBatchDirectoryName(), name);

                mCameraView.setCaptureMode(CameraView.CaptureMode.IMAGE);
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file1).build();
                mCameraView.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    final File fileExif = new WriteExifMetadata().modifyExif(file1, longitude, latitude);
                                    galleryAddPic(fileExif, 0);
                                    file1.delete();
                                    driveServieHelper.createImage(name2);
                                    FileOutputStream fos = null;
                                    try {
                                        fos = openFileOutput(name2, MODE_PRIVATE);
                                        String tekst = " ";
                                        fos.write(tekst.getBytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } finally {
                                        if(fos==null){
                                            try{
                                                if (fos != null) {
                                                    fos.close();
                                                }
                                            }
                                            catch (IOException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } catch (ImageReadException | ImageWriteException | IOException e) {
                                    e.printStackTrace();
                                }



                                //galleryAddPic(file1, 0);
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        error.printStackTrace();
                    }
                }); //image saved callback end
            } //onclick end
        }); //btnPhoto click listener end


        btnVideo.setOnClickListener(v -> {
            if(mCameraView.isRecording()){return;}

            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date()) + ".mp4");

            mCameraView.setCaptureMode(CameraView.CaptureMode.VIDEO);
            mCameraView.startRecording(file, executor, new VideoCapture.OnVideoSavedCallback() {

                @Override
                public void onVideoSaved(@NonNull OutputFileResults outputFileResults) {
                    galleryAddPic(file, 1);
                }

                @Override
                public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                    //Log.i("TAG",message);
                    mCameraView.stopRecording();
                }

            }); //image saved callback end
        }); //video listener end


        btnStop.setOnClickListener(v -> {
            if (mCameraView.isRecording()) {
                mCameraView.stopRecording();
            }
        });


        //close app
        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCameraView.isRecording()) {
                    mCameraView.stopRecording();
                }
                finish();
            }
        });// on click listener end


        btnLens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO delete logs
                Log.e("info", "Log dzia??a");
                //Log.e("Localisation info", locationStr);
                if (mCameraView.isRecording()) {
                    return;
                }

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if (mCameraView.hasCameraWithLensFacing(CameraSelector.LENS_FACING_FRONT)) {
                    mCameraView.toggleCamera();
                } else {
                    return;
                }
            }//onclick end
        }); // btnLens listener end


    } //start camera end


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraView.isRecording()) {
            mCameraView.stopRecording();
        }
        finish();
    }


    public boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }


    public String getBatchDirectoryName() {
        String app_folder_path;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            app_folder_path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        } else {
            app_folder_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera";
        }

        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {
        }
        return app_folder_path;
    }


    private void galleryAddPic(File originalFile, int mediaType) {
        if (!originalFile.exists()) {
            return;
        }

        int pathSeparator = String.valueOf(originalFile).lastIndexOf('/');
        int extensionSeparator = String.valueOf(originalFile).lastIndexOf('.');
        String filename = pathSeparator >= 0 ? String.valueOf(originalFile).substring(pathSeparator + 1) : String.valueOf(originalFile);
        String extension = extensionSeparator >= 0 ? String.valueOf(originalFile).substring(extensionSeparator + 1) : "";

        // Credit: https://stackoverflow.com/a/31691791/2373034
        String mimeType = extension.length() > 0 ? MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.ENGLISH)) : null;

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, filename);
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);

        if (mimeType != null && mimeType.length() > 0)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);

        Uri externalContentUri;
        if (mediaType == 0) {
            externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            values.put(MediaStore.Images.Media.LATITUDE, latitude);
            values.put(MediaStore.Images.Media.LONGITUDE, longitude);
        } else if (mediaType == 1) {
            externalContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            values.put(MediaStore.Video.Media.LATITUDE, latitude);
            values.put(MediaStore.Video.Media.LONGITUDE, longitude);
        } else {
            externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }


        // Android 10 restricts our access to the raw filesystem, use MediaStore to save media in that case
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Camera");
            values.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.MediaColumns.IS_PENDING, true);

            Uri uri = getContentResolver().insert(externalContentUri, values);
            if (uri != null) {
                try {
                    if (WriteFileToStream(originalFile, getContentResolver().openOutputStream(uri))) {
                        values.put(MediaStore.MediaColumns.IS_PENDING, false);
                        getContentResolver().update(uri, values, null, null);
                    }
                } catch (Exception e) {
                    getContentResolver().delete(uri, null, null);
                }
            }
            originalFile.delete();
        } else {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(originalFile));
            sendBroadcast(mediaScanIntent);
        }

    } //gallery add end


    public static boolean WriteFileToStream(File file, OutputStream out){
        try
        {
            InputStream in = new FileInputStream( file );
            try
            {
                byte[] buf = new byte[1024];
                int len;
                while( ( len = in.read( buf ) ) > 0 )
                    out.write( buf, 0, len );
            }
            finally
            {
                try
                {
                    in.close();
                }
                catch( Exception e )
                {
                    //Log.e( "Unity", "Exception:", e );
                }
            }
        }
        catch( Exception e )
        {
            //Log.e( "Unity", "Exception:", e );
            return false;
        }
        finally
        {
            try
            {
                out.close();
            }
            catch( Exception e )
            {
                //Log.e( "Unity", "Exception:", e );
            }
        }
        return true;
    } //write end




    public void createEXIF(File file){
        /*
        try{
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, mGpsLocation.getLatDms());
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, mGpsLocation.getLonDms());
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, mGpsLocation.getAltDms());
            exif.saveAttributes();
        } catch(IOException e){
            e.printStackTrace();
        }
        */

    }


}//Main activity end
