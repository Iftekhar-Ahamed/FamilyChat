package com.example.familychat.model;

import com.example.familychat.adapter.RecentChatAdapter;

import java.util.HashMap;
import java.util.Map;

public class ChatManager {
    private static Map<Integer, ChatRooms> chatRoomsHashMap = new HashMap<>();
    public RecentChatAdapter recentChatAdapter;


    private ChatManager() {
        // private constructor to prevent instantiation
    }

    public static void addChatRooms(Integer key, ChatRooms chatRooms) {
        chatRoomsHashMap.put(key, chatRooms);
    }


    public static ChatRooms getChatRooms(Integer key) {
        return chatRoomsHashMap.get(key);
    }

    public static Map<Integer, ChatRooms> getAllChatRooms() {
        return chatRoomsHashMap;
    }

    public static void clearAllChatRooms() {
        chatRoomsHashMap.clear();
    }
}
