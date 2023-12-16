package com.example.familychat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.model.SignalRManager;
import com.example.familychat.model.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.microsoft.signalr.HubConnection;

public class Home extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    UserContext user;
    HubConnection hubConnection;
    ChatRooms chatRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            user = (UserContext) getIntent().getExtras().get("user");
            setContentView(R.layout.activity_home);

            bottomNavigationView = findViewById(R.id.bottom_navigation);
            searchButton = findViewById(R.id.main_search_btn);

            SignalRManager.initialize(this);
            hubConnection = SignalRManager.getHubConnection();
            setupSignalR();


            ChatManager.addChatRooms(1, new ChatRooms(user, user));
            chatRooms = ChatManager.getChatRooms(1);

            searchButton.setOnClickListener((v) -> {
                Toast.makeText(Home.this, "Search", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Home.this, ChatActivity.class);
                intent.putExtra("chat", 1);
                startActivity(intent);
            });


            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.menu_chat) {
                        Toast.makeText(Home.this, "Chat", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Home.this, ChatActivity.class);
                        intent.putExtra("chat", 2);
                        startActivity(intent);
                    }
                    if (item.getItemId() == R.id.menu_profile) {
                        Toast.makeText(Home.this, "Profile", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Home.this, ChatActivity.class);
                        intent.putExtra("chat", 3);
                        startActivity(intent);
                    }
                    return true;
                }
            });
        }catch (Exception e){
            System.out.println(e);
        }
    }



    private void setupSignalR() {
        try {
            hubConnection.send("SaveUserConnection", user.UserId);
            hubConnection.send("NotifyOnConnectionIdUpdate", user.UserId);
            hubConnection.on("broadcastMessage", (message) -> {
                runOnUiThread(() -> {
                    try {
                        Toast.makeText(Home.this,message,Toast.LENGTH_SHORT).show();
                        ObjectMapper om = new ObjectMapper();
                        ChatMessage msg = om.readValue(message, ChatMessage.class);
                        if(chatRooms.chatFragment!=null){
                            chatRooms.chatFragment.onNewMessage(msg);
                        }
                    }catch (Exception e){
                        Toast.makeText(Home.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }, String.class);

            hubConnection.on("ActiveUser", (message) -> {
                runOnUiThread(() -> {
                    try {
                        ObjectMapper om = new ObjectMapper();
                        UserContext user = om.readValue(message, UserContext.class);
                        Toast.makeText(Home.this,message,Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Toast.makeText(Home.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }, String.class);
        } catch (Exception e) {
            Toast.makeText(Home.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
        finish();
    }
}
