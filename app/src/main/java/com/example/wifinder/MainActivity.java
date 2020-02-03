package com.example.wifinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    static int queren = 0;
    private static final String SHARED_PREFERENCES_WITHOUT_LOGIN = "shared_preferences_start_without_login";
    private static final String WITHOUT_LOGIN_KEY = "without_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find_weather();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_WITHOUT_LOGIN, Context.MODE_PRIVATE);
        boolean isWithoutLogin = sharedPreferences.getBoolean(WITHOUT_LOGIN_KEY, false);
        // ログインしたことがある ||「ログインなしで始める」をタップしたことがある
        if (user != null || isWithoutLogin) {
            // アプリ起動際にログイン画面出るのはうざいのでHomeActivityに遷移させてこの画面をfinishする
            navigateToHomeActivity();
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
                // ログインなしで始めたことを保存しておく // TODO: ログアウトする時、 SHARED_PREFERENCES_WITHOUT_LOGIN は false か clean しておく必要がある
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(WITHOUT_LOGIN_KEY, true);
                editor.apply();

                navigateToHomeActivity();
            }
        });
    }

    public void find_weather() {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=tokyo&appid=14b4b01ba522e0b8843285568e76f618";
//        String url ="https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String description  = object.getString("description");
                    String city = response.getString("name");

                    System.out.println("------------------------------------------------v");

                    System.out.println(temp);


 //                   t2_city.setText(city);
  //                  t3_description.setText(description);
                    if(description.equals("rain")){
                        queren = 1;
                    }
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE=MM-dd");
                    String formatted_date = sdf.format(calendar.getTime());

//                    t4_data.setText(formatted_date);

                    double temp_int = Double.parseDouble(temp);
                    double centi = (temp_int - 32) /1.8000;
                    centi = Math.round(centi);
                    int i = (int)centi;
  //                  t1_temp.setText(String.valueOf(i));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(getApplication(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
