package com.example.wifinder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifinder.data.model.Spots;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomView extends FrameLayout {

    public Spots spot;
    public TextView nameView;
    public TextView addressView;

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

        Button reviewButton = view.findViewById(R.id.review_button);
        reviewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Dialogを出す
                Toast.makeText(context, getSpot().id.toString(), Toast.LENGTH_SHORT).show();
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
}
