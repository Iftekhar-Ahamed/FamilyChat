package com.example.familychat.model;

import android.content.Context;

import java.util.List;

public class ChatMessage {
    private String messageText;
    private boolean isSender;

    public ChatMessage(String messageText, boolean isSender) {
        this.messageText = messageText;
        this.isSender = isSender;
    }


    public String getMessageText() {
        return messageText;
    }

    public boolean isSender() {
        return isSender;
    }
}
