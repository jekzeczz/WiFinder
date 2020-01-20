package com.example.wifinder;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        // ログインしている場合のみプロフィールと線を表示
        if (FirebaseAuth.getInstance().getUid() != null) {
            TextView profileView = view.findViewById(R.id.profile_text_view);
            profileView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.profile_text_view_bottom_border).setVisibility(View.VISIBLE);
            // クリックリスナー設定
            profileView.setOnClickListener(this);
        }

        TextView textView2 = view.findViewById(R.id.setting2);
        textView2.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.profile_text_view:
                intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.setting2:
                DialogFragment newFragment = new SelectLanguageDialogFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "Select Languages");
                break;

            case R.id.setting3:

                break;

            case R.id.setting4:
                break;
        }
    }
}
