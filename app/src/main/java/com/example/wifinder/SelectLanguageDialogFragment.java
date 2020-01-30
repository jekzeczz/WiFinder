package com.example.wifinder;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectLanguageDialogFragment extends DialogFragment {


    public SelectLanguageDialogFragment() {
        // Required empty public constructor
    }

/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_language_dialog, container, false);
    }
*/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.res_languages)
                .setItems(R.array.languages_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                setLocale("en");
                                break;

                            case 1:
                                setLocale("ja");
                                break;

                            case 2:
                                setLocale("ko");
                                break;
                            case 3:
                                setLocale("zh");
                                break;
                        }
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                });
        return builder.create();
    }

    void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, null);
    }
}
