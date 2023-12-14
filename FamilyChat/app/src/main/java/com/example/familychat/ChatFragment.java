package com.example.familychat;
import com.example.familychat.model.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private ChatAdapter adapter;
    private EditText messageInput;
    private RecyclerView recyclerView;
    private UserContext user = new UserContext();

    // HubConnection reference to send messages
    private HubConnection hubConnection;


    public ChatFragment(HubConnection hubConnection) {
        this.hubConnection = hubConnection;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        messageInput = rootView.findViewById(R.id.chat_message_input);
        ImageButton sendMessageBtn = rootView.findViewById(R.id.message_send_btn);
        recyclerView = rootView.findViewById(R.id.recyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        sendMessageBtn.setOnClickListener((v -> {

                String message = messageInput.getText().toString().trim();
                if (message.isEmpty())
                    return;
                try {
                    ObjectMapper om = new ObjectMapper();

                    ChatMessage chatMessage = new ChatMessage(this.user,message);
                    message = om.writeValueAsString(chatMessage);
                    hubConnection.send("SendNotificationToAll", message);
                    ChatMessage msg = om.readValue(message, ChatMessage.class);
                    msg.IsUser = true;
                    addMessagesFromSignalR(msg);
                    int itemCount = adapter.getItemCount();
                    recyclerView.smoothScrollToPosition(itemCount - 1);
                    messageInput.setText("");
                }catch (Exception ex){
                    System.out.println(ex);
                }

        }));

        List<ChatMessage> messages = new ArrayList<>();

        // Add messages from previous SignalR responses
        // Add more messages as needed

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ChatAdapter(getContext(), messages);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    public void onNewMessage(ChatMessage message) {
        // Handle the new message received from SignalR
        addMessagesFromSignalR(message);
        int itemCount = adapter.getItemCount();
        recyclerView.smoothScrollToPosition(itemCount - 1);
    }

    private void addMessagesFromSignalR(ChatMessage msg) {
        adapter.addMessage(msg);
    }
}

