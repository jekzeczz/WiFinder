package com.example.wifinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.wifinder.dummy.DummyContent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, FavoriteFragment.OnListFragmentInteractionListener {
    private GoogleMap mMap;
    private LocationManager locationManager;

    static final int REQUEST_PERMISSION = 1000;

    private double lat;
    private double lng;

    private String[] dataset = new String[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment;
            switch (menuItem.getItemId()) {
                case R.id.navigation_map:
                    Toast.makeText(getApplicationContext(), "map", Toast.LENGTH_SHORT).show();

                    return true;

                case R.id.navigation_favorite:
                    Toast.makeText(getApplicationContext(), "favorite", Toast.LENGTH_SHORT).show();
                    fragment = new FavoriteFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_review:
                    Toast.makeText(getApplicationContext(), "review", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.navigation_settings:
                    Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.map, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "nop", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
            return;
        } else {
            locationStart();

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);

        }
        Toast.makeText(this, "yes!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        // 緯度
        lat = location.getLatitude();
        lng = location.getLongitude();
        Log.e("#########", "緯度 Latitude:" + location.getLatitude());
        // 経度
        Log.e("#############", "経度 Longitude:" + location.getLongitude());

        //LatLng myLocation = new LatLng(lat, lng);
        //mMap.addMarker(new MarkerOptions().position(myLocation));

        //現在位置表示
        mMap.setMyLocationEnabled(true);

        //ズーム状態で表示
        zoomMap(lat, lng);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("#########", "location manager Enabled");
        } else {
            // GPSを設定するように促す
            Intent settingsIntent =
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.e("############", "not gpsEnable, startActivity");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);

            Log.d("############", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                REQUEST_PERMISSION, 50, this);
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
        mMap.moveCamera(CameraUpdateFactory.
                newLatLngBounds(bounds, width, height, 0));
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
