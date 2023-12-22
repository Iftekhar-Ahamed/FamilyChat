package com.example.familychat.utils;

import com.example.familychat.model.ChatMessage;

public class ChatMessageEvent {
    private final ChatMessage chatMessage;

    public ChatMessageEvent(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
    public ChatMessage getChatMessage() {
        return chatMessage;
    }
}
