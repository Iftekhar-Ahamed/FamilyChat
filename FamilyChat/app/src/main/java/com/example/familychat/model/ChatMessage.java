package com.example.familychat.model;

import java.time.LocalDateTime;

public class ChatMessage extends UserContext {
    public String messageText;
    public Integer chatId;
    public String messageDateTime;
    public ChatMessage(UserContext user,String messageText){
        super(user);
        this.messageText = messageText;
    }
    public ChatMessage(){

    }
}
