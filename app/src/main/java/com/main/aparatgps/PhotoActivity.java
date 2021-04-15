package com.main.aparatgps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;


public class PhotoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Intent intent = getIntent();
        photoPath = intent.getStringExtra("path");

        // ImageView
        ImageView imageView = findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        imageView.setImageBitmap(Bitmap.createBitmap(bitmap));

        // TextView photoPath
        TextView textViewPath = findViewById(R.id.photoPath);
        textViewPath.setText(photoPath);

        // TextView photoData

        /*
        TextView textViewData = findViewById(R.id.photoData);
        double[] latitude = new WriteExifMetadata().getGPSLatitude(photoPath);
        double[] longitude = new WriteExifMetadata().getGPSLongitude(photoPath);
        String viewDataString = "latitude: " + Arrays.toString(latitude) +
                "\nlongitude: " + Arrays.toString(longitude);
        //textViewData.setText(viewDataString);

         */


        /*
        getContentResolver().
        MediaStore.Images.Media.
         */

    }


    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}