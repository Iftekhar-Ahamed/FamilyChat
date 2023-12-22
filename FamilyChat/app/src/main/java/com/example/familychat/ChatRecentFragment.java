package com.example.familychat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.adapter.RecentChatAdapter;
import com.example.familychat.model.ChatManager;

public class ChatRecentFragment extends Fragment {
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
        adapter = new RecentChatAdapter(getContext(), ChatManager.getAllChatRooms());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
