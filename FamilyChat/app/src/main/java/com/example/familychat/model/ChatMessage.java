package com.example.familychat.model;

public class ChatMessage extends UserContext {
    public String messageText;
    public Integer chatId;
    public ChatMessage(UserContext user,String messageText){
        super(user);
        this.messageText = messageText;
    }
    public ChatMessage(){

    }
}
