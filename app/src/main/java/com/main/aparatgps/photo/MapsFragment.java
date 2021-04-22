package com.main.aparatgps.photo;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.main.aparatgps.R;

public class MapsFragment extends Fragment {

    String latitude;
    String longitude;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        //double latitudeNumber = getNumber(latitude);
        //double longitudeNumber = getNumber(longitude);


        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = new LatLng(-34, 151);
            //LatLng sydney = new LatLng(getNumber(latitude), getNumber(longitude));
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }

        /*
        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
        */
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_maps, container, false);

        //TextView textView = (TextView) view.findViewById(R.id.fragment_textView);
        //textView.setText(latitude);

        /*
        assert savedInstanceState != null;

         */
        //latitude = savedInstanceState.getString("latitude");
        //longitude = savedInstanceState.getString("longitude");


        return inflater.inflate(R.layout.fragment_maps, container, false);
        //return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //assert savedInstanceState != null;
        //latitude = savedInstanceState.getString("latitude");
        //longitude = savedInstanceState.getString("longitude");
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    /**
     * Use this factory method to convert
     * latitude and longitude
     *
     * @param value GPS latitude or longitude
     * @return converted number.
     */
    public static double getNumber(String value){
        String result = value.substring(0, value.indexOf(",", value.indexOf(",") + 1));
        return Double.parseDouble(result.replace(",", "."));
    }
}