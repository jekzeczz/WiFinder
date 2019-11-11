package com.example.wifinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class DBLorderActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editTextKey, editTextValue;
    private TestOpenHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dblorder);
    }
}
