package com.example.familychat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class Home extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;

    //ChatFragment chatFragment;
    //ProfileFragment profileFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);

        searchButton.setOnClickListener((v)->{
            Toast.makeText(Home.this, "Search", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(MainActivity.this,SearchUserActivity.class));
        });
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_chat){
                    Toast.makeText(Home.this, "Chat", Toast.LENGTH_SHORT).show();
                    //getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,chatFragment).commit();
                }
                if(item.getItemId()==R.id.menu_profile){
                    Toast.makeText(Home.this, "Profile", Toast.LENGTH_SHORT).show();
                    //getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,profileFragment).commit();
                }
                return true;
            }
        });
    }
    void getConnectionList(){
        /*try {
            String baseUrl = "http://familychat.somee.com/FamilyChat/GetAllConnectionByUserId?id=1";

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                message = response.getString("message");
                                token = response.getString("token");
                                Toast.makeText(LogIn.this,  message, Toast.LENGTH_SHORT).show();
                                Intent home = new Intent(LogIn.this, Home.class);
                                startActivity(home);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        Toast.makeText(LogIn.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                        setInProgress(false);
                                    }
                                });
                            }
                        });
                    queue.add(jsonObjectRequest);
            }catch (Exception e){
                e.printStackTrace();
        }*/
    }
    void loadChats(){

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
        finish();
    }
}
