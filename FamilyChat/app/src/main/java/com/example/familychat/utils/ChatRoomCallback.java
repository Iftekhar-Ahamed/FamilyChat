package com.example.familychat.utils;

import com.example.familychat.model.ChatRooms;

public interface ChatRoomCallback {
    void onChatRoomReceived(ChatRooms cr);
    void onChatRoomError(String errorMessage);
}
