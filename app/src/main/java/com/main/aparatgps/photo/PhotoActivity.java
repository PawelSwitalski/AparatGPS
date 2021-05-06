package com.main.aparatgps.photo;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.main.aparatgps.R;
import com.main.aparatgps.WriteExifMetadata;
import com.main.aparatgps.photo.bluetooth.BluetoothActivity;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageMetadata;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class PhotoActivity extends AppCompatActivity {

    private String photoPath;
    FloatingActionButton bluetoothButton;
    FloatingActionButton deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Intent intent = getIntent();
        photoPath = intent.getStringExtra("path");

        //Buttons
        bluetoothButton = findViewById(R.id.shareViaBlootothButton);
        deleteButton = findViewById(R.id.deletePhotoButton);

        // ImageView
        ImageView imageView = findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
        imageView.setImageBitmap(Bitmap.createBitmap(bitmap));

        // TextView photoPath
        TextView textViewPath = findViewById(R.id.photoPath);
        textViewPath.setText(photoPath);

        // TextView photoData
        TextView textViewData = findViewById(R.id.photoData);
        String latitude = new WriteExifMetadata().getGPSLatitude(photoPath);
        String longitude = new WriteExifMetadata().getGPSLongitude(photoPath);
        String viewDataString = "latitude: " + latitude +
                "\nlongitude: " + longitude;
        textViewData.setText(viewDataString);

        // MapFragment
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString("latitude", latitude);
            bundle.putString("longitude", longitude);
            bundle.putInt("some_int", 0);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.activity_photo_fragment, MapsFragment.class, bundle)
                    .commit();
        }
        
        // Bluetooth adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d("Bluetooth PhotoActivity", "bluetoothAdapter == null");
            Toast.makeText(this, "bluetooth nie działa", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
            }
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File fdelete = new File(photoPath);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Photo deleted", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Can't delete photo", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });

  
    public void openBluetooth(View view) {
        Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
        intent.putExtra("photoPath", photoPath);
        startActivity(intent);
    }
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
    /*
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

     */
