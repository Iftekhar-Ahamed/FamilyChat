package com.example.familychat.model;

import android.content.Context;
import android.widget.Toast;

import com.example.familychat.Home;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

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
                        if(chatRooms.chatAdapter!=null){
                            chatRooms.chatAdapter.addMessage(msg);
                        }
                    }catch (Exception e){
                        System.out.println(e);
                    }
            }, String.class);

            hubConnection.on("ActiveUser", (message) -> {
                try {
                    ObjectMapper om = new ObjectMapper();
                    UserContext u = om.readValue(message, UserContext.class);
                }catch (Exception e){
                    System.out.println(e.toString());
                }
            }, String.class);
        } catch (Exception e) {
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
