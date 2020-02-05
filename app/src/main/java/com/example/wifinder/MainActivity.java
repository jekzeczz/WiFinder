package com.example.wifinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String SHARED_PREFERENCES_WITHOUT_LOGIN = "shared_preferences_start_without_login";
    private static final String WITHOUT_LOGIN_KEY = "without_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_WITHOUT_LOGIN, Context.MODE_PRIVATE);
        boolean isWithoutLogin = sharedPreferences.getBoolean(WITHOUT_LOGIN_KEY, false);
//        // ログインしたことがある ||「ログインなしで始める」をタップしたことがある
//        if (user != null || isWithoutLogin) {
//            // アプリ起動際にログイン画面出るのはうざいのでHomeActivityに遷移させてこの画面をfinishする
//            navigateToHomeActivity();
//            return;
//        }

        //Login画面に遷移する
        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);
            }
        });

        //SignUp画面に遷移する
        Button signUpButton = findViewById(R.id.signUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SignUpActivity.class);
                startActivity(intent);
            }
        });

        //LoginせずにMap画面に遷移する
        TextView noLoginText = findViewById(R.id.noLogin);
        noLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ログインなしで始めたことを保存しておく // TODO: ログアウトする時、 SHARED_PREFERENCES_WITHOUT_LOGIN は false か clean しておく必要がある
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(WITHOUT_LOGIN_KEY, true);
                editor.apply();

                navigateToHomeActivity();
            }
        });
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(getApplication(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
