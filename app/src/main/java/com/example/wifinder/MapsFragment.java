package com.example.wifinder;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private LocationManager locationManager;

    static final int REQUEST_PERMISSION = 1000;

    private double lat;
    private double lng;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings us = mMap.getUiSettings();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(getActivity(), "nop", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
            return;
        }


        locationStart();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, REQUEST_PERMISSION, 50, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, REQUEST_PERMISSION, 50, this);

        us.setZoomControlsEnabled(true);
        //Toast.makeText(getActivity(), "yes!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("#", "checkSelfPermission true");

                locationStart();
            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(getActivity(),
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // 緯度
        lat = location.getLatitude();
        lng = location.getLongitude();
        Log.e("#########", "緯度 Latitude:" + location.getLatitude());
        // 経度
        Log.e("#############", "経度 Longitude:" + location.getLongitude());

        // Latitude:35.775315
        // Longitude:139.79716333333332
        //LatLng sydney = new LatLng(lat, lng);
        //mMap.addMarker(new MarkerOptions().position(sydney)); //.title("Marker in Sydney")

        //現在位置表示
        mMap.setMyLocationEnabled(true);
        zoomMap(lat, lng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void locationStart() {
        // LocationManager インスタンス生成
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("#########", "location manager Enabled");
        } else {
            // GPSを設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.e("############", "not gpsEnable, startActivity");
        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);

            Log.d("############", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, REQUEST_PERMISSION, 50, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, REQUEST_PERMISSION, 50, this);

    }

    private void zoomMap(double latitude, double longitude) {
        // 表示する東西南北の緯度経度を設定
        double south = latitude * (1 - 0.00005);
        double west = longitude * (1 - 0.00005);
        double north = latitude * (1 + 0.00005);
        double east = longitude * (1 + 0.00005);

        // LatLngBounds (LatLng southwest, LatLng northeast)
        LatLngBounds bounds = LatLngBounds.builder()
                .include(new LatLng(south, west))
                .include(new LatLng(north, east))
                .build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        // static CameraUpdate.newLatLngBounds(LatLngBounds bounds, int width, int height, int padding)
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, 0));
    }
}
