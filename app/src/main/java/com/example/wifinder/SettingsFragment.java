package com.example.wifinder;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // ログインしている場合のみプロフィールとログアウト、線を表示
        if (FirebaseAuth.getInstance().getUid() != null) {
            TextView setAccountView = view.findViewById(R.id.set_account_text_view);
            TextView setAccountDescriptionView = view.findViewById(R.id.set_account_description_view);
            setAccountView.setVisibility(View.VISIBLE);
            setAccountDescriptionView.setVisibility(View.VISIBLE);

            // クリックリスナー設定
            setAccountView.setOnClickListener(this);
        }
        else {
            TextView loginView = view.findViewById(R.id.login_text_view);
            TextView loginDescriptionView = view.findViewById(R.id.login_description_view);
            loginView.setVisibility(View.VISIBLE);
            loginDescriptionView.setVisibility(View.VISIBLE);

            loginView.setOnClickListener(this);
        }

        TextView setLanguageView = view.findViewById(R.id.set_language_text_view);
        setLanguageView.setOnClickListener(this);

        /* TODO: 使わなかったら場合消す */
        Button refreshButton = view.findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "更新ボタンです", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
           case R.id.set_language_text_view:
                DialogFragment newFragment = new SelectLanguageDialogFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "Select Language");
                break;

            case R.id.set_account_text_view:
                intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.login_text_view:
                intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}
