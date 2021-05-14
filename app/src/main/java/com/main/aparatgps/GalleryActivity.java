package com.main.aparatgps;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.main.aparatgps.photo.DatabaseHelper;
import com.main.aparatgps.photo.PhotoActivity;

import java.util.Calendar;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    List<String> images;

    private  static final int MY_READ_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = findViewById(R.id.recyclerview_gallery_images);

        //sprawdzanie permission
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(GalleryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        }
        else{
            loadImages();
            DatabaseHelper dataBaseHelper = new DatabaseHelper(getApplicationContext(), "FavouritePhotos", null, 1);
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        }
    }

    private void loadImages(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        images = ImageGallery.listOfImages(this);
        galleryAdapter = new GalleryAdapter(getApplicationContext(), images, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String path) {
                // Start new photo activity
                Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);

                //do something with photo
            }

            @Override
            public void onLongPhotoClick(String path) {
                Log.i("long click", path);
            }
        });

        recyclerView.setAdapter(galleryAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==MY_READ_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Read external storage permission granted", Toast.LENGTH_SHORT).show();
                        loadImages();
                    }
                }, 500);
            }
            else{
                Toast.makeText(this, "Read external storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        recyclerView = findViewById(R.id.recyclerview_gallery_images);

        //sprawdzanie permission
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(GalleryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        }
        else{
            loadImages();
        }
        super.onResume();
    }
}