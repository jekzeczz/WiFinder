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

public class HomeActivity extends AppCompatActivity implements FavoriteFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        BottomNavigationView navView = findViewById(R.id.nav_view);

        Fragment fragment;
        fragment = new MapsFragment();
        loadFragment(fragment);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_favorite, R.id.navigation_review, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
*/
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment;
            switch (menuItem.getItemId()) {
                case R.id.navigation_map:
                    //Toast.makeText(getApplicationContext(), "map", Toast.LENGTH_SHORT).show();
                    getSupportActionBar().hide();
                    fragment = new MapsFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_favorite:
                    //Toast.makeText(getApplicationContext(), "favorite", Toast.LENGTH_SHORT).show();
                    getSupportActionBar().show();
                    getSupportActionBar().setTitle("Favorite");
                    fragment = new FavoriteFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_review:
                    //Toast.makeText(getApplicationContext(), "review", Toast.LENGTH_SHORT).show();
                    getSupportActionBar().show();
                    getSupportActionBar().setTitle("Review");
                    fragment = new TestFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_settings:
                    //Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_SHORT).show();
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
