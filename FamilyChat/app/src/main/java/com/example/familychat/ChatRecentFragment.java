package com.example.familychat;
import java.util.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.ChatActivity;
import com.example.familychat.R;
import com.example.familychat.adapter.ChatAdapter;
import com.example.familychat.adapter.RecentChatAdapter;
import com.example.familychat.model.ChatManager;
import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRoomCallback;
import com.example.familychat.model.ChatRoomDto;
import com.example.familychat.model.ChatRooms;
import com.example.familychat.model.GetPreviousChatDto;
import com.example.familychat.model.ReciveMessageDto;
import com.example.familychat.model.UserContext;
import com.example.familychat.utils.API;
import com.example.familychat.utils.ChatMessageEvent;
import com.example.familychat.utils.ChatRoomEvent;
import com.example.familychat.utils.MyInformation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ChatRecentFragment extends Fragment implements RecentChatAdapter.OnItemClickListener{
    RecyclerView recyclerView;
    public RecentChatAdapter adapter;
    ChatRecentFragment(){

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_chat_recent, container, false);
        adapter = new RecentChatAdapter(getContext(), this);
        recyclerView = view.findViewById(R.id.recyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        setupChatRooms();
        return view;
    }
    @Override
    public void onItemClick(ChatRooms chatRoom) {
        Intent chatActivity = new Intent(getContext(), ChatActivity.class);
        chatActivity.putExtra("chat",chatRoom.chatId);
        startActivity(chatActivity);
    }
    public void addDataToAdapter(ChatRooms newData) {
        if (adapter != null) {
            adapter.addChatRoom(newData);
            adapter.notifyDataSetChanged();
        }
    }

    //region GetChatRoom
    private void setupChatRooms() {
        API<ChatRoomDto> apiConnectionList = new API<ChatRoomDto>(getContext());
        String connectionListUrl = "FamilyChat/GetAllConnectionByUserId" + "?id=" + MyInformation.data.userId;

        apiConnectionList.fetchDataList(connectionListUrl, ChatRoomDto.class, MyInformation.token, new API.UserCallback<List<ChatRoomDto>>() {
            @Override
            public void onUserReceived(List<ChatRoomDto> data) {
                processChatRooms(data, 0);
            }

            @Override
            public void onUserError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processChatRooms(List<ChatRoomDto> data, int index) {
        try {
            if (index < data.size()) {
                ChatRoomDto item = data.get(index);
                getChatRoom(item.chatFriendId,item.chatId, new ChatRoomCallback() {
                    @Override
                    public void onChatRoomReceived(ChatRooms chatRooms) {
                        chatRooms.chatId = item.chatId;
                        ChatManager.addChatRooms(item.chatId, chatRooms);
                        addDataToAdapter(chatRooms);
                        processChatRooms(data, index + 1); // Process the next item
                    }

                    @Override
                    public void onChatRoomError(String errorMessage) {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void getChatRoom(Integer chatFriendId, Integer chatId, ChatRoomCallback callback) {
        ChatRooms room = new ChatRooms();
        API<GetPreviousChatDto> userData = new API<GetPreviousChatDto>(getContext());
        String userDataUrl = "FamilyChat/GetAllMessageByChatId" + "?ChatId=" + chatId+ "&ChatFriendId="+chatFriendId;
        userData.fetchData(userDataUrl, GetPreviousChatDto.class, MyInformation.token, new API.UserCallback<GetPreviousChatDto>() {
            @Override
            public void onUserReceived(GetPreviousChatDto data) {
                room.UserFriend = data.userFriend;
                if(data.chatMessages.get(0).chatId!=0) {

                    //region ProcessResponse
                    List<ChatMessage> cmL = new ArrayList<ChatMessage>();
                    for (ReciveMessageDto item:data.chatMessages) {
                        item.isUser = (item.userId == MyInformation.data.userId);
                        cmL.add(item);
                    }
                    Collections.reverse(cmL);
                    //endregion

                    room.chatAdapter = new ChatAdapter(getContext(), cmL);
                }else {
                    room.chatAdapter = new ChatAdapter(getContext(), new ArrayList<ChatMessage>());
                }
                callback.onChatRoomReceived(room);
            }

            @Override
            public void onUserError(String errorMessage) {
                callback.onChatRoomError(errorMessage);
            }
        });
    }
    //endregion


}
