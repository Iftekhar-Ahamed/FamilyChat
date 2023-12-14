package com.example.familychat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    HubConnection hubConnection;
    private Map<String, ChatRooms> chatRoomsHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ImageButton backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });

        // Setup SignalR
        setupSignalR();
        showChatFragment("123");

    }
    private void showChatFragment(String userId) {
        ChatRooms chatRooms = getChatFragment(userId);
        if (chatRooms == null) {
            chatRooms = new ChatRooms();
            chatRooms.chatFragment = new ChatFragment(hubConnection);
            chatRoomsHashMap.put(userId, chatRooms);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chatRooms.chatFragment)
                .commit();
    }
    private ChatRooms getChatFragment(String userId) {
        return chatRoomsHashMap.get(userId);
    }
    private void setupSignalR() {
        String url = "http://familychat.somee.com/notificationHub";

        try {
            hubConnection = HubConnectionBuilder
                    .create(url)
                    .build();

            hubConnection.on("broadcastMessage", (message) -> {
                // Handle the received message
                runOnUiThread(() -> {
                    try {
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
            new ChatActivity.HubConnectionTask().execute(hubConnection);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    class HubConnectionTask extends AsyncTask<HubConnection, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(HubConnection... hubConnections) {
            HubConnection hubConnection = hubConnections[0];
            hubConnection.start().blockingAwait();
            return null;
        }
    }
}
