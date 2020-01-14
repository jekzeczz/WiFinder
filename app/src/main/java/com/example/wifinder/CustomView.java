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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

    private FirebaseUser user;
    private DocumentSnapshot mLastQueriedDocument;

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
                // TODO: ログイン/非ログインユーザーを判断し、格データベースにデータをInsert
                if (user != null) {
                    // User is signed in
                    Log.e("#######", "Login User");
                    Log.e("#######", user.getUid());
                    Integer spotId = spot.id;
                    String spotName = spot.name;
                    String spotAddress = spot.address;
                    /*
                    if() {
                        createNewFavorite(spotId, spotName, spotAddress);
                    }*/

                } else {
                    // No user is signed in
                    Log.e("#######", "No Login User");
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

    // Firebaseにお気に入りデータをInsert
    public void createNewFavorite(Integer spotId, String spotName, String spotAddress) {
        String email = user.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newFavoriteRef = db.collection("favorite").document(email)
                .collection("spotId").document(spotId.toString());

        Favorite favorite = new Favorite();
        favorite.setEmail(email);
        favorite.setSpotId(spotId);
        favorite.setSpotName(spotName);
        favorite.setSpotAddress(spotAddress);
        favorite.setIsFavorite(FAV_YES);

        newFavoriteRef.set(favorite).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Inserted new Favorite", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed. Check log.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getFavorite(String spotId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference favCollectionRef = db.collection("favorite");
        Query favoriteQuery = null;
        if(mLastQueriedDocument != null) {
            //favoriteQuery = favCollectionRef.whereEqualTo("spotId", spotId)
        }
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
