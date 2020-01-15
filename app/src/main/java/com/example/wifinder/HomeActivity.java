package com.example.wifinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.wifinder.dummy.DummyContent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements FavoriteFragment.OnListFragmentInteractionListener {

    public static final int REQUEST_PERMISSION = 1000;

    private Fragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        // permission チェック
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("########## Activity", "許可されてない");
            // 許可されてないので許可を求める
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
        } else {
            // 許可されている場合
            loadFragment();
        }
    }

    private void loadFragment() {
        // マップ表示
        if (mapFragment == null) {
            mapFragment = new MapsFragment();
        }
        loadFragment(mapFragment);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 許可されている
            loadFragment();
        } else {
            // 拒否された場合アプリ終了
            Toast.makeText(HomeActivity.this, "許可されないと利用できません", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment;
            switch (menuItem.getItemId()) {
                case R.id.navigation_map:
                    getSupportActionBar().hide();
                    if (mapFragment == null) {
                        fragment = new MapsFragment();
                    } else {
                        fragment = mapFragment;
                    }
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_favorite:
                    getSupportActionBar().show();
                    getSupportActionBar().setTitle("Favorite");
                    fragment = new FavoriteFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_settings:
                    getSupportActionBar().show();
                    getSupportActionBar().setTitle("Settings");
                    fragment = new SettingsFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
