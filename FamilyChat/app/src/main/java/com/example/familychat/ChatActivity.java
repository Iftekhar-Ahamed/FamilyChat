package com.example.familychat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.adapter.ChatAdapter;
import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.model.MyInformation;
import com.example.familychat.model.SignalRManager;
import com.example.familychat.model.UserContext;
import com.example.familychat.utils.ChatMessageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    HubConnection hubConnection = SignalRManager.getHubConnection();
    private  Integer chatId;
    private  ChatRooms chatRooms;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.activity_chat);
            chatId = (Integer) getIntent().getExtras().get("chat");
            chatRooms = ChatManager.getChatRooms(chatId);
            ImageButton backBtn = findViewById(R.id.back_btn);
            recyclerView = findViewById(R.id.chat_recycler_view);
            ImageButton sendMessageBtn = findViewById(R.id.message_send_btn);
            EditText messageInput = findViewById(R.id.chat_message_input);
            TextView textView = findViewById(R.id.other_username);
            textView.setText(ChatManager.getChatRooms(chatId).UserFriend.userName);



            List<ChatMessage> messages = new ArrayList<>();
            if(chatRooms.chatAdapter==null){
                chatRooms.chatAdapter = new ChatAdapter(this,messages);
            }

            recyclerView.setAdapter(chatRooms.chatAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            chatRooms.chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    recyclerView.smoothScrollToPosition(positionStart+itemCount);
                }
            });



            sendMessageBtn.setOnClickListener((v -> {

                String message = messageInput.getText().toString().trim();
                if (message.isEmpty())
                    return;
                try {
                    ObjectMapper om = new ObjectMapper();

                    ChatMessage chatMessage = new ChatMessage(ChatManager.getChatRooms(chatRooms.chatId).UserFriend, message);
                    chatMessage.chatId = chatRooms.chatId;
                    chatMessage.isUser = false;
                    message = om.writeValueAsString(chatMessage);
                    hubConnection.send("SendNotificationToClient", message);
                    chatMessage.isUser = true;
                    postChatMessageEvent(chatMessage);
                    messageInput.setText("");
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }));
            goTobottomOfChat();

            backBtn.setOnClickListener((v) -> {
                onBackPressed();
            });
        }catch (Exception ex){
            System.out.println(ex);
        }
    }
    private static void postChatMessageEvent(ChatMessage chatMessage) {
        try {
            EventBus.getDefault().post(new ChatMessageEvent(chatMessage));
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
    void goTobottomOfChat(){

        if(recyclerView!=null && chatRooms.chatAdapter!=null) {
            int itemCount = chatRooms.chatAdapter.getItemCount();
            recyclerView.smoothScrollToPosition(itemCount - 1);
        }
    }

}
