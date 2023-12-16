package com.example.familychat;
import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.model.SignalRManager;
import com.example.familychat.model.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.adapter.ChatAdapter;
import com.example.familychat.model.ChatMessage;
import com.microsoft.signalr.HubConnection;

import java.util.ArrayList;
import java.util.List;
public class ChatFragment extends Fragment {

    private EditText messageInput;
    private RecyclerView recyclerView;
    private ChatRooms chatRooms;


    // HubConnection reference to send messages
    private HubConnection hubConnection;


    public ChatFragment(ChatRooms chatRooms) {
        this.chatRooms = chatRooms;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        try {
            // Inflate the layout for this fragment

            messageInput = rootView.findViewById(R.id.chat_message_input);
            ImageButton sendMessageBtn = rootView.findViewById(R.id.message_send_btn);
            recyclerView = rootView.findViewById(R.id.recyler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            hubConnection = SignalRManager.getHubConnection();
            List<ChatMessage> messages = new ArrayList<>();


            sendMessageBtn.setOnClickListener((v -> {

                String message = messageInput.getText().toString().trim();
                if (message.isEmpty())
                    return;
                try {
                    ObjectMapper om = new ObjectMapper();

                    ChatMessage chatMessage = new ChatMessage(chatRooms.User, message);
                    chatMessage.IsUser = false;
                    message = om.writeValueAsString(chatMessage);
                    hubConnection.send("SendNotificationToAll", message);
                    chatMessage.IsUser = true;
                    onNewMessage(chatMessage);
                    messageInput.setText("");

                } catch (Exception ex) {
                    System.out.println(ex);
                }

            }));






            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);

            if (chatRooms.chatAdapter == null) {
                chatRooms.chatAdapter = new ChatAdapter(getContext(), messages);
            }
            recyclerView.setAdapter(chatRooms.chatAdapter);
            goTobottomOfChat();

        }catch (Exception ex){
            System.out.println(ex);
        }
        return rootView;
    }

    void goTobottomOfChat(){

        if(recyclerView!=null && chatRooms.chatAdapter!=null) {
            int itemCount = chatRooms.chatAdapter.getItemCount();
            recyclerView.smoothScrollToPosition(itemCount - 1);
        }
    }
    public void onNewMessage(ChatMessage message) {
        // Handle the new message received from SignalR
        addMessagesFromSignalR(message);
        goTobottomOfChat();
    }

    private void addMessagesFromSignalR(ChatMessage msg) {
        chatRooms.chatAdapter.addMessage(msg);
        chatRooms.chatAdapter.notifyDataSetChanged();
    }
}

