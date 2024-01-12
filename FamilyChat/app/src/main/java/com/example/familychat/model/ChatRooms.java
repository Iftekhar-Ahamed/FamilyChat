package com.example.familychat.model;

import com.example.familychat.adapter.ChatAdapter;
import com.example.familychat.adapter.RecentChatAdapter;

import java.time.LocalDateTime;

public class ChatRooms {
    public UserContext UserFriend;
    public ChatAdapter chatAdapter;
    public Integer chatId;
    public String lastMessageTime = "";
    public String lastMessageText = "Start a new message";
    public ChatRooms(){

    }
    public ChatRooms(UserContext userFriend){
        this.UserFriend = userFriend;
    }
}
