package com.example.familychat;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.model.SignalRManager;
import com.example.familychat.model.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    UserContext user;
    HubConnection hubConnection;
    private Map<String, ChatRooms> chatRoomsHashMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);

        SignalRManager.initialize(this);
        hubConnection = SignalRManager.getHubConnection();
        setupSignalR();


        searchButton.setOnClickListener((v)->{
            Toast.makeText(Home.this, "Search", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Home.this,ChatActivity.class);
            user = (UserContext) getIntent().getExtras().get("user");
            intent.putExtra("user", user);
            startActivity(intent);
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



    private void setupSignalR() {
        try {
            hubConnection.send("SaveUserConnection", user.UserId);
            hubConnection.send("NotifyOnConnectionIdUpdate", user.UserId);

            hubConnection.on("broadcastMessage", (message) -> {
                runOnUiThread(() -> {
                    try {
                        Toast.makeText(Home.this,message,Toast.LENGTH_SHORT).show();
                        // Notify ChatFragment about the new message
                        ObjectMapper om = new ObjectMapper();
                        ChatMessage msg = om.readValue(message, ChatMessage.class);
                        ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        if (chatFragment != null) {
                            chatFragment.onNewMessage(msg);
                        }
                    }catch (Exception e){
                        System.out.println(e);
                    }
                });
            }, String.class);


            hubConnection.on("ActiveUser", (message) -> {
                runOnUiThread(() -> {
                    try {
                        Toast.makeText(Home.this,message,Toast.LENGTH_SHORT).show();
                        ObjectMapper om = new ObjectMapper();
                        UserContext user = om.readValue(message, UserContext.class);
                        Toast.makeText(Home.this,message,Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        System.out.println(e);
                    }
                });
            }, String.class);
        } catch (Exception e) {
            System.out.println(e);
        }
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
