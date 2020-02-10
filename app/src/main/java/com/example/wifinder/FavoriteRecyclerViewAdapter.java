package com.example.wifinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifinder.FavoriteFragment.OnFavoriteClickListener;
import com.example.wifinder.data.model.Favorite;

import java.util.List;

public class FavoriteRecyclerViewAdapter extends RecyclerView.Adapter<FavoriteRecyclerViewAdapter.ViewHolder> {

    private final List<Favorite> favorites;
    private final OnFavoriteClickListener containerListener;
    private final OnFavoriteDeleteClickListener deleteClickListener;

    FavoriteRecyclerViewAdapter(List<Favorite> favorites, OnFavoriteClickListener listener, OnFavoriteDeleteClickListener deleteClickListener) {
        this.favorites = favorites;
        this.containerListener = listener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorite_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.favorite = favorites.get(position);
        holder.spotNameView.setText(favorites.get(position).spotName);
        holder.spotAddressView.setText(favorites.get(position).spotAddress);
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClicked(position);
                }
            }
        });
        holder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerListener != null) {
                    containerListener.onItemClicked(favorites.get(position).spotId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View containerView;
        TextView spotNameView;
        TextView spotAddressView;
        ImageButton favoriteButton;
        Favorite favorite;

        ViewHolder(View view) {
            super(view);
            containerView = view;
            spotNameView = view.findViewById(R.id.spot_name);
            spotAddressView = view.findViewById(R.id.spot_address);
            favoriteButton = view.findViewById(R.id.favorite_button);
        }
    }

    public interface OnFavoriteDeleteClickListener {
        void onDeleteClicked(int position);
    }
}
