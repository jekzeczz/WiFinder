package com.example.wifinder;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.wifinder.data.model.Favorite;
import com.example.wifinder.data.model.Rating;
import com.example.wifinder.data.model.RatingResult;
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

import static com.example.wifinder.MainActivity.queren;

public class CustomView extends FrameLayout {

    private Spots spot;
    private TextView nameView;
    private TextView addressView;
    private RatingBar ratingBar;
    private ImageButton we;
    private float ratingValue = 0.0F;
    final private Integer FAV_YES = 1;
    final private Integer FAV_NO = 0;
    public Integer dbSpotId;
    public Integer dbIsFavorite;
    public  mSurfaceView m;
    private Integer spotId;
    private String spotName;
    private String spotAddress;

    private Integer sumRating = 0;
    private Integer numRating = 0;

    private FirebaseUser user;

    private OnUpdateViewListener onUpdateViewListener;

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
        ratingBar = findViewById(R.id.rating_bar);

        // TODO: 一回評価したユーザーはボタンを表示しないようにするとか？
        ImageButton reviewButton = view.findViewById(R.id.review_button);
        reviewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 星で評価するダイアログを出す
                // ログインしている場合
                if (user != null) {
                    getRatingSpot(context);
                } else {
                    //　ログインしていない場合
                    Toast.makeText(context, "ログインしてください", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // お気に入りボタン
        ImageButton favoriteButton = view.findViewById(R.id.favorite_button);
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
                if (ratingValue != 0) {
                    addRatingSpot(ratingValue);
                } else {
                    Toast.makeText(context, "1点以上入れて下さい", Toast.LENGTH_LONG).show();
                }
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

    public void getRatingSpot(final Context context) {
        // クリックしたマーカーの情報を保存
        spotId = spot.id;
        spotName = spot.name;
        spotAddress = spot.address;

        // 1. Firebaseのデータベース取得
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // 2. ratingSpotテーブルを取得
        DocumentReference ratingDocRef = db.collection("ratingSpot").document(spotId.toString()).collection("users").document(user.getEmail());
        ratingDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e("#####", "DocumentSnapshot data: " + document.getData());
                        Toast.makeText(getContext(), "すでに評価済みです", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("#####", "No such document");
                        showDialog(context);
                    }
                } else {
                    Log.e("#####", "get failed with ", task.getException());
                }
            }
        });
    }

    public void addRatingSpot(float ratingValue) {
        // クリックしたマーカーの情報を保存
        spotId = spot.id;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference addRatingDocRef = db.collection("ratingSpot").document(spotId.toString())
                .collection("users").document(user.getEmail());

        Rating rating = new Rating();
        rating.setRating(ratingValue);

        addRatingDocRef.set(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "評価追加", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed. Check log.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getRatingSum(spotId, ratingValue);
    }

    public void addRatingSum(Integer sumRating, Integer numRating, final float ratingValue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference addRatingSumDocRef = db.collection("ratingSpot").document(spotId.toString());
        final RatingResult ratingResult = new RatingResult();

        if (sumRating == 0 && numRating == 0) {
            ratingResult.setSumRating((int) ratingValue);
            ratingResult.setNumRating(1);
        } else {
            ratingResult.setSumRating(sumRating + (int) ratingValue);
            ratingResult.setNumRating(numRating + 1);
        }

        addRatingSumDocRef.set(ratingResult).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "評価合計追加", Toast.LENGTH_SHORT).show();
                    if (onUpdateViewListener != null) {
                        // 評価処理が終わったタイミングでレイアウトをアップデートさせる
                        onUpdateViewListener.onUpdate(spot, ratingValue);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed. Check log.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getRatingSum(Integer spotId, final float ratingValue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference sumDocRef = db.collection("ratingSpot").document(spotId.toString());

        sumDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    RatingResult ratingResult = document.toObject(RatingResult.class);

                    if (document.exists() && ratingResult.getSumRating() >= 0) {
                        Log.e("#####", "DocumentSnapshot data: " + document.getData());

                        sumRating = ratingResult.getSumRating();
                        numRating = ratingResult.getNumRating();

                        addRatingSum(sumRating, numRating, ratingValue);
                    } else {
                        Log.e("#####", "No such document");

                        sumRating = 0;
                        numRating = 0;

                        addRatingSum(sumRating, numRating, ratingValue);
                    }
                } else {
                    Log.e("######", "Error getting documents: ", task.getException());
                }
            }
        });
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

    public void setRatingBar(float rating) {
        ratingBar.setRating(rating);
        invalidate();
        requestLayout();
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public FirebaseUser getUser() {
        return this.user;
    }

    public void setOnUpdateViewListener(OnUpdateViewListener updateViewListener) {
        this.onUpdateViewListener = updateViewListener;
    }

    public interface OnUpdateViewListener {
        void onUpdate(Spots spot, float avgRating);
    }

}
