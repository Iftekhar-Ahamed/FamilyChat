package com.example.familychat;
import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.model.MyInformation;
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


            recyclerView = rootView.findViewById(R.id.recyler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            hubConnection = SignalRManager.getHubConnection();
            List<ChatMessage> messages = new ArrayList<>();





            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);

            if (chatRooms.chatAdapter == null) {
                chatRooms.chatAdapter = new ChatAdapter(getContext(), messages);
            }
            recyclerView.setAdapter(chatRooms.chatAdapter);


        }catch (Exception ex){
            System.out.println(ex);
        }
        return rootView;
    }
}

