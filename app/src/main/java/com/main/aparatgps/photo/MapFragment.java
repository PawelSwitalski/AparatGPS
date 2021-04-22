package com.main.aparatgps.photo;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.main.aparatgps.R;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {


    /*
    public MapFragment() {
        super(R.layout.fragment_map);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        int someInt = requireArguments().getInt("some_int");
        this.latitude = requireArguments().getString("latitude");
        this.longitude = requireArguments().getString("longitude");

        TextView textView = view.findViewById(R.id.fragment_textView);
        textView.setText("nowe\nnowe\nnowe\nnowe\nnowe\nnowe\nnowe\nnowe\nnowe\nnowe\nnowe\nnowe");

    }

     */



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String latitude;
    private String longitude;

    private GoogleMap mMap;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param latitude GPS latitude
     * @param longitude GPS longitude
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance(String param1, String param2, String latitude, String longitude) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(LATITUDE, latitude);
        args.putString(LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            this.latitude = getArguments().getString(LATITUDE);
            this.longitude = getArguments().getString(LONGITUDE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        TextView textView = (TextView) view.findViewById(R.id.fragment_textView);
        textView.setText(latitude);

        MapView mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.getMapAsync(this);


        //return inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}