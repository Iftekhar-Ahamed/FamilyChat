package com.example.familychat.utils;

import com.example.familychat.model.ChatMessage;
import com.example.familychat.model.ChatRooms;

public class ChatRoomEvent {
    private final ChatRooms chatRooms;

    public ChatRoomEvent(ChatRooms chatRooms) {
        this.chatRooms = chatRooms;
    }
    public ChatRooms getChatRoom() {
        return chatRooms;
    }
}
