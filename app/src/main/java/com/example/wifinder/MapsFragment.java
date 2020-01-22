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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.wifinder.data.SpotsAdapter;
import com.example.wifinder.data.model.RatingResult;
import com.example.wifinder.data.model.Spots;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final int MAP_DEFAULT_ZOOM_LEVEL = 15;
    private static final int MAP_MOVE_SPEED = 500;

    private FusedLocationProviderClient fusedLocationClient;

    private Location currentLocation;

    private GoogleMap mMap;

    private List<Spots> spotsList;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private FrameLayout containerView;

    private ProgressBar progressBar;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        // 最新位置取得
        getLastLocation();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // マップ表示
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // 親View
        containerView = rootView.findViewById(R.id.custom_view_container);
        progressBar = rootView.findViewById(R.id.progress);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings us = mMap.getUiSettings();
        us.setZoomControlsEnabled(true);
        us.setMapToolbarEnabled(false);

        // 最初出るマップの位置とzoomを指定
        mMap.moveCamera(CameraUpdateFactory.zoomTo(MAP_DEFAULT_ZOOM_LEVEL));
        if (currentLocation == null) {
            // デフォルト値を日本電子専門学校にする
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(35.6988277, 139.696522)));
        } else {
            // デバイスの位置情報があった場合はそれをデフォルト値にする
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        }

        try {
            GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.geojson, getActivity());
            layer.addLayerToMap();
            initLoadDB();
            for (int i = 0; i < spotsList.size(); i++) {
                LatLng place = new LatLng(spotsList.get(i).getLatitude(), spotsList.get(i).getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(place);
                Marker marker = mMap.addMarker(markerOptions);
                // spot データ保存
                marker.setTag(spotsList.get(i));
            }
        } catch (IOException | JSONException e) {
            Log.e("####", "### exception! " + e);
            e.printStackTrace();
        }

        // マーカーをクリックしたら店情報が出るように。
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // spotデータを取得できる
                Spots spot = (Spots) marker.getTag();
                if (spot != null) {
                    // spotに評価平均値をセットする
                    setRatingSum(spot);
                } else {
                    Toast.makeText(getContext(), "情報の取得に失敗しました。", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        locationStart();
    }

    @Override
    public void onLocationChanged(Location location) {
        // 現在位置を取得できたのでProgressBarを非表示
        progressBar.setVisibility(View.GONE);
        // 緯度
        double lat = location.getLatitude();
        // 経度
        double lng = location.getLongitude();
        Log.e("#########", "緯度 Latitude:" + lat);
        Log.e("#############", "経度 Longitude:" + lng);

        // 現在位置表示
        mMap.setMyLocationEnabled(true);
        // 現在の位置まで移動
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)), MAP_MOVE_SPEED, null);
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
        // 位置取得権限を確認する
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // LocationManager インスタンス生成
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            if (locationManager == null || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // GPSがOFFになっているため、設定するように促す
                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(settingsIntent);
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, HomeActivity.REQUEST_PERMISSION, 50, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, HomeActivity.REQUEST_PERMISSION, 50, this);
        } else {
            // 許可されてない場合
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, HomeActivity.REQUEST_PERMISSION);
        }
    }

    private void initLoadDB() {
        SpotsAdapter mDbHelper = new SpotsAdapter(getActivity());
        mDbHelper.createDatabase();
        mDbHelper.open();

        // DB内の値をmodelを適用し、入れる
        spotsList = mDbHelper.getTableData();

        // db close
        mDbHelper.close();
    }

    private void setRatingSum(final Spots spot) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference sumDocRef = db.collection("ratingSpot").document(spot.id.toString());
        sumDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (task.isSuccessful() && document != null) {
                    // 平均値をセットするために、まず評価（Rating）値をとってくる
                    RatingResult ratingResult = document.toObject(RatingResult.class);
                    if (document.exists() && ratingResult.getSumRating() > 0) {
                        Log.e("#####", "DocumentSnapshot data: " + document.getData());
                        // 平均を計算
                        int sumRating = ratingResult.getSumRating();
                        int numRating = ratingResult.getNumRating();
                        float avgRating = sumRating / numRating;
                        Log.e("@@@@@@", "@@@@@@@@ 評価されている平均: " + avgRating);
                        // ビュー描画
                        addCustomView(spot, avgRating);
                    } else {
                        // 評価がないスポットの場合ここにくる
                        // その場合、平均を 0.0に（初期化）して表示する
                        Log.e("@@@@@@", "@@@@@@@@ 評価されてない.平均の初期化: " + task.getException());
                        // ビュー描画
                        addCustomView(spot, 0.0F);
                    }
                }
            }
        });
    }

    private void addCustomView(Spots spot, float avgRating) {
        if (getContext() != null) {
            // TODO: ビューを消す処理も入れとく必要がある containerView.removeAllViews() 的に。
            final CustomView customView = new CustomView(getContext());
            customView.setSpot(spot);
            customView.setUser(user);
            customView.setRatingBar(avgRating);
            customView.setOnUpdateViewListener(new CustomView.OnUpdateViewListener() {
                @Override
                public void onUpdate(Spots spot, float avgRating) {
                    // spot情報レイアウトを全部消して
                    containerView.removeAllViewsInLayout();
                    // 新しく追加する
                    addCustomView(spot, avgRating);
                }
            });
            containerView.addView(customView);
        } else {
            Toast.makeText(getContext(), "データがありません。", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLastLocation() {
        // 最新の位置情報を取得
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        /*　以下の場合は location が　null になる
                        1. デバイスの設定で位置情報がオフになっている
                        2. 新しいデバイスまたは工場出荷時の設定に戻されたデバイス
                        3. デバイス上の Google Play 開発者サービスが再起動され、サービスの再起動後に位置情報を取得していなかった
                         */
                        currentLocation = location;
                    }
                });
    }

    public void onSpotLocation(int spotId) {
        for (int i = 0; i < spotsList.size(); i++) {
            if(spotId == spotsList.get(i).getId()) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(spotsList.get(i).getLatitude(), spotsList.get(i).getLongitude())));
            } else {
            }
        }
    }

}