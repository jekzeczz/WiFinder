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

import com.example.wifinder.data.model.Favorite;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements FavoriteFragment.OnFavoriteClickListener {

    public static final int REQUEST_PERMISSION = 1000;

    private Fragment mapFragment;

    private Fragment favoriteFragment;

    private FirebaseUser user;

    private List<Favorite> favorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // permission チェック
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 許可されてないので許可を求める
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
        } else {
            // 許可されている場合
            user = FirebaseAuth.getInstance().getCurrentUser();
            initMapFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 許可された
            user = FirebaseAuth.getInstance().getCurrentUser();
            initMapFragment();
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
                    if (mapFragment == null) {
                        fragment = new MapsFragment();
                    } else {
                        fragment = mapFragment;
                    }
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_favorite:
                    checkLogin();
                    return true;

                case R.id.navigation_settings:
                    fragment = new SettingsFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void initMapFragment() {
        // マップ表示
        if (mapFragment == null) {
            mapFragment = new MapsFragment();
        }
        loadFragment(mapFragment);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    private void checkLogin() {
        // 未ログイン
        if (user == null) {
            favoriteFragment = new FavoriteFragment(null, favorites);
            loadFragment(favoriteFragment);
        } else {
            // お気に入り取得
            getFavorite();
        }
    }

    public void getFavorite() {
        // もし email のないユーザーだった場合は処理を実行させない
        if (user.getEmail() == null) {
            Log.e("#####", "user email is null!!");
            return;
        }
        // FirebaseFirestoreからユーザーのお気に入り情報を取得
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("favorite").document(user.getEmail())
                .collection("spotId")
                .whereEqualTo("isFavorite", 1) // お気に入りされているデータで絞る
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots != null) {
                            // お気に入り画面を開く
                            favorites = documentSnapshots.toObjects(Favorite.class);
                            favoriteFragment = new FavoriteFragment(user, favorites);
                            loadFragment(favoriteFragment);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("#####", "onFailure " + e.getMessage());
                        // TODO: FavoriteFragment に遷移しないので、何か失敗時の画面用意
                    }
                });
    }

    @Override
    public void onItemClicked(int spotId) {
        // お気に入りリストからの遷移になるので、区別できるように Key, Value をセット
        MapsFragment mapFragment = new MapsFragment();
        Bundle bundle = new Bundle();
        // Key, Valueセット
        bundle.putBoolean("is_clicked_favorite", true);
        bundle.putInt("clicked_favorite_id", spotId);
        mapFragment.setArguments(bundle);
        loadFragment(mapFragment);
    }
}
