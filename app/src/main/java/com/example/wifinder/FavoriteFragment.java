package com.example.wifinder;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifinder.data.model.Favorite;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FavoriteFragment extends Fragment {

    private FirebaseUser user;

    private List<Favorite> favorites;

    private OnFavoriteClickListener listener;

    private FavoriteRecyclerViewAdapter adapter;

    // お気に入り解除の時のコールバック
    private FavoriteRecyclerViewAdapter.OnFavoriteDeleteClickListener deleteClickListener = new FavoriteRecyclerViewAdapter.OnFavoriteDeleteClickListener() {
        @Override
        public void onDeleteClicked(int position) {
            // お気に入りデータから消す
            removeFavorite(favorites.get(position).spotId.toString(), position);
        }
    };

    FavoriteFragment(FirebaseUser user, List<Favorite> favorites) {
        this.user = user;
        this.favorites = favorites;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        // 未ログインの場合
        if (user == null) {
            // 未ログイン画面を表示
            view.findViewById(R.id.not_logged_in).setVisibility(View.VISIBLE);
            return view;
        } else {
            // 未ログイン画面を非表示
            view.findViewById(R.id.not_logged_in).setVisibility(View.GONE);
        }

        // 表示するお気に入りがない場合
        if (favorites == null || favorites.size() == 0) {
            // EmptyViewを表示
            view.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            return view;
        } else {
            // お気に入りリスト表示
            RecyclerView recyclerView = view.findViewById(R.id.list);
            if (recyclerView != null) {
                Context context = view.getContext();
                recyclerView.setHasFixedSize(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                // 区切り線追加
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                // データセット
                adapter = new FavoriteRecyclerViewAdapter(favorites, listener, deleteClickListener);
                recyclerView.setAdapter(adapter);
            }
            return view;
        }
    }

    private void removeFavorite(final String spotId, final int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference updateFavoriteRef = db.collection("favorite").document(user.getEmail())
                .collection("spotId").document(spotId);
        // TODO: インターネット接続の確認処理を入れる
        updateFavoriteRef.update("isFavorite", 0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // リストから行を削除
                        favorites.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, favorites.size());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("####", "Error updating document", e);
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFavoriteClickListener) {
            listener = (OnFavoriteClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFavoriteClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFavoriteClickListener {
        void onItemClicked();
    }
}
