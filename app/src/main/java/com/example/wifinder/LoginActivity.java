package com.example.wifinder;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONException;

import com.example.wifinder.data.URLS;
import com.example.wifinder.data.model.User;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }
    void init(){
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        //if user presses on login calling the method login
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
        //if user presses on not registered
        /*
        findViewById(R.id.textViewRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register screen
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
         */
    }

    private void userLogin() {
        //値の取得してくる
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        //何も入力されていない場合
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter Email");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter password");
            editTextPassword.requestFocus();
            return;
        }
        //すべて入力OK
        UserLogin ul = new UserLogin(email,password);
        ul.execute();
    }

    class UserLogin extends AsyncTask<Void, Void, String> {
        ProgressBar progressBar;
        String email, password;
        UserLogin(String email,String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = findViewById(R.id.loading);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            try {
                //レスポンスをjsonオブジェクトに変換
                JSONObject obj = new JSONObject(s);
                Log.d("#", "JSON Object : " + obj);
                //レスポンスにエラーが無い場合
                if (!obj.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                    //レスポンスからuserをゲット
                    JSONObject userJson = obj.getJSONObject("user");

                    //userオブジェクトを生成
                    User user = new User(
                            userJson.getInt("id"),
                            userJson.getString("username"),
                            userJson.getString("email")
                    );

                    Log.d("##", "id : " + user.getId());
                    Log.d("##", "username : " + user.getUsername());
                    Log.d("##", "email : " + user.getEmail());

                    //storing the user in shared preferences
                    PrefManager.getInstance(getApplicationContext()).setUserLogin(user);
                    Log.d("##", "###");
                    //マップ画面に遷移
                    finish();
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            //creating request handler object
            RequestHandler requestHandler = new RequestHandler();

            //creating request parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("password", password);

            Log.d("#", "JSON Object1 : " + email);
            Log.d("#", "JSON Object2 : " + password);
            //returing the response
            return requestHandler.sendPostRequest(URLS.URL_LOGIN, params);
        }
    }
}
