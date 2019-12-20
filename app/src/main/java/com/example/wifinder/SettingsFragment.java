package com.example.wifinder;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener{


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        TextView textView1 = view.findViewById(R.id.setting1);
        TextView textView2 = view.findViewById(R.id.setting2);
        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.setting1:
                intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.setting2:
                DialogFragment newFragment = new SelectLanguageDialogFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "Select Languages");
                break;
        }
    }
}
