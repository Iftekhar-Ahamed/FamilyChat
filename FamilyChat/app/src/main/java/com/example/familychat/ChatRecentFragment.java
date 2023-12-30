package com.example.familychat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.ChatActivity;
import com.example.familychat.R;
import com.example.familychat.adapter.RecentChatAdapter;
import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatRooms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatRecentFragment extends Fragment implements RecentChatAdapter.OnItemClickListener{
    RecyclerView recyclerView;
    RecentChatAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chat_recent, container, false);
        try {

            recyclerView = view.findViewById(R.id.recyler_view);
            setupRecyclerView();

        }catch (Exception e){
            System.out.println(e);
        }
        return view;
    }
    void setupRecyclerView(){
        List<ChatRooms> chatRoomsList = new ArrayList<>();
        for (Map.Entry<Integer, ChatRooms> entry : ChatManager.getAllChatRooms().entrySet()) {
            chatRoomsList.add(entry.getValue());
        }
        adapter = new RecentChatAdapter(getContext(), chatRoomsList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onItemClick(ChatRooms chatRoom) {
        Intent chatActivity = new Intent(getContext(), ChatActivity.class);
        chatActivity.putExtra("chat",chatRoom.chatId);
        startActivity(chatActivity);
    }
}
