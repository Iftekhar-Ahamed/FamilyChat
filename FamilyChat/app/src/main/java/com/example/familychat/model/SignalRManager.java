package com.example.familychat.model;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.familychat.ChatActivity;
import com.example.familychat.Home;
import com.example.familychat.adapter.ChatAdapter;
import com.example.familychat.utils.ActiveUserDto;
import com.example.familychat.utils.ChatMessageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignalRManager {
    private static final String HUB_URL = "http://familychat.somee.com/notificationHub";
    private static HubConnection hubConnection;

    private SignalRManager() {
        // private constructor to prevent instantiation
    }

    public static void initialize(Context context,UserContext user) {
        if (hubConnection == null) {
            hubConnection = HubConnectionBuilder.create(HUB_URL).build();

            // Additional configuration or setup if needed

            // Start the connection
            hubConnection.start().blockingAwait();

            setupSignalR(user);


            // Handle connection events or send initial messages if necessary

            Toast.makeText(context, "SignalR connection initialized", Toast.LENGTH_SHORT).show();
        }
    }
    private static void setupSignalR(UserContext user) {
        try {
            hubConnection.send("SaveUserConnection", user.userId);
            hubConnection.send("NotifyOnConnectionIdUpdate", user.userId);
            hubConnection.on("broadcastMessage", (message) -> {

                    try {
                        ObjectMapper om = new ObjectMapper();
                        ChatMessage msg = om.readValue(message, ChatMessage.class);
                        ChatRooms chatRooms = ChatManager.getChatRooms(msg.chatId);
                        postChatMessageEvent(msg);
                    }catch (Exception e){
                        System.out.println(e);
                    }
            }, String.class);
            hubConnection.on("ReceivedPersonalNotification", (message) -> {

                try {
                    ObjectMapper om = new ObjectMapper();
                    ChatMessage msg = om.readValue(message, ChatMessage.class);
                    postChatMessageEvent(msg);
                }catch (Exception e){
                    System.out.println(e);
                }
            }, String.class);
            hubConnection.on("ActiveUser", (message) -> {
                try {
                    ObjectMapper om = new ObjectMapper();
                    ActiveUserDto u = om.readValue(message, ActiveUserDto.class);
                    ChatManager.getChatRooms(u.chatId).UserFriend.connectionId = u.connectionId;
                }catch (Exception e){
                    System.out.println(e.toString());
                }
            }, String.class);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    private static void postChatMessageEvent(ChatMessage chatMessage) {
        try {
            EventBus.getDefault().post(new ChatMessageEvent(chatMessage));
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public static HubConnection getHubConnection() {
        return hubConnection;
    }

    public static void stopConnection() {
        if (hubConnection != null) {
            hubConnection.stop();
            hubConnection = null;
        }
    }
}
