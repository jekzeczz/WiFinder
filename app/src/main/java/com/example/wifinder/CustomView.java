package com.example.wifinder;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifinder.data.model.Favorite;
import com.example.wifinder.data.model.Spots;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class CustomView extends FrameLayout {

    private Spots spot;
    private TextView nameView;
    private TextView addressView;

    private float ratingValue = 0.0F;
    final private Integer FAV_YES = 1;
    final private Integer FAV_NO = 0;
    public Integer dbSpotId;
    public Integer dbIsFavorite;

    private Integer spotId;
    private String spotName;
    private String spotAddress;

    private FirebaseUser user;

    public CustomView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        // TODO: 店情報のレイアウトをここで修正
        View view = inflate(context, R.layout.custom_view, this);
        nameView = findViewById(R.id.name_view);
        addressView = findViewById(R.id.address_view);

        // TODO: 一回評価したユーザーはボタンを表示しないようにするとか？
        Button reviewButton = view.findViewById(R.id.review_button);
        reviewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 星で評価するダイアログを出す
                showDialog(context);
            }
        });

        // お気に入りボタン
        Button favoriteButton = view.findViewById(R.id.favorite_button);
        favoriteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // ログインしている場合
                if (user != null) {
                    // お気に入り設定
                    getFavorite();
                } else {
                    //　ログインしていない場合
                    Toast.makeText(context, "ログインしてください", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showDialog(final Context context) {
        // dialog のレイアウト作成
        final View customView = inflate(context, R.layout.rating_layout, null);
        // dialog にレイアウトセット
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Wifiはいかがでしたか？");
        dialog.setView(customView);

        // 点数を取得して変数に入れておく
        RatingBar ratingBar = customView.findViewById(R.id.rating_bar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingValue = v;
            }
        });

        // 評価するボタンを押した時
        dialog.setPositiveButton("評価する", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO: ratingValue をDBに保存する
                Toast.makeText(context, ratingValue + "点", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void getFavorite() {
        // クリックしたマーカーの情報を保存
        spotId = spot.id;
        spotName = spot.name;
        spotAddress = spot.address;

        // 1. Firebaseのデータベース取得
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 2. ログインしているユーザーのお気に入りテーブルを取得
        CollectionReference favCollectionRef = db.collection("favorite").document(user.getEmail()).collection("spotId");
        // 3. spotIdにお気に入りを追加するというクエリ文を定義
        Query query = favCollectionRef.whereEqualTo("spotId", spotId).whereEqualTo("isFavorite", FAV_YES);
        // クエリ実行
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                // 3．のクエリが成功した場合
                if (task.isSuccessful()) {
                    // 4. クエリの結果を格納
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Favorite favorite = document.toObject(Favorite.class);
                        dbSpotId = favorite.getSpotId();
                        dbIsFavorite = favorite.getIsFavorite();
                    }

                    // 格納した結果、データがない場合はお気に入りデータを追加
                    if (task.getResult().size() == 0 || !dbSpotId.equals(spotId)) {
                        addFavorite(spotId, spotName, spotAddress);
                    } else {
                        // すでにスポットをお気に入りしていた場合
                        Toast.makeText(getContext(), "お気に入り削除", Toast.LENGTH_SHORT).show();
                        removeFavorite();
                    }
                }
                // 3．のクエリが失敗した場合
                else {
                    Log.e("######", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void addFavorite(Integer spotId, String spotName, String spotAddress) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newFavoriteRef = db.collection("favorite").document(user.getEmail())
                .collection("spotId").document(spotId.toString());

        Favorite favorite = new Favorite();
        favorite.setEmail(user.getEmail());
        favorite.setSpotId(spotId);
        favorite.setSpotName(spotName);
        favorite.setSpotAddress(spotAddress);
        favorite.setIsFavorite(FAV_YES);

        newFavoriteRef.set(favorite).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "お気に入り追加", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed. Check log.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void removeFavorite() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference updateFavoriteRef = db.collection("favorite").document(user.getEmail())
                .collection("spotId").document(spotId.toString());

        // TODO: インターネット接続の確認処理を入れる

        updateFavoriteRef.update("isFavorite", FAV_NO)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("####", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("####", "Error updating document", e);
                    }
                });
    }

    public void getRatingSpot() {
        // クリックしたマーカーの情報を保存
        spotId = spot.id;
        spotName = spot.name;
        spotAddress = spot.address;

        // 1. Firebaseのデータベース取得
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 2. ratingSpotテーブルを取得
        DocumentReference ratingDocRef = db.collection("ratingSpot").document(spotId.toString()).collection("users").document();
        ratingDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("#####", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("#####", "No such document");
                    }
                } else {
                    Log.d("#####", "get failed with ", task.getException());
                }
            }
        });

    }

    public void addRatingSpot() {
        // クリックしたマーカーの情報を保存
        spotId = spot.id;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ratingDocRef = db.collection("ratingSpot").document(spotId.toString())
                .collection("users").document();
    }

    public void setSpot(Spots spot) {
        this.spot = spot;

        nameView.setText(spot.getName());
        addressView.setText(spot.getAddress());
        invalidate();
        requestLayout();
    }

    public Spots getSpot() {
        return this.spot;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public FirebaseUser getUser() {
        return this.user;
    }
}
