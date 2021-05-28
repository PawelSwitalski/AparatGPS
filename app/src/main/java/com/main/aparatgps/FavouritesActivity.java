package com.main.aparatgps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.main.aparatgps.photo.DatabaseHelper;
import com.main.aparatgps.photo.PhotoActivity;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity {

    RecyclerView favouritesView;
    GalleryAdapter favouritesAdapter;
    List<String> images;
    List<String> favourites;

    DatabaseHelper dataBaseHelper;
    SQLiteDatabase db;

    private  static final int MY_READ_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        favouritesView = findViewById(R.id.recyclerview_favorites);

        //sprawdzanie permission
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FavouritesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        }
        else{
            dataBaseHelper = new DatabaseHelper(getApplicationContext(), "FavouritePhotos", null, 1);
            db = dataBaseHelper.getWritableDatabase();
            loadImages();
        }
    }

    private void loadImages(){
        favouritesView.setHasFixedSize(true);
        favouritesView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        images = ImageGallery.listOfImages(this);
        favourites = loadFavourites(images);
        favouritesAdapter = new GalleryAdapter(getApplicationContext(), favourites, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String path) {
                Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }

            @Override
            public void onLongPhotoClick(String path) {

            }
        });

        favouritesView.setAdapter(favouritesAdapter);
    }

    public List<String> loadFavourites(List<String> images){
        List<String> favourites = new ArrayList<String>();
        boolean hasNext = true;
        Cursor cursor = db.query("FavouritePhotos", new String[]{"_ID", "Absolute_Path"}, null, null, null, null, null);
        if(cursor.moveToFirst()){
            while(hasNext){
                favourites.add(cursor.getString(1));
                if(cursor.isLast()){
                    hasNext=false;
                }
            }
        }
        return favourites;
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
        favouritesView = findViewById(R.id.recyclerview_favorites);

        //sprawdzanie permission
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FavouritesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        }
        else{
            loadImages();
        }
        super.onResume();
    }
}