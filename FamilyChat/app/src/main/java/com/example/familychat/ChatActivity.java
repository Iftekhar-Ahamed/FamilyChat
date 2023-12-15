package com.example.familychat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.model.SignalRManager;
import com.example.familychat.model.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    HubConnection hubConnection = SignalRManager.getHubConnection();
    private Map<String, ChatRooms> chatRoomsHashMap = new HashMap<>();
    private UserContext user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (UserContext) getIntent().getExtras().get("user");
        setContentView(R.layout.activity_chat);


        ImageButton backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });

        showChatFragment(user.UserId.toString());
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


}
