package com.example.familychat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.adapter.ChatAdapter;
import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.utils.MyInformation;
import com.example.familychat.utils.SignalRManager;
import com.example.familychat.utils.ChatMessageEvent;
import com.example.familychat.utils.TrackingActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.signalr.HubConnection;

import org.greenrobot.eventbus.EventBus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
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
                    chatMessage.userName = MyInformation.data.userName;
                    chatMessage.chatId = chatRooms.chatId;
                    chatMessage.isUser = false;
                    chatMessage.userId = MyInformation.data.userId;
                    chatMessage.messageDateTime = LocalDateTime.now().toString();
                    message = om.writeValueAsString(chatMessage);
                    chatMessage.isUser = true;
                    if(SignalRManager.serviceRunning == true){
                        if(SignalRManager.SendMessageToClint(message)) {
                            postChatMessageEvent(chatMessage);
                        }else {
                            Toast.makeText(this,"Connection Is Not Acitve",Toast.LENGTH_SHORT).show();
                        }
                        messageInput.setText("");
                    }else {
                        Toast.makeText(this,"You are in Offline",Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                    Toast.makeText(this,ex.toString(),Toast.LENGTH_SHORT).show();
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TrackingActivity.trackingActivity.setChatId(0);
    }
}
