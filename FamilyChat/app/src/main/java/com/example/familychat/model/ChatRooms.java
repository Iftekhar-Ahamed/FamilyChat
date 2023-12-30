package com.example.familychat.model;

import com.example.familychat.adapter.ChatAdapter;

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
