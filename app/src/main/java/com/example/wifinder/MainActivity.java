package com.example.wifinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            navigateToHomeActivity();
            Toast.makeText(this, "ログインされています。", Toast.LENGTH_SHORT).show();
            return;
        }

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
