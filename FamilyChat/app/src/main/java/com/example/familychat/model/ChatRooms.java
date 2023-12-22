package com.example.familychat.model;

import com.example.familychat.ChatActivity;
import com.example.familychat.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatRooms {
    public UserContext UserFriend;
    public ChatAdapter chatAdapter;
    public Integer chatId;
    public ChatRooms(){

    }
    public ChatRooms(UserContext userFriend){
        this.UserFriend = userFriend;
    }
}
