package com.main.aparatgps.photo;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.main.aparatgps.photo.googledriveshare.GoogleDriveShare;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageMetadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;


public class PhotoActivity extends AppCompatActivity {

    private String photoPath;
    FloatingActionButton bluetoothButton;
    FloatingActionButton deleteButton;
    FloatingActionButton noteButton;
    FloatingActionButton googleDriveButton;

    String nazwa;
    TextView photoNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Intent intent = getIntent();
        photoPath = intent.getStringExtra("path");

        photoNote = findViewById(R.id.photoNote);

        //Buttons
        bluetoothButton = findViewById(R.id.shareViaBluetoothButton);
        deleteButton = findViewById(R.id.deletePhotoButton);
        noteButton = findViewById(R.id.noteButton);
        googleDriveButton = findViewById(R.id.googleDrivePhotoButton);

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

        String [] separatedPhotoPath = photoPath.split("/");
        int arraySize = separatedPhotoPath.length;
        nazwa = separatedPhotoPath[arraySize-1];

        //TextView photoNote
        loadNote(nazwa, photoNote);

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
            Toast.makeText(this, "Bluetooth doesn't work", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
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

        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
                intent.putExtra("nazwa", nazwa);
                startActivity(intent);
            }
        });

        noteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "Add/edit note", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        });

        googleDriveButton.setOnClickListener(v -> {
            Intent intentGoogleDrive = new Intent(
                    getApplicationContext(),
                    GoogleDriveShare.class);
            intentGoogleDrive.putExtra("photoPath", photoPath);
            startActivity(intentGoogleDrive);
        });
        }

    @Override
    protected void onResume() {
        super.onResume();
        loadNote(nazwa, photoNote);
    }

    public void openBluetooth(View view) {
        Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
        intent.putExtra("photoPath", photoPath);
        startActivity(intent);
    }

    public void loadNote(String nazwa, TextView textView){
        FileInputStream fis = null;
        try {
            fis = openFileInput(nazwa);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine())!=null){
                sb.append(text).append("\n");
            }

            textView.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

