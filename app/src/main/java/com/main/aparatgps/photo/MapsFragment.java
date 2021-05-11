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


    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private String latitude;
    private String longitude;

    private GoogleMap mMap;

    public MapsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @param latitude GPS latitude
     * @param longitude GPS longitude
     * @return A new instance of fragment MapFragment.
     */
    public static MapsFragment newInstance(String param1, String param2, String latitude, String longitude) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(LATITUDE, latitude);
        args.putString(LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.latitude = getArguments().getString(LATITUDE);
            this.longitude = getArguments().getString(LONGITUDE);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setMapMarker();
    }

    /**
     * Use this factory method to convert
     * latitude and longitude
     *
     * @param value GPS latitude or longitude
     * @return converted number. Or default value 0.0
     */
    public static double getNumber(String value){
        //String result = value.substring(0, value.indexOf(",", value.indexOf(",") + 1));
        String result;
        try {
            result = value.substring(0, value.indexOf(",", value.indexOf(",") + 1));
        } catch (StringIndexOutOfBoundsException exception) {
            result = "0.0";
        }

        return Double.parseDouble(result.replace(",", "."));
    }


    private void setMapMarker(){
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {


            OnMapReadyCallback callback = new OnMapReadyCallback() {

                /**
                 * Manipulates the map once available.
                 * This callback is triggered when the map is ready to be used.
                 * This is where we can add markers or lines, add listeners or move the camera.
                 * In this case, we just add a marker near Sydney, Australia.
                 * If Google Play services is not installed on the device, the user will be prompted to
                 * install it inside the SupportMapFragment. This method will only be triggered once the
                 * user has installed Google Play services and returned to the app.
                 */

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    //LatLng sydney = new LatLng(-34, 151);
                    LatLng sydney = new LatLng(getNumber(latitude), getNumber(longitude));
                    googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                }
            };
            mapFragment.getMapAsync(callback);
        }
    }
}