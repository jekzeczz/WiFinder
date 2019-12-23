package com.example.wifinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.wifinder.data.model.TestOpenHelper;
import com.example.wifinder.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TestOpenHelper helper;
    private SQLiteDatabase db;
    private List<User> users = new ArrayList<>();
    //TextView textViewId, textViewUsername, textViewEmail;
    TextView textViewUsername, textViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();


    }

    void init() {
        //textViewId = findViewById(R.id.textViewId);
        //textViewUsername = findViewById(R.id.textViewUsername);
        //textViewEmail = findViewById(R.id.textViewEmail);

        //getting the current userFLAG_ACTIVITY_NEW_TASK
        User user = PrefManager.getInstance(this).getUser();

        //setting the values to the textviews
        //textViewId.setText(String.valueOf(user.getId()));
        //textViewUsername.setText(user.getUsername());
        //textViewEmail.setText(user.getEmail());

        //when the user presses logout button calling the logout method
        /*
        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                PrefManager.getInstance(getApplicationContext()).logout();
                //PrefManager.getInstance(ProfileActivity.this).logout();
            }
        });*/
        Log.d("#", "UserData " + user);
        readData();
    }

    public void readData(){

        helper = new TestOpenHelper(getApplicationContext());
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
        for(int i = 0; i < users.size(); i++) {
            users.get(i).getUsername();
            Log.d("#", "UserData " + users.get(i).getUsername());
        }

    }
}
