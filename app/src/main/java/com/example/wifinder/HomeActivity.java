package com.example.wifinder;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.wifinder.dummy.DummyContent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity implements FavoriteFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        BottomNavigationView navView = findViewById(R.id.nav_view);

        Fragment fragment;
        fragment = new MapsFragment();
        loadFragment(fragment);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment;
            switch (menuItem.getItemId()) {
                case R.id.navigation_map:
                    getSupportActionBar().hide();
                    fragment = new MapsFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_favorite:
                    getSupportActionBar().show();
                    getSupportActionBar().setTitle("Favorite");
                    fragment = new FavoriteFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_review:
                    getSupportActionBar().show();
                    getSupportActionBar().setTitle("Review");
                    fragment = new TestFragment();
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
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
