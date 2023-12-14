package com.example.familychat.model;

import android.content.Context;

import java.util.List;

public class ChatMessage extends UserContext {
    public String messageText;
    public ChatMessage(UserContext user,String messageText){
        super(user);
        this.messageText = messageText;
    }
    public ChatMessage(){

    }
}
