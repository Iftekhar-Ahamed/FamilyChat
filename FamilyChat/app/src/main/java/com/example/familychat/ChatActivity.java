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
            textView.setText(chatRooms.User.name);


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

            // Create a new instance of the ChatFragment

            chatRooms.chatFragment = new ChatFragment(chatRooms);


            // Pass any necessary data to the fragment using arguments
            /*Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            chatFragment.setArguments(bundle);*/

            // Begin the fragment transaction
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace the content of the container with the ChatFragment
            transaction.replace(R.id.fragment_container, chatRooms.chatFragment);

            // Commit the transaction
            transaction.commit();
        }catch (Exception ex){
            System.out.println(ex);
        }
    }


}
