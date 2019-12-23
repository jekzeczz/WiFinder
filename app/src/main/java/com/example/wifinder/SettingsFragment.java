package com.example.wifinder;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifinder.data.model.TestOpenHelper;
import com.example.wifinder.data.model.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener{
    private TestOpenHelper helper;
    private SQLiteDatabase db;
    private List<User> users = new ArrayList<>();

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

            case R.id.setting3:

                break;

            case R.id.setting4:
                readData();
                for(int i = 0; i < users.size(); i++) {
                    users.get(i).getUsername();
                }
                break;

        }
    }

    public void readData(){

        helper = new TestOpenHelper(getActivity());
        db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                "user",
                new String[] { "id", "name", "email"},
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            User n = new User( cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            users.add(n);

            //Log.d("#", "spotData" + n);
            cursor.moveToNext();
        }

        cursor.close();

        //Log.d("#", "spotData" + )

    }
}
