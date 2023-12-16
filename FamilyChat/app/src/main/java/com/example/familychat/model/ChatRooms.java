package com.example.familychat.model;

import com.example.familychat.ChatActivity;
import com.example.familychat.ChatFragment;
import com.example.familychat.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatRooms {
    public UserContext User;
    public UserContext UserFriend;
    public ChatAdapter chatAdapter;
    public ChatFragment chatFragment;
    public ChatRooms(){

    }
    public ChatRooms(UserContext user,UserContext userFriend){
        this.User = user;
        this.UserFriend = userFriend;
    }
}
