package com.example.familychat.utils;

import com.example.familychat.model.ChatMessage;

public class NotificationEvent {
    public String title = new String();
    public String content = new String();
    public Integer chatId = 0;
    public NotificationEvent(String title,String content,Integer chatId) {
        this.title = title;
        this.content = content;
        this.chatId = chatId;
    }
}
