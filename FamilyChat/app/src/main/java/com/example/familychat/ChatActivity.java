package com.example.familychat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.model.MyInformation;
import com.example.familychat.model.SignalRManager;
import com.example.familychat.model.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    HubConnection hubConnection = SignalRManager.getHubConnection();
    private  Integer chatId;
    private  ChatFragment chatFragment;
    private  ChatRooms chatRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_chat);
            chatId = (Integer) getIntent().getExtras().get("chat");
            chatRooms = ChatManager.getChatRooms(chatId);
            ImageButton backBtn = findViewById(R.id.back_btn);

            TextView textView = findViewById(R.id.other_username);
            textView.setText(ChatManager.getChatRooms(chatId).UserFriend.userName);


            backBtn.setOnClickListener((v) -> {
                onBackPressed();
            });

            loadChatFragment();



        }catch (Exception ex){
            System.out.println(ex);
        }
    }

    private void loadChatFragment() {
        try {
            if(chatRooms.chatFragment == null) {
                chatRooms.chatFragment = new ChatFragment(chatRooms);
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, chatRooms.chatFragment);
            transaction.commit();
        }catch (Exception ex){
            System.out.println(ex);
        }
    }


}
